package no.ntnu.gr10.bachelor_gateway.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JwtUserDetails implements UserDetails {
  private final String id;
  private final String company;
  private final Collection<? extends GrantedAuthority> authorities;

  public JwtUserDetails(String id, String company, Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.company = company;
    this.authorities = authorities;
  }

  // This is not used with JWT, so we return an empty string.
  @Override
  public String getPassword() {
    return "";
  }

  @Override
  public String getUsername() {
    return id;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public String getId() {
    return id;
  }
}