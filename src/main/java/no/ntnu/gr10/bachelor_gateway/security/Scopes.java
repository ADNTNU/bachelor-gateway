package no.ntnu.gr10.bachelor_gateway.security;

import org.springframework.security.core.GrantedAuthority;

public enum Scopes implements GrantedAuthority {

  FISHERY_ACTIVITY("fishery_activity"),
  FISHING_FACILITY("fishing_facility"),
  ADMIN("admin");

  private final String authority;

  Scopes(String authority) {
    this.authority = authority;
  }


  /**
   * Returns the authority string as it appears in the JWT claim.
   *
   * @return the authority key in the token
   */
  public String getAuthority() {
    return authority;
  }

}
