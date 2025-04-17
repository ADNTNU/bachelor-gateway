package no.ntnu.gr10.bachelor_gateway.scope;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing scopes.
 * Extends JpaRepository to provide CRUD operations.
 *
 * @author Anders Lund
 * @version 06.04.2025
 */
public interface ScopeRepository extends JpaRepository<Scope, Long> {

}