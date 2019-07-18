package uk.gov.companieshouse.web.lfp.service.lfp;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalties;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;

public interface EnterLFPDetailsService {

    String appendToCompanyNumber(String companyNumber);

    CompanyProfileApi getCompanyProfile(String companyNumber) throws ServiceException;

    LateFilingPenalties getLateFilingPenalties(String companyNumber, String penaltyNumber) throws ServiceException;
}
