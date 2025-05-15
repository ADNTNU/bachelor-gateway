package no.ntnu.gr10.bachelorgateway.security.grpc;

import io.grpc.BindableService;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.Status;
import io.jsonwebtoken.JwtException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import no.ntnu.gr10.bachelorgateway.auth.AuthGrpc;
import no.ntnu.gr10.bachelorgateway.security.CustomReactiveUserDetailsService;
import no.ntnu.gr10.bachelorgateway.security.CustomUserDetails;
import no.ntnu.gr10.bachelorgateway.security.JwtUtil;
import no.ntnu.gr10.bachelorgateway.security.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;



/**
 * gRPC server interceptor responsible for gateway-level authentication and authorization.
 *
 * <p>This interceptor performs the following steps for each incoming call (except for
 * authentication, reflection, and health endpoints):
 * <ol>
 *   <li>Extract and validate the JWT from the "Authorization" metadata header.</li>
 *   <li>Retrieve the username, company ID, and scopes from the token via {@link JwtUtil}.</li>
 *   <li>Load user details using {@link CustomReactiveUserDetailsService}
 *   and ensure the account is enabled.</li>
 *   <li>Populate the gRPC {@link Context} with
 *   authentication metadata (company ID and scopes).</li>
 *   <li>Enforce method-level scope checks based on {@link ScopesAllowed} annotations.</li>
 * </ol>
 * </p>
 *
 * @author Daniel Neset
 * @version 15.05.2025
 */
@Component
@GrpcGlobalServerInterceptor
public class JwtAuthInterceptor implements ServerInterceptor {


  private final JwtUtil jwtUtil;
  private final CustomReactiveUserDetailsService userDetailsService;
  private final Map<String, BindableService> serviceBeans;


  /**
   * Constructs a JwtAuthInterceptor with the necessary components.
   *
   * @param jwtUtil The utility for verifying JWTs and extracting claims
   * @param userDetailsService The reactive service to load user details by username
   * @param ctx The Spring application context for discovering gRPC service beans
   */
  @Autowired
  public JwtAuthInterceptor(
          JwtUtil jwtUtil,
          CustomReactiveUserDetailsService userDetailsService,
          ApplicationContext ctx
  ) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;

    this.serviceBeans = ctx
            .getBeansOfType(BindableService.class)
            .values()
            .stream()
            .map(bs -> {
              ServerServiceDefinition def = bs.bindService();
              return def == null || def.getServiceDescriptor() == null
                      ? null
                      : Map.entry(def.getServiceDescriptor().getName(), bs);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (first, second) -> first
            ));
  }


  /**
   * Intercepts incoming gRPC calls to perform authentication and authorization.
   *
   * @param call The server call being invoked
   * @param headers The incoming metadata headers containing the authorization token
   * @param next The handler to invoke upon successful auth checks
   * @param <ReqT> The type of the request message
   * @param <ResT> The type of the response message
   * @return Return a listener for the request stream, or a no-op listener on auth failure
   */
  @Override
  public <ReqT, ResT> ServerCall.Listener<ReqT> interceptCall(
          ServerCall<ReqT, ResT> call,
          Metadata headers,
          ServerCallHandler<ReqT, ResT> next
  ) {
    String fullMethod = call.getMethodDescriptor().getFullMethodName();
    if (fullMethod.equals(AuthGrpc.SERVICE_NAME + "/Authenticate")
            || fullMethod.equals("grpc.reflection.v1alpha.ServerReflection/ServerReflectionInfo")
            || fullMethod.equals("grpc.health.v1.Health/Check")
            || fullMethod.equals("grpc.health.v1.Health/Watch")) {
      return next.startCall(call, headers);
    }

    String raw = headers.get(SecurityContext.AUTH_HEADER);
    if (raw == null || !raw.startsWith("Bearer ")) {
      call.close(Status.UNAUTHENTICATED.withDescription("Missing token"), new Metadata());
      return new ServerCall.Listener<>() {};
    }
    String token = raw.substring(7);

    String username;
    try {
      username = jwtUtil.verifyTokenAndGetUsername(token);
    } catch (JwtException | IllegalArgumentException e) {
      call.close(Status.UNAUTHENTICATED.withDescription("Invalid token"), new Metadata());
      return new ServerCall.Listener<>() {};
    }

    CustomUserDetails user;
    try {
      user = (CustomUserDetails) userDetailsService
              .findByUsername(username)
              .block(Duration.ofSeconds(1));
    } catch (UsernameNotFoundException e) {
      call.close(Status.UNAUTHENTICATED.withDescription("User not found"), new Metadata());
      return new ServerCall.Listener<>() {};
    }
    if (user == null || !user.isEnabled()) {
      call.close(Status.PERMISSION_DENIED.withDescription("User account disabled"), new Metadata());
      return new ServerCall.Listener<>() {};
    }

    Long companyId = jwtUtil.verifyTokenAndGetCompanyId(token).longValue();
    List<String> scopes  = jwtUtil.verifyTokenAndGetScopes(token);

    Context ctx = Context.current()
            .withValue(SecurityContext.CURRENT_METADATA, headers)
            .withValue(SecurityContext.COMPANY_ID_CTX_KEY, companyId)
            .withValue(SecurityContext.AUTHORITIES_CTX_KEY, scopes);

    String serviceName = call.getMethodDescriptor().getServiceName();
    String rpcMethod   = call.getMethodDescriptor().getBareMethodName();
    String javaMethod  = Character.toLowerCase(rpcMethod.charAt(0))
            + rpcMethod.substring(1);

    BindableService svcBean = serviceBeans.get(serviceName);
    if (svcBean != null) {
      Method target = Arrays.stream(svcBean.getClass().getMethods())
              .filter(m -> m.getName().equals(javaMethod))
              .findFirst()
              .orElse(null);

      if (target != null && target.isAnnotationPresent(ScopesAllowed.class)) {
        ScopesAllowed ann = target.getAnnotation(ScopesAllowed.class);
        Set<String> required = Arrays.stream(ann.value())
                .map(Scope::getAuthority)
                .collect(Collectors.toSet());
        if (!scopes.containsAll(required)) {
          call.close(Status.PERMISSION_DENIED.withDescription("Not authorized"), new Metadata());
          return new ServerCall.Listener<>() {};
        }
      }
    }

    return Contexts.interceptCall(ctx, call, headers, next);
  }
}

