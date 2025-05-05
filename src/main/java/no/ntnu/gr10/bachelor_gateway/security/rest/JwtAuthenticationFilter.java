package no.ntnu.gr10.bachelor_gateway.security.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import no.ntnu.gr10.bachelor_gateway.dto.ErrorResponse;
import no.ntnu.gr10.bachelor_gateway.exception.UserIsDisabled;
import no.ntnu.gr10.bachelor_gateway.security.CustomReactiveUserDetailsService;
import no.ntnu.gr10.bachelor_gateway.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
@Order(-100)
public class JwtAuthenticationFilter implements WebFilter {

  private final JwtUtil jwtUtil;
  private final CustomReactiveUserDetailsService customUserDetailsService;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  /**
   * Constructs a new JwtAuthenticationFilter with the provided JwtUtil.
   *
   * @param jwtUtil the utility class used for JWT parsing and validation
   */
  public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomReactiveUserDetailsService customUserDetailsService){
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
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String token = getJwtFromRequest(exchange);

    if (token == null) {
      return chain.filter(exchange);
    }

    try {
      String username = jwtUtil.verifyTokenAndGetUsername(token);
      return Mono.defer(() -> customUserDetailsService.findByUsername(username)
                      .flatMap(userDetails -> {
                        if (!userDetails.isEnabled()) {
                          return writeJsonError(exchange, HttpStatus.UNAUTHORIZED, "User has been deactivated");
                        }
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                      }))
              .onErrorResume(UsernameNotFoundException.class,
                      e -> writeJsonError(exchange, HttpStatus.NOT_FOUND, "User not found"))
              .onErrorResume(UserIsDisabled.class,
                      e -> writeJsonError(exchange, HttpStatus.UNAUTHORIZED, "User has been deactivated"));
    } catch (JwtException | IllegalArgumentException e) {
      return writeJsonError(exchange, HttpStatus.UNAUTHORIZED, "Invalid JWT token");
    }
  }


  private String getJwtFromRequest(ServerWebExchange exchange) {
    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    return null;
  }

  private Mono<Void> writeJsonError(ServerWebExchange exchange, HttpStatus status, String message) {
    exchange.getResponse().setStatusCode(status);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
    byte[] bytes;
    try {
      ErrorResponse errorResponse = new ErrorResponse(message);
      bytes = objectMapper.writeValueAsString(errorResponse).getBytes(StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.error("Error serializing JSON error response: {}", e.getMessage());
      bytes = "{\"message\":\"Internal server error\"}".getBytes(StandardCharsets.UTF_8);
      exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
            .bufferFactory().wrap(bytes)));
  }
}
