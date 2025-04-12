package no.ntnu.gr10.bachelor_gateway.scope;

import jakarta.persistence.*;

/**
 * Represents a scope to limit access to a subset of API endpoints.
 * <p>
 * A scope is a specific area of access control that can be assigned to API keys.
 * </p>
 *
 * @author Anders Lund
 */
@Entity
@Table(name = "scopes")
public class Scope {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  public Scope() {}

  /**
   * Constructor for creating a new scope.
   *
   * @param name        The name of the scope.
   * @param description A description of the scope.
   */
  public Scope(String name, String description) {
    setName(name);
    setDescription(description);
  }

  /**
   * Get the ID of the scope.
   *
   * @return The ID of the scope.
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name of the scope.
   *
   * @param name The name to set.
   * @throws IllegalArgumentException if the name is null or empty.
   * @throws IllegalArgumentException if the name exceeds 255 characters.
   */
  private void setName(String name) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Scope name cannot be null or empty");
    }

    if (name.length() > 255) {
      throw new IllegalArgumentException("Scope name cannot exceed 255 characters");
    }

    this.name = name;
  }

  /**
   * Get the description of the scope.
   *
   * @return The description of the scope.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Set the description of the scope.
   *
   * @param description The description to set.
   * @throws IllegalArgumentException if the description exceeds 255 characters.
   */
  public void setDescription(String description) {
    if (description != null && description.length() > 255) {
      throw new IllegalArgumentException("Scope description cannot exceed 255 characters");
    }

    this.description = description;
  }


}