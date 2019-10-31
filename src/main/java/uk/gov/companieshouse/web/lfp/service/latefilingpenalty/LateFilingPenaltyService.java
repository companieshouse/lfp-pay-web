package uk.gov.companieshouse.web.lfp.service.latefilingpenalty;

import uk.gov.companieshouse.api.model.latefilingpenalty.FinanceHealthcheck;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;

import java.util.List;

public interface LateFilingPenaltyService {

    List<LateFilingPenalty> getLateFilingPenalties(String companyNumber, String penaltyNumber) throws ServiceException;
    FinanceHealthcheck checkFinanceSystemAvailableTime() throws ServiceException;
}
