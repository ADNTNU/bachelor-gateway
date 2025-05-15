package no.ntnu.gr10.bachelorgateway.authentication;

import no.ntnu.gr10.bachelorgateway.authentication.dto.AuthenticationRequest;
import no.ntnu.gr10.bachelorgateway.authentication.dto.AuthenticationResponse;
import no.ntnu.gr10.bachelorgateway.dto.ErrorResponse;
import no.ntnu.gr10.bachelorgateway.security.CustomUserDetails;
import no.ntnu.gr10.bachelorgateway.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


/**
 * Controller for handling authentication-related requests.
 * Include method for login.
 *
 * @author Anders Lund and Daniel Neset
 * @version 05.04.2025
 */
@RestController
@RequestMapping("/auth")
public class AuthRestController {

  private static final Logger log = LoggerFactory.getLogger(AuthRestController.class);
  private final JwtUtil jwtUtil;
  private final ReactiveAuthenticationManager authenticationManager;


  /**
   * Constructor for AuthController.
   *
   * @param jwtUtil               The JWT token provider
   * @param authenticationManager The authentication manager
   */
  public AuthRestController(JwtUtil jwtUtil, ReactiveAuthenticationManager authenticationManager) {
    this.jwtUtil = jwtUtil;
    this.authenticationManager = authenticationManager;
  }


  /**
   * Authenticates the user and returns a JWT token if successful.
   *
   * <p>This method handles the login request by validating the provided username and password.
   * If the credentials are valid, it generates a JWT token and returns it in the response.
   * If the credentials are invalid, it returns an unauthorized response.
   * </p>
   *
   * @param authenticationRequest the login request containing username and password
   */
  @PostMapping
  public Mono<ResponseEntity<Object>> authenticate(
          @RequestBody AuthenticationRequest authenticationRequest
  ) {
    return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getId(),
                            authenticationRequest.getSecret()
                    )
            )
            .map(auth -> {
              AuthenticationResponse response = authenticateAndGenerateResponse(auth);
              return ResponseEntity.ok().body((Object) response);
            })
            .onErrorResume(BadCredentialsException.class, e ->
                    Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new ErrorResponse("Invalid username or password")))
            )
            .onErrorResume(DisabledException.class, e ->
                    Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new ErrorResponse("User has been disabled")))
            )
            .onErrorResume(e -> {
                      log.error("Authentication error: {}", e.getMessage());
                      return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                              .body(new ErrorResponse("An error occurred during authentication")));
                    }
            );
  }

  private AuthenticationResponse authenticateAndGenerateResponse(Authentication authentication) {
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtil.generateToken(authentication);
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    return new AuthenticationResponse(
            jwt,
            userDetails.getUsername(),
            userDetails.getCompanyId(),
            userDetails.getAuthorities()
    );
  }
}
