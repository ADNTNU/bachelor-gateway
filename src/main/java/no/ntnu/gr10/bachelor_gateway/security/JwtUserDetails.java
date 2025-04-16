package no.ntnu.gr10.bachelor_gateway.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Represents a JWT-based user and implements {@link UserDetails}.
 * <p>
 * This implementation holds the unique API key client id (used as the username),
 * the associated company id, and the collection of granted authorities.
 * The password is not used, so {@code getPassword()} returns an empty string.
 * </p>
 *
 * @author Daniel Neset
 * @version 14.04.2025
 */
public class JwtUserDetails implements UserDetails {
  private final String id;
  private final int company;
  private final Collection<? extends GrantedAuthority> authorities;

  /**
   * Constructs a new {@code JwtUserDetails} instance.
   *
   * @param id the unique API key client id, used as the username
   * @param company the company id associated with the API key
   * @param authorities the granted authorities (roles/scopes)
   */
  public JwtUserDetails(String id, int company, Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.company = company;
    this.authorities = authorities;
  }

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