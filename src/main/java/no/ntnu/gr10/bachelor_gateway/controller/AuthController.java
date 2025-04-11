package no.ntnu.gr10.bachelor_gateway.controller;

import no.ntnu.gr10.bachelor_gateway.security.AccessUserService;
import no.ntnu.gr10.bachelor_gateway.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;
  private final AccessUserService accessUserService;

  public AuthController(AccessUserService accessUserService, JwtUtil jwtUtil, AuthenticationManager authenticationManager){
    this.accessUserService = accessUserService;
    this.jwtUtil = jwtUtil;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping
  private ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest){
    try{
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
              authenticationRequest.id(),
              authenticationRequest.secret()
      ));
    }catch (BadCredentialsException badCredentialsException) {
      return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
    }
    final UserDetails userDetails = accessUserService.loadUserByUsername(
            authenticationRequest.id()
    );
    final String jwt = jwtUtil.generateToken(userDetails);
    return ResponseEntity.ok(new AuthenticationResponse(jwt));
  }


  record AuthenticationRequest(String id, String secret) {}
  record AuthenticationResponse(String token) {}

}
