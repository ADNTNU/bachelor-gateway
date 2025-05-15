package no.ntnu.gr10.bachelorgateway.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

/**
 * Enum representing the different scopes in the application.
 * Scopes define the permissions for what API keys can access.
 */
@Getter
public enum Scope implements GrantedAuthority {

  FISHERY_ACTIVITY("fishery-activity"),
  FISHING_FACILITY("fishing-facility"),
  ADMIN("admin");

  private final String authority;

  Scope(String authority) {
    this.authority = authority;
  }


}
