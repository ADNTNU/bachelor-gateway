package no.ntnu.gr10.bachelorgateway.exception;

/**
 * Custom exception class for handling invalid role errors.
 * This class extends IllegalArgumentException and is used to indicate
 * that a specific role is invalid in the system.
 *
 * @author Anders Lund
 * @version 16.04.2025
 */
public class InvalidRoleException extends IllegalArgumentException {
  /**
   * Constructs a new InvalidRoleException with the specified detail message.
   *
   * @param message the detail message
   */
  public InvalidRoleException(String message) {
    super(message);
  }
}