package no.ntnu.gr10.bachelor_gateway;

import no.ntnu.gr10.bachelor_gateway.apiKey.ApiKey;
import no.ntnu.gr10.bachelor_gateway.apiKey.ApiKeyRepository;
import no.ntnu.gr10.bachelor_gateway.company.Company;
import no.ntnu.gr10.bachelor_gateway.company.CompanyRepository;
import no.ntnu.gr10.bachelor_gateway.scope.Scope;
import no.ntnu.gr10.bachelor_gateway.scope.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Test class to implement sample data into the database.
 *
 * @author Daniel Neset
 */
@Component
public class DummyDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

  private final CompanyRepository companyRepository;
  private final ApiKeyRepository apiKeyRepository;
  private final ScopeRepository scopeRepository;

  @Autowired
  public DummyDataInitializer(CompanyRepository companyRepository, ApiKeyRepository apiKeyRepository, ScopeRepository scopeRepository){
    this.companyRepository = companyRepository;
    this.apiKeyRepository = apiKeyRepository;
    this.scopeRepository = scopeRepository;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

    /**Company company = new Company("BigBoyClub");
    ApiKey apiKey = new ApiKey(true, company, "Ree", "Raa");
    Scope scope = new Scope("S_name", "S_des");
    scopeRepository.save(scope);
    apiKey.addScope(scope);
    companyRepository.save(company);
    apiKeyRepository.save(apiKey);*/

  }
}
