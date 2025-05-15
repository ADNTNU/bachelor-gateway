package no.ntnu.gr10.bachelor_gateway.authentication.dto;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * DTO representing the response returned after a successful authentication.
 *
 * <p>Holds the issued JWT token along with metadata about the authenticated user,
 * including their username, company ID, and granted authorities.</p>
 *
 * @param token The JWT token issued for use in subsequent secured requests
 * @param username The unique username or client ID associated with the token
 * @param company The identifier of the company to which the authenticated user belongs
 * @param authorities The collection of granted authorities or scopes assigned to the user
 */
public record AuthenticationResponse(String token, String username, int company, Collection<? extends GrantedAuthority> authorities) {
}
