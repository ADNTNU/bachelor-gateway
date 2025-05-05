package no.ntnu.gr10.bachelor_gateway.security.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class WebSocketSessionService {

  private final Logger logger = Logger.getLogger(getClass().getName());
  private final StringRedisTemplate redis;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final String WS_SESSION_PREFIX = "ws-session:";

  public WebSocketSessionService(StringRedisTemplate redis) {
    this.redis = redis;
  }

  public Optional<String> issueAndStoreToken(Integer companyId, List<String> scopes) {
    try {
      if (companyId == null || scopes == null) return Optional.empty();

      String wsToken = UUID.randomUUID().toString();
      Map<String, Object> session = new HashMap<>();
      session.put("companyId", companyId);
      session.put("scopes", scopes);

      redis.opsForValue().set(WS_SESSION_PREFIX + wsToken, objectMapper.writeValueAsString(session), 1, TimeUnit.MINUTES);
      return Optional.of(wsToken);
    } catch (Exception e) {
      logger.severe("Failed to issue and store token: " + e.getMessage());
      e.printStackTrace();
      return Optional.empty();
    }
  }

  @SuppressWarnings("unchecked")
  public Optional<SessionDto> resolveSession(String wsToken) {
    try {
      String json = redis.opsForValue().get(WS_SESSION_PREFIX + wsToken);
      if (json == null) return Optional.empty();

      Object raw = objectMapper.readValue(json, Object.class);
      if (!(raw instanceof Map<?, ?> map)) return Optional.empty();

      Object companyId = map.get("companyId");
      Object scopes = map.get("scopes");

      if (!(companyId instanceof Integer)) return Optional.empty();
      if (!(scopes instanceof List<?> scopeList) ||
              !scopeList.stream().allMatch(String.class::isInstance)) {
        return Optional.empty();
      }

      return Optional.of(new SessionDto((Integer) companyId, (List<String>) scopeList));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public void invalidate(String wsToken) {
    redis.delete(WS_SESSION_PREFIX + wsToken);
  }
}