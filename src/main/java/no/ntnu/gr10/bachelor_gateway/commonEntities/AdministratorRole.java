package no.ntnu.gr10.bachelor_gateway.commonEntities;

import no.ntnu.gr10.bachelor_gateway.exception.InvalidRoleException;

public enum AdministratorRole {
  OWNER,
  ADMINISTRATOR;

  public static AdministratorRole fromString(String value) throws InvalidRoleException {
    try {
      return AdministratorRole.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new InvalidRoleException("Invalid role: " + value);
    }
  }
}