package no.ntnu.gr10.bachelor_gateway.security;

import no.ntnu.gr10.bachelor_gateway.entity.Client;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class AccessUserDetails implements UserDetails {

  private final String tokenId;
  private final String tokenSecret;
  // TODO Implement Authority
  //private final List<GrantedAuthority> authorityList = new LinkedList<>();

  public AccessUserDetails(Client client){
    this.tokenId = client.getClientId();
    this.tokenSecret = client.getSecret();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getPassword() {
    return tokenSecret;
  }

  @Override
  public String getUsername() {
    return tokenId;
  }
}
