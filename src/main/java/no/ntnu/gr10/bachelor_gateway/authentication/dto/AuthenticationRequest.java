package no.ntnu.gr10.bachelor_gateway.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for carrying authentication credentials in requests to the gateway's authentication endpoint.
 * <p>
 * Contains an identifier and a secret (e.g., a password or token) for client authentication.
 * Both fields are required and must not be blank.
 * </p>
 *
 * @author Anders Lund and Daniel Neset
 * @version 05.05.2025
 */
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

  /**
   * Default constructor needed for serialization/deserialization frameworks.
   */
  public AuthenticationRequest() {
//    Empty constructor for serialization/deserialization
  }

  /**
   * Constructs a new AuthenticationRequest with the given credentials.
   *
   * @param id The unique identifier for the client, must not be null or blank
   * @param secret The secret for authentication, must not be null or blank
   */
  public AuthenticationRequest(@NotNull @NotBlank String id, @NotNull @NotBlank String secret) {
    this.id = id;
    this.secret = secret;
  }
}
