package no.ntnu.gr10.bachelor_gateway.security;

import no.ntnu.gr10.bachelor_gateway.apiKey.ApiKey;
import no.ntnu.gr10.bachelor_gateway.scope.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AccessUserDetails implements UserDetails {

  private final long id;
  private final String tokenId;
  private final String tokenSecret;
  private final boolean enabled;
  private final List<GrantedAuthority> authorities = new LinkedList<>();

  public AccessUserDetails(ApiKey apiKey){
    this.id = apiKey.getId();
    this.tokenId = apiKey.getClientId();
    this.tokenSecret = apiKey.getClientSecret();
    this.enabled = apiKey.isEnabled();
    this.convertRoles(apiKey.getScopes());
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
}
