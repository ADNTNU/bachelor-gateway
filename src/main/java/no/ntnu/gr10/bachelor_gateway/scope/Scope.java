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
/**
 * Represents a scope to limit access to a subset of API endpoints.
 * <p>
 * A scope is a specific area of access control that can be assigned to API keys.
 * </p>
 */
@Entity
@Table(name = "api_scopes")
public class Scope {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "scope_key", unique = true, nullable = false)
  private String key;

  @Column(nullable = false)
  private boolean enabled = true;

  @Column(unique = true, nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  public Scope() {
    // Default constructor for JPA
  }

  /**
   * Constructor for creating a new scope.
   *
   * @param key         The unique key for the scope.
   * @param name        The name of the scope.
   * @param description A description of the scope.
   */
  public Scope(String key, String name, String description) {
    setKey(key);
    setName(name);
    setDescription(description);
  }

  /**
   * Get the ID of the scope.
   *
   * @return The ID of the scope.
   */
  public long getId() {
    return id;
  }

  /**
   * Get the unique key of the scope.
   *
   * @return The unique key of the scope.
   */
  public String getKey() {
    return key;
  }

  /**
   * Set the unique key of the scope.
   *
   * @param key The unique key to set.
   * @throws IllegalArgumentException if the key is null or empty.
   * @throws IllegalArgumentException if the key exceeds 50 characters.
   */
  public void setKey(String key) {
    if (key == null || key.isEmpty()) {
      throw new IllegalArgumentException("Scope key cannot be null or empty");
    }

    if (key.length() > 50) {
      throw new IllegalArgumentException("Scope key cannot exceed 50 characters");
    }

    this.key = key;
  }

  /**
   * Get the enabled status of the scope.
   *
   * @return true if the scope is enabled, false otherwise.
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Set the enabled status of the scope.
   *
   * @param enabled The enabled status to set.
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Get the name of the scope.
   *
   * @return The name of the scope.
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