package no.ntnu.gr10.bachelor_gateway.authentication;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import no.ntnu.gr10.bachelor_gateway.auth.AuthGrpc;
import no.ntnu.gr10.bachelor_gateway.auth.AuthProto;
import no.ntnu.gr10.bachelor_gateway.security.CustomUserDetails;
import no.ntnu.gr10.bachelor_gateway.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Map;

/**
 * gRPC service for handling authentication requests in the gateway.
 * <p>
 * Provides the <code>Authenticate</code> RPC method, which validates user credentials
 * via the <code>ReactiveAuthenticationManager</code>, generates a JWT containing
 * user-specific claims (company ID and scopes), and returns it along with user metadata.
 * </p>
 *
 * @author Daniel Neset
 * @version 14.05.2025
 */
@GrpcService
public class AuthGrpcService extends AuthGrpc.AuthImplBase {
  private final ReactiveAuthenticationManager authManager;
  private final JwtUtil jwtUtil;


  /**
   * Constructs the AuthGrpcService with its required dependencies.
   *
   * @param authManager The reactive authentication manager used to validate credentials
   * @param jwtUtil The utility for creating JWTs with custom claims
   */
  @Autowired
  public AuthGrpcService(ReactiveAuthenticationManager authManager, JwtUtil jwtUtil) {
    this.authManager = authManager;
    this.jwtUtil = jwtUtil;
  }


  /**
   * Handles the <code>Authenticate</code> RPC.
   * <p>
   * Takes an <code>AuthRequest</code> containing an ID and secret, authenticates the user,
   * generates a JWT with the user's company ID and granted scopes, and returns an
   * <code>AuthResponse</code> with the token, company ID, and roles. Emits appropriate
   * gRPC status codes on authentication failure or errors.
   * </p>
   *
   * @param req The authentication request with user credentials
   * @param resp The stream observer used to send back the authentication response or error
   */
  @Override
  public void authenticate(AuthProto.AuthRequest req, StreamObserver<AuthProto.AuthResponse> resp) {
    UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(req.getId(), req.getSecret());

    authManager.authenticate(token)
            .map(auth -> (CustomUserDetails) auth.getPrincipal())
            .map(user -> {
              Map<String, Object> claims = Map.of(
                      "companyId", user.getCompanyId(),
                      "scopes", user.getAuthorities().stream()
                              .map(GrantedAuthority::getAuthority)
                              .toList()
              );
              String jwt = jwtUtil.generateToken(user.getUsername(), claims);

              return AuthProto.AuthResponse.newBuilder()
                      .setToken(jwt)
                      .setCompanyId(user.getCompanyId())
                      .addAllRoles(user.getAuthorities().stream()
                              .map(GrantedAuthority::getAuthority)
                              .toList())
                      .build();
            })
            .subscribe(
                    resp::onNext,
                    ex -> {
                      if (ex instanceof org.springframework.security.authentication.BadCredentialsException) {
                        resp.onError(Status.UNAUTHENTICATED
                                .withDescription("Invalid credentials")
                                .asRuntimeException());
                      } else if (ex instanceof org.springframework.security.authentication.DisabledException) {
                        resp.onError(Status.PERMISSION_DENIED
                                .withDescription("User disabled")
                                .asRuntimeException());
                      } else {
                        resp.onError(Status.INTERNAL
                                .withDescription("Unexpected error: " + ex.getMessage())
                                .asRuntimeException());
                      }
                    },
                    resp::onCompleted
            );
  }
}
