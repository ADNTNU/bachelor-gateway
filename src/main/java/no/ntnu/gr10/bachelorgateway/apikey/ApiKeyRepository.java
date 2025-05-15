package no.ntnu.gr10.bachelorgateway.apikey;


import java.util.Optional;
import no.ntnu.gr10.bachelorgateway.commonentities.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing API keys.
 * Extends JpaRepository to provide CRUD operations.
 *
 * @author Anders Lund
 * @version 05.04.2025
 */
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
  /**
   * Finds an API key by its client ID.
   *
   * @param clientId the client ID of the API key
   * @return an Optional containing the found API key, or empty if not found
   */
  Optional<ApiKey> findByClientId(String clientId);

}
