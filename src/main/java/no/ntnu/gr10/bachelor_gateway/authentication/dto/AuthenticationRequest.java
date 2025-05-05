package no.ntnu.gr10.bachelor_gateway.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticationRequest {
  @NotNull
  @NotBlank
  private String id;
  @NotNull
  @NotBlank
  private String secret;

  public AuthenticationRequest() {
//    Empty constructor for serialization/deserialization
  }

  public AuthenticationRequest(@NotNull @NotBlank String id, @NotNull @NotBlank String secret) {
    this.id = id;
    this.secret = secret;
  }
}
