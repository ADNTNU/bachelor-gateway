package no.ntnu.gr10.bachelor_gateway.authentication;

import no.ntnu.gr10.bachelor_gateway.authentication.dto.AuthenticationRequest;
import no.ntnu.gr10.bachelor_gateway.authentication.dto.AuthenticationResponse;
import no.ntnu.gr10.bachelor_gateway.dto.ErrorResponse;
import no.ntnu.gr10.bachelor_gateway.security.CustomUserDetails;
import no.ntnu.gr10.bachelor_gateway.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
  private final AuthenticationManager authenticationManager;

  /**
   * Constructor for AuthController
   *
   * @param jwtUtil The JWT token provider
   * @param authenticationManager The authentication manager
   */
  public AuthRestController(JwtUtil jwtUtil, AuthenticationManager authenticationManager){
    this.jwtUtil = jwtUtil;
    this.authenticationManager = authenticationManager;
  }

  /**
   * Authenticates the user and returns a JWT token if successful.
   * <p>
   *   This method handles the login request by validating the provided username and password.
   *   If the credentials are valid, it generates a JWT token and returns it in the response.
   *   If the credentials are invalid, it returns an unauthorized response.
   *   </p>
   *
   *   @param authenticationRequest the login request containing username and password
   */
  @PostMapping
  public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest authenticationRequest){
    ResponseEntity<?> response;

    try{
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      authenticationRequest.id(),
                      authenticationRequest.secret()
              )
      );
      AuthenticationResponse authenticationResponse = authenticateAndGenerateResponse(authentication);
      response = ResponseEntity.ok(authenticationResponse);

    } catch (BadCredentialsException badCredentialsException) {
      response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid username or password"));
    }  catch (DisabledException e){
      log.warn("User tried logged in with invalid credentials");
      response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("User has been disabled"));
    }catch (Exception e) {
      log.warn("User tried logging in with an disabled credentials");
      response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred during authentication"));
    }

    return response;
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
