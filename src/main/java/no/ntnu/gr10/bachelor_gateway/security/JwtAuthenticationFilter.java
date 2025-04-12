package no.ntnu.gr10.bachelor_gateway.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    String jwtToken = getJwtFromRequest(httpServletRequest);

    try {
      if (jwtToken != null) {
        String username = jwtUtil.verifyTokenAndGetUsername(jwtToken);
        //TODO This can be removed? Token is validated. Needs testing
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        registerUserAsAuthenticated(httpServletRequest, userDetails);
      }
    } catch (JwtException | IllegalArgumentException ex) {
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
