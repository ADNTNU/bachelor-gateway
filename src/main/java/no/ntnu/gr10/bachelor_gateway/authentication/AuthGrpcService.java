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

@GrpcService
public class AuthGrpcService extends AuthGrpc.AuthImplBase {
  private final ReactiveAuthenticationManager authManager;
  private final JwtUtil jwtUtil;

  @Autowired
  public AuthGrpcService(ReactiveAuthenticationManager authManager, JwtUtil jwtUtil) {
    this.authManager = authManager;
    this.jwtUtil = jwtUtil;
  }

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
