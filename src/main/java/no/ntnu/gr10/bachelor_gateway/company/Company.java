package no.ntnu.gr10.bachelor_gateway.company;

import jakarta.persistence.*;
import no.ntnu.gr10.bachelor_gateway.administrator.Administrator;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a company entity.
 * <p>
 * This entity is used to store company information in the database.
 * The company is associated with different administrators and API keys.
 * </p>
 *
 * @author Anders Lund
 * @version 07.04.2025
 */
@Entity
@Table(name = "companies")
public class Company {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(unique = true, nullable = false)
  private String name;

  @ManyToMany(mappedBy = "companies")
  private Set<Administrator> administrators = new HashSet<>();

  public Company() {
    // Default constructor for JPA
  }

  /**
   * Constructor for creating a new company.
   * @param name The name of the company.
   */
  public Company(String name) {
    setName(name);
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  /**
   * Set the name of the company.
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
   * Gets the administrators associated with the company.
   * @return The set of administrators associated with the company.
   */
  public Set<Administrator> getAdministrators() {
    return administrators;
  }

  /**
   * Adds an administrator to the company.
   */
  public void addAdministrator(Administrator administrator) {
    if (administrator == null) {
      throw new IllegalArgumentException("Administrator cannot be null");
    }
    administrators.add(administrator);
//        TODO: Verify if we need to add the company to the administrator as well
    administrator.getCompanies().add(this);
  }

  /**
   * Removes an administrator from the company.
   */
  public void removeAdministrator(Administrator administrator) {
    if (administrator == null) {
      throw new IllegalArgumentException("Administrator cannot be null");
    }
    administrators.remove(administrator);
//        TODO: Verify if we need to remove the company from the administrator as well
    administrator.getCompanies().remove(this);
  }

  /**
   * Checks whether two companies are equal based on their ID and name.
   * @param obj The object to compare with.
   * @return True if the companies are equal, false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Company company)) return false;
    return Objects.equals(id, company.id) && Objects.equals(name, company.name);
  }

  /**
   * Returns the hash code of the company based on its ID and name.
   * @return The hash code of the company.
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  /**
   * Returns a string representation of the company.
   * @return A string representation of the company.
   */
  @Override
  public String toString() {
    return "Company{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
  }
}