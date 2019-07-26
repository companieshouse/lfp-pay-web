package uk.gov.companieshouse.web.lfp.service.lfp;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;

import java.util.List;

public interface EnterLFPDetailsService {

    String appendToCompanyNumber(String companyNumber);

    CompanyProfileApi getCompanyProfile(String companyNumber) throws ServiceException;

    List<LateFilingPenalty> getPayableLateFilingPenalties(String companyNumber, String penaltyNumber) throws ServiceException;
}
