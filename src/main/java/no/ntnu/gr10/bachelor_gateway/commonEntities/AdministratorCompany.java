package no.ntnu.gr10.bachelor_gateway.commonEntities;

import jakarta.persistence.*;

/**
 * Represents the association between an administrator and a company.
 * This entity is used to manage the relationship between administrators and companies,
 * including their roles and status (enabled/accepted).
 *
 * @author Anders Lund
 * @version 23.04.2025
 */
@Entity
@Table(name = "administrator_company")
public class AdministratorCompany {

  @EmbeddedId
  private AdministratorCompanyId id = new AdministratorCompanyId();

  @Column
  private boolean enabled = true;

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

  public AdministratorCompany() {
    // Default constructor for JPA
  }

  public AdministratorCompanyId getId() {
    return id;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isAccepted() {
    return accepted;
  }

  public void setAccepted(boolean accepted) {
    this.accepted = accepted;
  }

  public Administrator getAdministrator() {
    return administrator;
  }

  public void setAdministrator(Administrator administrator) {
    this.administrator = administrator;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public AdministratorRole getRole() {
    return role;
  }

  public void setRole(AdministratorRole role) {
    this.role = role;
  }
}