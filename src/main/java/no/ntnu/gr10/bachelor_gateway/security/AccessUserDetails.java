package no.ntnu.gr10.bachelor_gateway.security;

import no.ntnu.gr10.bachelor_gateway.apiKey.ApiKey;
import no.ntnu.gr10.bachelor_gateway.company.Company;
import no.ntnu.gr10.bachelor_gateway.scope.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A UserDetails implementation for API key based authentication.
 * <p>
 * This class wraps an {@link ApiKey} entity and exposes its key values (ID, client id, secret, etc.)
 * along with the granted authorities (derived from scopes) and company id.
 * </p>
 *
 * @author Daniel Neset
 * @version 11.04.2025
 */
public class AccessUserDetails implements UserDetails {

  private final long id;
  private final String tokenId;
  private final String tokenSecret;
  private final boolean enabled;
  private final List<GrantedAuthority> authorities = new LinkedList<>();
  private final int companyId;

  /**
   * Constructs a new AccessUserDetails using the provided API key.
   *
   * @param apiKey the API key entity containing user information and scopes
   */
  public AccessUserDetails(ApiKey apiKey){
    this.id = apiKey.getId();
    this.tokenId = apiKey.getClientId();
    this.tokenSecret = apiKey.getClientSecret();
    this.enabled = apiKey.isEnabled();
    this.convertRoles(apiKey.getScopes());
    this.companyId = apiKey.getCompanyId();
  }

  private void convertRoles(Set<Scope> permissions) {
    authorities.clear();
    for (Scope role : permissions) {
      authorities.add(new SimpleGrantedAuthority(role.getName()));
    }
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

  public int getCompanyId(){
    return companyId;
  }

  public Long getId(){
    return id;
  }


}
