package no.ntnu.gr10.bachelor_gateway.apiKey;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing API keys.
 * Extends JpaRepository to provide CRUD operations.
 *
 * @author Anders Lund
 * @version 05.04.2025
 */
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
  Optional<ApiKey> findByClientId(String clientId);

}
