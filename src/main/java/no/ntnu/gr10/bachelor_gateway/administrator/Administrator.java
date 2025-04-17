package no.ntnu.gr10.bachelor_gateway.administrator;

import jakarta.persistence.*;
import no.ntnu.gr10.bachelor_gateway.company.Company;


import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents an administrator entity.
 * <p>
 *   This entity is used to store administrator information in the database.
 *   The administrator is associated with different companies and has a username and password for authentication.
 *   </p>
 *
 * @author Anders Lund
 * @version 06.04.2025
 */
@Entity
@Table(name = "administrators")
public class Administrator {
  @Id()
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private boolean enabled = true;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @ManyToMany
  @JoinTable(
          name = "administrator_company",
          joinColumns = @JoinColumn(name = "administrator_id"),
          inverseJoinColumns = @JoinColumn(name = "company_id")
  )
  private Set<Company> companies = new HashSet<>();

  public Administrator() {
    // Default constructor for JPA
  }

  /**
   * Constructor for creating a new administrator.
   * @param username The username of the administrator.
   * @param password The password of the administrator.
   */
  public Administrator(String username, String password, String firstName, String lastName) {
    setUsername(username);
    setPassword(password);
    setFirstName(firstName);
    setLastName(lastName);
  }

  /**
   * Gets the ID of the administrator.
   * @return The ID of the administrator.
   */
  public Long getId() {
    return id;
  }

  /**
   * Gets the username of the administrator.
   * @return The username of the administrator.
   */
  public String getUsername() {
    return username;
  }

  private void setUsername(String username) {
//        TODO: Add safe guard for username
    this.username = username;
  }

  /**
   * Gets the password of the administrator.
   * @return The password of the administrator.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Set the password for the administrator.
   * @param password The password to set.
   */
  public void setPassword(String password) {
//        TODO: Add safe guard for password
    this.password = password;
  }

  /**
   * Get the first name of the administrator.
   * @return The first name of the administrator.
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Set the first name of the administrator.
   * @param firstName The first name to set.
   */
  public void setFirstName(String firstName) {
//        TODO: Add safe guard for first name
    this.firstName = firstName;
  }

  /**
   * Get the last name of the administrator.
   * @return The last name of the administrator.
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Set the last name of the administrator.
   * @param lastName The last name to set.
   */
  public void setLastName(String lastName) {
//        TODO: Add safe guard for last name
    this.lastName = lastName;
  }

  /**
   * Get the enabled status of the administrator.
   * @return true if the administrator is enabled, false otherwise.
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Set whether the administrator is enabled or not.
   * @param enabled The enabled status to set.
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Get the companies associated with this administrator.
   * @return A set of companies associated with this administrator.
   */
  public Set<Company> getCompanies() {
    return companies;
  }

  /**
   * Add a company to the set of companies associated with this administrator.
   * @param company The company to add.
   */
  public void addCompany(Company company) {
    this.companies.add(company);
//        TODO: Verify if we need to add the administrator to the company as well
    company.getAdministrators().add(this);
  }

  /**
   * Remove a company from the set of companies associated with this administrator.
   * @param company The company to remove.
   */
  public void removeCompany(Company company) {
    this.companies.remove(company);
//        TODO: Verify if we need to remove the administrator from the company as well
    company.getAdministrators().remove(this);
  }

  /**
   * Checks whether two administrators are equal based on their ID and username.
   * @param o The object to compare with.
   * @return true if the administrators are equal, false otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Administrator other)) return false;

    return Objects.equals(id, other.id) && username.equals(other.username);
  }

  /**
   * Returns the hash code of the administrator based on its ID and username.
   * @return The hash code of the administrator.
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, username);
  }

  /**
   * Returns a string representation of the administrator.
   * <p>
   *   This method returns a string representation of the administrator, including its ID, username, and enabled status.
   *   </p>
   * @return A string representation of the administrator.
   */
  @Override
  public String toString() {
    return "Administrator{" +
            "id=" + id +
            ", enabled=" + enabled +
            ", username='" + username + '\'' +
            '}';
  }
}