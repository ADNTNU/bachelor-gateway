package no.ntnu.gr10.bachelorgateway.commonentities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;

/**
 * Represents a company entity.
 *
 * <p>This entity is used to store company information in the database.
 * The company is associated with different administrators and API keys.
 * </p>
 *
 * @author Anders Lund
 * @version 07.04.2025
 */
@Getter
@Entity
@Table(name = "companies")
public class Company {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(unique = true, nullable = false)
  private String name;

  @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
  private final Set<AdministratorCompany> administratorCompanies = new HashSet<>();


  /**
   * Default constructor for JPA.
   */
  public Company() {
    // Default constructor for JPA
  }

  /**
   * Constructor for creating a new company.
   *
   * @param name The name of the company.
   */
  public Company(String name) {
    setName(name);
  }

  /**
   * Set the name of the company.
   *
   * @param name The name to set.
   * @throws IllegalArgumentException if the name is null or empty.
   * @throws IllegalArgumentException if the name exceeds 255 characters.
   */
  private void setName(String name) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Company name cannot be null or empty");
    }
    if (name.length() > 255) {
      throw new IllegalArgumentException("Company name cannot exceed 255 characters");
    }
    this.name = name;
  }


  /**
   * Checks whether two companies are equal based on their ID and name.
   *
   * @param obj The object to compare with.
   * @return True if the companies are equal, false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Company company)) {
      return false;
    }
    return Objects.equals(id, company.id) && Objects.equals(name, company.name);
  }

  /**
   * Returns the hash code of the company based on its ID and name.
   *
   * @return The hash code of the company.
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  /**
   * Returns a string representation of the company.
   *
   * @return A string representation of the company.
   */
  @Override
  public String toString() {
    return "Company{"
            + "id=" + id
            + ", name='" + name + '\''
            + '}';
  }
}