package no.ntnu.gr10.bachelorgateway.security.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for managing WebSocket sessions.
 * This service issues and stores tokens for WebSocket sessions,
 */
@Service
public class WebSocketSessionService {

  private final Logger logger = Logger.getLogger(getClass().getName());
  private final StringRedisTemplate redis;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final String WS_SESSION_PREFIX = "ws-session:";

  /**
   * Constructor for WebSocketSessionService.
   *
   * @param redis the Redis template for storing session data
   */
  public WebSocketSessionService(StringRedisTemplate redis) {
    this.redis = redis;
  }

  /**
   * Issues a new WebSocket session token and stores the associated company ID and scopes in Redis.
   *
   * @param companyId the ID of the company
   * @param scopes    the list of scopes for the session
   * @return an Optional containing the issued token, or an empty Optional if an error occurred
   */
  public Optional<String> issueAndStoreToken(Integer companyId, List<String> scopes) {
    try {
      if (companyId == null || scopes == null) {
        return Optional.empty();
      }

      String wsToken = UUID.randomUUID().toString();
      Map<String, Object> session = new HashMap<>();
      session.put("companyId", companyId);
      session.put("scopes", scopes);

      redis.opsForValue().set(
              WS_SESSION_PREFIX + wsToken,
              objectMapper.writeValueAsString(session),
              1, TimeUnit.MINUTES
      );
      return Optional.of(wsToken);
    } catch (Exception e) {
      logger.log(java.util.logging.Level.SEVERE, "Failed to issue and store token", e);
      return Optional.empty();
    }
  }

  /**
   * Resolves a WebSocket session from the given token.
   *
   * @param wsToken the token to resolve.
   * @return an Optional containing the SessionDto if the session is found,
   *         or an empty Optional if not found or if an error occurred.
   */
  @SuppressWarnings("unchecked")
  public Optional<SessionDto> resolveSession(String wsToken) {
    try {
      String json = redis.opsForValue().get(WS_SESSION_PREFIX + wsToken);
      if (json == null) {
        return Optional.empty();
      }

      Object raw = objectMapper.readValue(json, Object.class);
      if (!(raw instanceof Map<?, ?> map)) {
        return Optional.empty();
      }

      Object companyId = map.get("companyId");
      Object scopes = map.get("scopes");

      if (!(companyId instanceof Integer)) {
        return Optional.empty();
      }
      if (!(scopes instanceof List<?> scopeList)
              || !scopeList.stream().allMatch(String.class::isInstance)) {
        return Optional.empty();
      }

      return Optional.of(new SessionDto((Integer) companyId, (List<String>) scopeList));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  /**
   * Invalidates a WebSocket session by deleting the token from Redis.
   *
   * @param wsToken the token to invalidate
   */
  public void invalidate(String wsToken) {
    redis.delete(WS_SESSION_PREFIX + wsToken);
  }
}