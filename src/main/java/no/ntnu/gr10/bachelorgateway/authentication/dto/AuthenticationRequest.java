package no.ntnu.gr10.bachelorgateway.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for carrying authentication credentials in requests to the gateway's authentication endpoint.
 *
 * <p>Contains an identifier and a secret (e.g., a password or token) for client authentication.
 * Both fields are required and must not be blank.
 * </p>
 *
 * @author Anders Lund and Daniel Neset
 * @version 05.05.2025
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AuthenticationRequest {

  /**
   * Unique identifier for the client (e.g., username or client ID).
   */
  @NotNull
  @NotBlank
  private String id;

  /**
   * Secret for authentication (e.g., password or client secret).
   */
  @NotNull
  @NotBlank
  private String secret;

}
