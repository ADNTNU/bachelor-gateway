package no.ntnu.gr10.bachelor_gateway.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for managing companies.
 * Extends JpaRepository to provide CRUD operations.
 *
 * @author Anders Lund
 * @version 05.04.2025
 */
public interface CompanyRepository extends JpaRepository<Company, Long> {
  @Query("SELECT c FROM Company c JOIN c.administrators a WHERE a.id = :administratorId ORDER BY c.name")
  List<Company> findByAdministratorId(Long administratorId);
}