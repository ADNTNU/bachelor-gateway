package no.ntnu.gr10.bachelor_gateway.company;

import no.ntnu.gr10.bachelor_gateway.company.dto.CompanySimpleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for getting company-related information.
 * Has methods for CRUD operations.
 *
 * @author Anders Lund
 * @version 05.04.2025
 */
@Service
public class CompanyService {
  final CompanyRepository companyRepository;

  @Autowired
  public CompanyService(CompanyRepository companyRepository) {
    this.companyRepository = companyRepository;
  }

  /**
   * Get the company by ID.
   *
   * @param id the ID of the company
   * @return the company
   */
  public Company getCompanyById(Long id) {
    return companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Company not found"));
  }

  public List<CompanySimpleDto> getSimpleCompaniesByAdministratorId(Long administratorId) {
    return companyRepository.findByAdministratorId(administratorId).stream()
            .map(company -> new CompanySimpleDto(company.getId(), company.getName()))
            .toList();
  }

  public Company createCompany(Company company) {
    return companyRepository.save(company);
  }

//  TODO: Add methods for creating, updating, and deleting companies
}