package uk.gov.companieshouse.web.lfp.service.company;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;

public interface CompanyService {

    String appendToCompanyNumber(String companyNumber);

    CompanyProfileApi getCompanyProfile(String companyNumber) throws ServiceException;
}
