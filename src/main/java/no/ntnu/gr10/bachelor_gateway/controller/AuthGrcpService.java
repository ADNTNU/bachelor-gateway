package no.ntnu.gr10.bachelor_gateway.controller;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import no.ntnu.gr10.bachelor_gateway.auth.AuthGrpc;
import no.ntnu.gr10.bachelor_gateway.auth.AuthProto;
import no.ntnu.gr10.bachelor_gateway.security.CustomUserDetails;
import no.ntnu.gr10.bachelor_gateway.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Map;

@GrpcService
public class AuthGrcpService extends AuthGrpc.AuthImplBase {
  private final AuthenticationManager authManager;
  private final JwtUtil jwtUtil;

  @Autowired
  public AuthGrcpService(AuthenticationManager authManager, JwtUtil jwtUtil) {
    this.authManager = authManager;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public void authenticate(AuthProto.AuthRequest req,
                           StreamObserver<AuthProto.AuthResponse> resp) {
    try {
      Authentication auth = authManager.authenticate(
              new UsernamePasswordAuthenticationToken(req.getId(), req.getSecret())
      );

      CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

      // TODO refactor
      Map<String,Object> claims = Map.of(
              "companyId",     user.getCompanyId(),
              "scopes", user.getAuthorities()
                      .stream()
                      .map(a -> Map.of("authority", a.getAuthority()))
                      .toList()
      );

      String token = jwtUtil.generateToken(user.getUsername(), claims);

      resp.onNext(
              AuthProto.AuthResponse.newBuilder()
                      .setToken(token)
                      .setUsername(user.getUsername())
                      .setCompanyId(user.getCompanyId())
                      .build()
      );
      resp.onCompleted();

    } catch (BadCredentialsException e) {
      resp.onError(Status.UNAUTHENTICATED
              .withDescription("Invalid id or secret")
              .asRuntimeException());
    } catch (DisabledException e) {
      resp.onError(Status.PERMISSION_DENIED
              .withDescription("User account disabled")
              .asRuntimeException());
    } catch (Exception e) {
      resp.onError(Status.INTERNAL
              .withDescription("Authentication error: " + e.getMessage())
              .asRuntimeException());
    }
  }
}
