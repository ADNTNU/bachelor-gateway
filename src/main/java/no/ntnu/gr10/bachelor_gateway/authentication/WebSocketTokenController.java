package no.ntnu.gr10.bachelor_gateway.authentication;

import no.ntnu.gr10.bachelor_gateway.dto.ErrorResponse;
import no.ntnu.gr10.bachelor_gateway.security.JwtUtil;
import no.ntnu.gr10.bachelor_gateway.security.websocket.WebSocketSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * REST controller that issues WebSocket authentication tokens based on an incoming JWT.
 * <p>
 * This endpoint validates the provided bearer token, extracts the user's company ID
 * and scopes, and delegates to the {@link WebSocketSessionService} to generate
 * and store a WebSocket-specific token. Returns the token in JSON or an error status.
 * </p>
 *
 * @author Anders Lund
 * @version 05.05.2025
 */
@RestController
@RequestMapping("/ws-auth-token")
public class WebSocketTokenController {

  private final Logger logger = Logger.getLogger(getClass().getName());
  private final JwtUtil jwtUtil;
  private final WebSocketSessionService webSocketSessionService;
  private static final String BEARER_PREFIX = "Bearer ";


  /**
   * Constructs a new controller for issuing WebSocket auth tokens.
   *
   * @param jwtUtil The utility for verifying JWTs and extracting claims
   * @param webSocketSessionService The service for generating and storing WS tokens
   */
  public WebSocketTokenController(JwtUtil jwtUtil, WebSocketSessionService webSocketSessionService) {
    this.jwtUtil = jwtUtil;
    this.webSocketSessionService = webSocketSessionService;
  }


  /**
   * HTTP GET endpoint to obtain a WebSocket authentication token.
   * <p>
   * Expects an Authorization header with a valid Bearer JWT. Verifies the token,
   * extracts <em>companyId</em> and <em>scopes</em>, and issues a WS token via
   * {@link WebSocketSessionService#issueAndStoreToken(Integer, List)}.
   * </p>
   *
   * @param authHeader the HTTP Authorization header containing a Bearer JWT
   * @return 200 OK with JSON {"wsToken": "..."} if successful;
   *         401 Unauthorized if header is missing/invalid or token verification fails;
   *         500 Internal Server Error on unexpected errors
   */
  @GetMapping
  public ResponseEntity<?> getWsAuthToken(@RequestHeader("Authorization") String authHeader) {
    try {
      if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
        return ResponseEntity.status(401).build();
      }
      String jwt = authHeader.substring(BEARER_PREFIX.length());

      Integer companyId = jwtUtil.verifyTokenAndGetCompanyId(jwt);
      List<String> scopes = jwtUtil.verifyTokenAndGetScopes(jwt);

      return webSocketSessionService.issueAndStoreToken(companyId, scopes)
              .map(wsToken -> ResponseEntity.ok().body(Map.of("wsToken", wsToken)))
              .orElse(ResponseEntity.status(401).build());
    } catch (Exception e) {
      logger.severe("Error fetching WebSocket token: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(new ErrorResponse("An error occurred while fetching WebSocket token"));
    }
  }
}