package no.ntnu.gr10.bachelor_gateway.security.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import no.ntnu.gr10.bachelor_gateway.dto.ErrorResponse;
import no.ntnu.gr10.bachelor_gateway.exception.UserIsDisabled;
import no.ntnu.gr10.bachelor_gateway.security.CustomUserDetailsService;
import no.ntnu.gr10.bachelor_gateway.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private final CustomUserDetailsService customUserDetailsService;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  /**
   * Constructs a new JwtAuthenticationFilter with the provided JwtUtil.
   *
   * @param jwtUtil the utility class used for JWT parsing and validation
   */
  public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService){
    this.jwtUtil = jwtUtil;
    this.customUserDetailsService = customUserDetailsService;
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
    try {
      String jwtToken = getJwtFromRequest(httpServletRequest);

      if (jwtToken != null) {
        String clientId = jwtUtil.verifyTokenAndGetUsername(jwtToken);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(clientId);

        if(!userDetails.isEnabled()){
          throw new UserIsDisabled("Account has been disabled");
        }

        registerUserAsAuthenticated(httpServletRequest, userDetails);
      }
      filterChain.doFilter(httpServletRequest, httpServletResponse);

    } catch (JwtException | IllegalArgumentException ex) {
      writeJsonError(httpServletResponse, HttpStatus.UNAUTHORIZED, "Invalid JWT token");
    } catch (UsernameNotFoundException ex) {
      writeJsonError(httpServletResponse, HttpStatus.NOT_FOUND, "User not found");
    }catch (UserIsDisabled ex) {
      writeJsonError(httpServletResponse, HttpStatus.UNAUTHORIZED, "User has been deactivated");
    }
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
    final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
  }

  private void writeJsonError(HttpServletResponse response, HttpStatus status, String message) {
    try {
      response.setContentType("application/json");
      response.setStatus(status.value());

      ErrorResponse errorResponse = new ErrorResponse(message);
      String json = objectMapper.writeValueAsString(errorResponse);

      response.getWriter().write(json);
    } catch (Exception e) {
      log.error("Error writing JSON error response: {}. Original error: {}", e.getMessage(), message);
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      try {
        response.getWriter().write("Internal server error");
      } catch (IOException ioException) {
        log.error("Error writing internal server error response: {}", ioException.getMessage());
      }
    }
  }

}
