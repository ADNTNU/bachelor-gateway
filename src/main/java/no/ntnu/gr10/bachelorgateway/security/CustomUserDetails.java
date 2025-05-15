package no.ntnu.gr10.bachelorgateway.security;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import no.ntnu.gr10.bachelorgateway.commonentities.ApiKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * A UserDetails implementation for API key based authentication.
 *
 * <p>This class wraps an {@link ApiKey} entity and exposes its key values
 * (ID, client id, secret, etc.)
 * along with the granted authorities (derived from scopes) and company id.
 * </p>
 *
 * @author Daniel Neset
 * @version 11.04.2025
 */
public class CustomUserDetails implements UserDetails {

  private final long id;
  private final String tokenId;
  private final String tokenSecret;
  private final boolean enabled;
  private final List<GrantedAuthority> authorities = new LinkedList<>();
  @Getter
  private final long companyId;

  /**
   * Constructs a new AccessUserDetails using the provided API key.
   *
   * @param apiKey the API key entity containing user information and scopes
   */
  public CustomUserDetails(ApiKey apiKey) {
    this.id = apiKey.getId();
    this.tokenId = apiKey.getClientId();
    this.tokenSecret = apiKey.getClientSecret();
    this.enabled = apiKey.isEnabled();
    this.convertRoles(apiKey.getScopes());
    this.companyId = apiKey.getCompany().getId();
  }

  private void convertRoles(Set<no.ntnu.gr10.bachelorgateway.commonentities.Scope> permissions) {
    authorities.clear();
    for (no.ntnu.gr10.bachelorgateway.commonentities.Scope scope : permissions) {
      authorities.add(new SimpleGrantedAuthority(scope.getKey()));
    }
    //    Temporarily adds ADMIN scope to all API keys,
    //    that allows all API-keys to interact with the producer API.
    //    Just for making the demo work semi-securely without needing
    //    to create a separate role system for the producer.
    authorities.add(new SimpleGrantedAuthority(Scope.ADMIN.getAuthority()));
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return tokenSecret;
  }

  @Override
  public String getUsername() {
    return tokenId;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  public Long getId() {
    return id;
  }


}
