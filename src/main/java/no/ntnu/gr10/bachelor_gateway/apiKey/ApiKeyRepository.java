package no.ntnu.gr10.bachelor_gateway.apiKey;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
  Optional<ApiKey> findByClientIdAndClientSecret(String clientId, String clientSecret);
  //TODO Look into using both or just one /maybe smart to use both
  Optional<ApiKey> findByClientId(String clientId);

  List<ApiKey> findApiKeysByCompanyId(long companyId, org.springframework.data.domain.Pageable pageable);
}
