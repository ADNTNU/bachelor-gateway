package no.ntnu.gr10.bachelorgateway.security.websocket;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for WebSocket session information.
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SessionDto {
  private Integer companyId;
  private List<String> scopes;
}
