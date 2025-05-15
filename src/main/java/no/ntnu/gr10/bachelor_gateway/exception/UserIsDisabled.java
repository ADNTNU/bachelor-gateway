package no.ntnu.gr10.bachelor_gateway.exception;

/**
 * Custom exception class for handling Disabled Users.
 * This class extends Exception and is used to indicate
 * that a specific User is Disabled
 *
 * @author Daniel neset
 * @version 17.04.2025
 */
public class UserIsDisabled extends Exception {
  public UserIsDisabled(String e){
    super(e);
  }
}
