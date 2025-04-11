package no.ntnu.gr10.bachelor_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import no.ntnu.gr10.bachelor_gateway.entity.Client;
import no.ntnu.gr10.bachelor_gateway.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;


  public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService){
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
          HttpServletRequest httpServletRequest,
          HttpServletResponse httpServletResponse,
          FilterChain filterChain
  )throws IOException, ServletException {

    String jwtToken = getJwtToken(httpServletRequest);
    String username = jwtToken != null ? getUsernameFrom(jwtToken) : null;

    if(username != null && notAuthenticatedYet()){
      UserDetails userDetails = getUserDetailsFromDatabase(username);
      if(jwtService.validateToken(jwtToken, userDetails)) {
        registerUserAsAuthenticated(httpServletRequest, userDetails);
      }
    }

    filterChain.doFilter(httpServletRequest, httpServletResponse);
  }

  private UserDetails getUserDetailsFromDatabase(String username) {
    UserDetails userDetails = null;
    try {
      userDetails = userDetailsService.loadUserByUsername(username);
    } catch (UsernameNotFoundException usernameNotFoundException) {
      logger.warn("User: " + username + " Not found in the database.");
    }
    return userDetails;
  }

  private List<GrantedAuthority> buildAuthorities(Object scopesObj) {
    if(scopesObj instanceof List){
      List<String> scopes = (List<String>)  scopesObj;
      return scopes.stream()
              .map(scope -> (GrantedAuthority) () -> "SCOPE_" + scope.trim())
              .collect(Collectors.toList());
    }else if (scopesObj instanceof String){
      String[] scopes = ((String) scopesObj).split(",");
      return Arrays.stream(scopes)
              .map(scope -> (GrantedAuthority) () -> "SCOPE_" + scope.trim())
              .collect(Collectors.toList());
    }
    return List.of();
  }

  private String getJwtToken(HttpServletRequest httpServletRequest){
    final String authorizationHeader = httpServletRequest.getHeader("Authorization");
    String jwt = null;

    if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
      jwt = stripBearerPrefixFrom(authorizationHeader);
    }

    return jwt;
  }

  private boolean notAuthenticatedYet() {
    return SecurityContextHolder.getContext().getAuthentication() == null;
  }

  private static String stripBearerPrefixFrom(String authorizationHeaderValue) {
    final int numberOfCharsToStrip = "Bearer ".length();
    return authorizationHeaderValue.substring(numberOfCharsToStrip);
  }

  private String getUsernameFrom(String jwtToken) {
    String username = null;
    try {
      username = jwtService.extractUsername(jwtToken);
    } catch (MalformedJwtException malformedJwtException) {
      logger.warn("Malformed JWT: " + malformedJwtException.getMessage());
    } catch (JwtException jwtException) {
      logger.warn("Error in JWT token: " + jwtException.getMessage());
    }
    return username;
  }

  private static void registerUserAsAuthenticated(HttpServletRequest request, UserDetails userDetails) {
    final UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(upat);
  }

}
