package no.ntnu.gr10.bachelor_gateway.security.grpc;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.jsonwebtoken.JwtException;
import no.ntnu.gr10.bachelor_gateway.auth.AuthGrpc;
import no.ntnu.gr10.bachelor_gateway.security.CustomReactiveUserDetailsService;
import no.ntnu.gr10.bachelor_gateway.security.CustomUserDetails;
import no.ntnu.gr10.bachelor_gateway.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@GlobalServerInterceptor
public class JwtAuthInterceptor implements ServerInterceptor {


  private final JwtUtil jwtUtil;
  private final CustomReactiveUserDetailsService userDetailsService;

  @Autowired
  public JwtAuthInterceptor(
          JwtUtil jwtUtil,
          CustomReactiveUserDetailsService userDetailsService
  ) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  @Override
  public <ReqT, ResT> ServerCall.Listener<ReqT> interceptCall(
          ServerCall<ReqT, ResT> call,
          Metadata headers,
          ServerCallHandler<ReqT, ResT> next
  ) {
    Context ctx = Context.current()
            .withValue(SecurityContext.CURRENT_METADATA, headers);

    String fullMethod = call.getMethodDescriptor().getFullMethodName();
    String loginMethod = AuthGrpc.SERVICE_NAME + "/Authenticate";
    if (fullMethod.equals(loginMethod)) {
      return Contexts.interceptCall(ctx, call, headers, next);
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

    // TODO, check if the user has access to the grpc api before sending data over there

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

    return Contexts.interceptCall(ctx, call, headers, next);
  }
}

