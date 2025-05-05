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

@RestController
@RequestMapping("/ws-auth-token")
public class WebSocketTokenController {

  private final Logger logger = Logger.getLogger(getClass().getName());
  private final JwtUtil jwtUtil;
  private final WebSocketSessionService webSocketSessionService;
  private static final String BEARER_PREFIX = "Bearer ";

  public WebSocketTokenController(JwtUtil jwtUtil, WebSocketSessionService webSocketSessionService) {
    this.jwtUtil = jwtUtil;
    this.webSocketSessionService = webSocketSessionService;
  }


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