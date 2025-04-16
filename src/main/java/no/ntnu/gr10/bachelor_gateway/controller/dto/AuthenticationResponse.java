package no.ntnu.gr10.bachelor_gateway.controller.dto;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record AuthenticationResponse(String token, String username, int company, Collection<? extends GrantedAuthority> authorities) {
}
