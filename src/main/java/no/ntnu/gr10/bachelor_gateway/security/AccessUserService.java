package no.ntnu.gr10.bachelor_gateway.security;

import no.ntnu.gr10.bachelor_gateway.apiKey.ApiKey;
import no.ntnu.gr10.bachelor_gateway.apiKey.ApiKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AccessUserService implements UserDetailsService{

    private final ApiKeyRepository apiKeyRepository;

    @Autowired
    public AccessUserService(ApiKeyRepository apiKeyRepository) {
      this.apiKeyRepository = apiKeyRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      Optional<ApiKey> apiKey = apiKeyRepository.findByClientId(username);
      if (apiKey.isPresent()) {
        return new AccessUserDetails(apiKey.get());
      } else {
        throw new UsernameNotFoundException("User: " + username + " not found!");
      }
    }

}
