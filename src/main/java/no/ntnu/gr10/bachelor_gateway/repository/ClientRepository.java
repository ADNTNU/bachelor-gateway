package no.ntnu.gr10.bachelor_gateway.repository;

import no.ntnu.gr10.bachelor_gateway.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
  Optional<Client> findByClientId(String clientId);
}
