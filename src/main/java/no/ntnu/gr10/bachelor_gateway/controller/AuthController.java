package no.ntnu.gr10.bachelor_gateway.controller;

import no.ntnu.gr10.bachelor_gateway.company.Company;
import no.ntnu.gr10.bachelor_gateway.security.AccessUserDetails;
import no.ntnu.gr10.bachelor_gateway.security.AccessUserService;
import no.ntnu.gr10.bachelor_gateway.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Controller for handling authentication-related requests.
 * Include method for login.
 *
 * @author Anders Lund and Daniel Neset
 * @version 05.04.2025
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;
  private final AccessUserService accessUserService;

  /**
   * Constructor for AuthController
   *
   * @param accessUserService The ApiKey service
   * @param jwtUtil The JWT token provider
   * @param authenticationManager The authentication manager
   */
  public AuthController(AccessUserService accessUserService, JwtUtil jwtUtil, AuthenticationManager authenticationManager){
    this.accessUserService = accessUserService;
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
  private ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest){

    try{
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      authenticationRequest.id,
                      authenticationRequest.secret
              )
      );
      AuthenticationResponse authenticationResponse = authenticateAndGenerateResponse(authentication);
      return ResponseEntity.ok(authenticationResponse);

    } catch (BadCredentialsException badCredentialsException) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    } catch (Exception e) {
      System.out.println(e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during authentication");
    }

  }

  private AuthenticationResponse authenticateAndGenerateResponse(Authentication authentication) {
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtil.generateToken(authentication);
    AccessUserDetails userDetails = (AccessUserDetails) authentication.getPrincipal();

    return new AuthenticationResponse(
            jwt,
            userDetails.getUsername(),
            userDetails.getCompanyId(),
            userDetails.getAuthorities()
    );
  }

  record AuthenticationRequest(String id, String secret) {}
  record AuthenticationResponse(String token, String username, int company, Collection authorities) {}

}
