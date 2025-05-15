package no.ntnu.gr10.bachelorgateway.commonentities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

/**
 * This class represents a composite key for the AdministratorCompany entity.
 * It consists of two fields: administratorId and companyId.
 * The class implements Serializable and overrides equals() and hashCode() methods
 * to ensure proper comparison and hashing based on the composite key fields.
 */
@Embeddable
public class AdministratorCompanyId implements Serializable {
  private Long administratorId;
  private Long companyId;

  /**
   * Default constructor for serialization.
   */
  public AdministratorCompanyId() {
  //  Default constructor for JPA
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AdministratorCompanyId that)) {
      return false;
    }

    if (!administratorId.equals(that.administratorId)) {
      return false;
    }
    return companyId.equals(that.companyId);
  }

  @Override
  public int hashCode() {
    int result = administratorId.hashCode();
    //  31 is a prime number, commonly used in hashCode implementations
    //  to reduce the chance of collisions while still being efficient.
    result = 31 * result + companyId.hashCode();
    return result;
  }
}