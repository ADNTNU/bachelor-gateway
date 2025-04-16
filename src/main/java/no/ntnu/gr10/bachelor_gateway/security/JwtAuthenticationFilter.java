package no.ntnu.gr10.bachelor_gateway.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter.
 * <p>
 * Checks for the presence of a JWT token in the request header, validates it,
 * and sets the authentication in the security context.
 * </p>
 *
 * @author Daniel Neset
 * @version 11.04.2025
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  /**
   * Constructs a new JwtAuthenticationFilter with the provided JwtUtil.
   *
   * @param jwtUtil the utility class used for JWT parsing and validation
   */
  public JwtAuthenticationFilter(JwtUtil jwtUtil){
    this.jwtUtil = jwtUtil;
  }

  /**
   * Filters incoming requests to process the JWT token if present.
   * <p>
   * If a valid JWT is found, it extracts the user details from it and registers
   * the authentication in the SecurityContext.
   * </p>
   *
   * @param httpServletRequest  the incoming HTTP request
   * @param httpServletResponse the HTTP response
   * @param filterChain         the filter chain
   * @throws IOException      if an input/output exception occurs
   * @throws ServletException if a servlet error occurs
   */
  @Override
  protected void doFilterInternal(
          HttpServletRequest httpServletRequest,
          HttpServletResponse httpServletResponse,
          FilterChain filterChain
  )throws IOException, ServletException {

    String jwtToken = getJwtFromRequest(httpServletRequest);
    try {
      if (jwtToken != null) {
         UserDetails userDetails1 = jwtUtil.verifyTokenAndGetUserDetails(jwtToken);

        registerUserAsAuthenticated(httpServletRequest, userDetails1);
      }
    } catch (JwtException | IllegalArgumentException ex) {
      System.out.println(ex);
      httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
      httpServletResponse.getWriter().write("Invalid JWT token");
      return;
    } catch (UsernameNotFoundException ex) {
      httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
      httpServletResponse.getWriter().write("User not found");
      return;
    }

    filterChain.doFilter(httpServletRequest, httpServletResponse);
  }


  private String getJwtFromRequest(HttpServletRequest request) {
    final String BEARER_PREFIX = "Bearer ";
    String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length()); // Remove "Bearer " prefix
    }
    return null;
  }


  private static void registerUserAsAuthenticated(HttpServletRequest request, UserDetails userDetails) {
    final UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(upat);
  }

}
