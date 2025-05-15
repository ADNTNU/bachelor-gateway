package no.ntnu.gr10.bachelorgateway.security;

import no.ntnu.gr10.bachelorgateway.apikey.ApiKeyRepository;
import no.ntnu.gr10.bachelorgateway.commonentities.ApiKey;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Service for retrieving access user details from the API key repository.
 *
 * <p>This service implements {@link ReactiveUserDetailsService} and
 * loads user details based on the API key's client id.
 * </p>
 *
 * @author Daniel Neset
 * @version 11.04.2025
 */
@Component
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

  private final ApiKeyRepository apiKeyRepository;

  /**
   * Constructs a new AccessUserService with the specified ApiKeyRepository.
   *
   * @param apiKeyRepository the repository for accessing API key entities
   */
  public CustomReactiveUserDetailsService(ApiKeyRepository apiKeyRepository) {
    this.apiKeyRepository = apiKeyRepository;
  }

  /**
   * Loads user details using the provided client id.
   *
   * <p>Retrieves the {@link ApiKey} using the client id (treated as the username)
   * from the repository.
   * If found, returns an {@link CustomUserDetails} instance wrapping the API key.
   * Otherwise, throws a {@link UsernameNotFoundException}.
   * </p>
   *
   * @param username the client id of the API key
   * @return the corresponding {@link UserDetails} for the API key
   * @throws UsernameNotFoundException if the API key is not found
   */
  @Override
  public Mono<UserDetails> findByUsername(String username) {
    return Mono.fromCallable(() -> apiKeyRepository.findByClientId(username)
                    .map(CustomUserDetails::new)
                    .map(UserDetails.class::cast)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username))
            )
            .subscribeOn(Schedulers.boundedElastic());
  }
}


