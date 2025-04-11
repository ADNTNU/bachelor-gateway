package no.ntnu.gr10.bachelor_gateway.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;


  public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService){
    this.jwtUtil = jwtUtil;
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
      if(jwtUtil.validateToken(jwtToken, userDetails)) {
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
      username = jwtUtil.extractUsername(jwtToken);
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
