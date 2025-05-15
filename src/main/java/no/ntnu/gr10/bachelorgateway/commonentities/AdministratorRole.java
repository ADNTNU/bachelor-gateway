package no.ntnu.gr10.bachelorgateway.commonentities;

import no.ntnu.gr10.bachelorgateway.exception.InvalidRoleException;

/**
 * Enum representing the roles of an administrator.
 *
 * <p>The roles are:
 * <ul>
 *   <li>OWNER: The owner of the company.</li>
 *   <li>ADMINISTRATOR: An administrator of the company.</li>
 * </ul>
 */
public enum AdministratorRole {
  OWNER,
  ADMINISTRATOR;

  /**
   * Returns corresponding AdministratorRole from a string.
   * The string is case-insensitive.
   *
   * @param value the string representation of the role
   * @return the corresponding AdministratorRole
   * @throws InvalidRoleException if the string does not match any role
   */
  public static AdministratorRole fromString(String value) throws InvalidRoleException {
    try {
      return AdministratorRole.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new InvalidRoleException("Invalid role: " + value);
    }
  }
}