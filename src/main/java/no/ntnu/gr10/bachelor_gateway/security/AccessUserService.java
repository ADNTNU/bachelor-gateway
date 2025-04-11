package no.ntnu.gr10.bachelor_gateway.security;

import no.ntnu.gr10.bachelor_gateway.entity.Client;
import no.ntnu.gr10.bachelor_gateway.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
@Service
public class AccessUserService implements UserDetailsService{

    private static final int MIN_PASSWORD_LENGTH = 8;
    private final ClientRepository clientRepository;
    //private final RoleRepository roleRepository;


    @Autowired
    public AccessUserService(ClientRepository clientRepository) {
      this.clientRepository = clientRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      Optional<Client> client = clientRepository.findByClientId(username);
      if (client.isPresent()) {
        return new AccessUserDetails(client.get());
      } else {
        throw new UsernameNotFoundException("USer: " + username + " not found!");
      }
    }

    public Client getSessionUser() {
      SecurityContext securityContext = SecurityContextHolder.getContext();
      Authentication authentication = securityContext.getAuthentication();
      String username = authentication.getName();
      return clientRepository.findByClientId(username).orElse(null);
    }

    /**
     * Checks if a user with the specified username exists.
     *
     * @param username The username to check.
     * @return Return true if the user exists, false otherwise.
     */
    private boolean userExists(String username) {
      try {
        loadUserByUsername(username);
        return true;
      } catch (UsernameNotFoundException usernameNotFoundException) {
        return false;
      }
    }

    /**
     * Validates the password against specific requirements.
     *
     * @param password The password to check.
     * @return Return an error message if validation fails, null if it passes.
     */
    private String checkPasswordRequirements(String password) {
      String errorMessage = null;
      if (password == null || password.length() == 0) {
        errorMessage = "Password cannot be empty";
      } else if (password.length() < MIN_PASSWORD_LENGTH) {
        errorMessage = "Password must be at least " + MIN_PASSWORD_LENGTH + " characters";
      }
      return errorMessage;
    }


    /**
     * Creates a bcrypt hash of the given password.
     *
     * @param password The password to hash.
     * @return Return the hashed password.
     */
    private String createHash(String password) {
      return BCrypt.hashpw(password, BCrypt.gensalt());
    }

}
