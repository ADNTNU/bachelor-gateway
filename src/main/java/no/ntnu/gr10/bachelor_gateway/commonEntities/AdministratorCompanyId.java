package no.ntnu.gr10.bachelor_gateway.commonEntities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class AdministratorCompanyId implements Serializable {
  private Long administratorId;
  private Long companyId;

  public AdministratorCompanyId() {
//    Default constructor for JPA
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AdministratorCompanyId that)) return false;

    if (!administratorId.equals(that.administratorId)) return false;
    return companyId.equals(that.companyId);
  }

  @Override
  public int hashCode() {
    int result = administratorId.hashCode();
//    31 is a prime number, commonly used in hashCode implementations
//    to reduce the chance of collisions while still being efficient.
    result = 31 * result + companyId.hashCode();
    return result;
  }
}