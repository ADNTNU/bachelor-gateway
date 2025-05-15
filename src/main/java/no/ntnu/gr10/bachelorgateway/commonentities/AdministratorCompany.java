package no.ntnu.gr10.bachelorgateway.commonentities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the association between an administrator and a company.
 * This entity is used to manage the relationship between administrators and companies,
 * including their roles and status (enabled/accepted).
 *
 * @author Anders Lund
 * @version 23.04.2025
 */
@Getter
@Entity
@Table(name = "administrator_company")
public class AdministratorCompany {

  @EmbeddedId
  private AdministratorCompanyId id = new AdministratorCompanyId();

  @Setter
  @Column
  private boolean enabled = true;

  @Setter
  @Column
  private boolean accepted = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("administratorId")
  private Administrator administrator;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("companyId")
  private Company company;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AdministratorRole role;

  /**
   * Default constructor for serialization.
   */
  public AdministratorCompany() {
    // Default constructor for JPA
  }

  /**
   * Sets the administrator of the administrator-company relation.
   *
   * @param administrator The administrator to set
   * @throws IllegalArgumentException if the administrator is null
   */
  public void setAdministrator(Administrator administrator) {
    if (administrator == null) {
      throw new IllegalArgumentException("administrator cannot be null");
    }
    this.administrator = administrator;
  }

  /**
   * Sets the company of the administrator-company relation.
   *
   * @param company The company to set
   * @throws IllegalArgumentException if the company is null
   */
  public void setCompany(Company company) {
    if (company == null) {
      throw new IllegalArgumentException("company cannot be null");
    }
    this.company = company;
  }

  /**
   * Sets the role of the administrator-company relation.
   *
   * @param role The role to set
   * @throws IllegalArgumentException if the role is null
   */
  public void setRole(AdministratorRole role) {
    if (role == null) {
      throw new IllegalArgumentException("role cannot be null");
    }
    this.role = role;
  }
}