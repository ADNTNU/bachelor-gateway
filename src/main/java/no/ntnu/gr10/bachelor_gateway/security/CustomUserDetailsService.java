package no.ntnu.gr10.bachelor_gateway.security;

import no.ntnu.gr10.bachelor_gateway.apiKey.ApiKey;
import no.ntnu.gr10.bachelor_gateway.apiKey.ApiKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for retrieving access user details from the API key repository.
 * <p>
 * This service implements {@link UserDetailsService} and loads user details based on the API key's client id.
 * </p>
 *
 * @author Daniel Neset
 * @version 11.04.2025
 */
@Service
public class CustomUserDetailsService implements UserDetailsService{

    private final ApiKeyRepository apiKeyRepository;

  /**
   * Constructs a new AccessUserService with the specified ApiKeyRepository.
   *
   * @param apiKeyRepository the repository for accessing API key entities
   */
    @Autowired
    public CustomUserDetailsService(ApiKeyRepository apiKeyRepository) {
      this.apiKeyRepository = apiKeyRepository;
    }

  /**
   * Loads user details using the provided client id.
   * <p>
   * Retrieves the {@link ApiKey} using the client id (treated as the username) from the repository.
   * If found, returns an {@link CustomUserDetails} instance wrapping the API key.
   * Otherwise, throws a {@link UsernameNotFoundException}.
   * </p>
   *
   * @param username the client id of the API key
   * @return the corresponding {@link UserDetails} for the API key
   * @throws UsernameNotFoundException if the API key is not found
   */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      Optional<ApiKey> apiKey = apiKeyRepository.findByClientId(username);
      if(apiKey.isPresent()) {
        return new CustomUserDetails(apiKey.get());
      } else
      {
          throw new UsernameNotFoundException("User: " + username + " not found!");
      }
    }
}


