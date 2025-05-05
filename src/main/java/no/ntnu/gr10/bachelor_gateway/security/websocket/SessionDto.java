package no.ntnu.gr10.bachelor_gateway.security.websocket;

import java.util.List;

public class SessionDto {
  private Integer companyId;
  private List<String> scopes;

  public SessionDto() {
    // Default constructor for deserialization
  }

  public SessionDto(Integer companyId, List<String> scopes) {
    this.companyId = companyId;
    this.scopes = scopes;
  }

  public Integer getCompanyId() {
    return companyId;
  }

  public void setCompanyId(Integer companyId) {
    this.companyId = companyId;
  }

  public List<String> getScopes() {
    return scopes;
  }

  public void setScopes(List<String> scopes) {
    this.scopes = scopes;
  }
}
