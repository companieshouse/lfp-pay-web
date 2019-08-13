package uk.gov.companieshouse.web.lfp.service.latefilingpenalty;

import uk.gov.companieshouse.api.model.latefilingpenalty.PayableLateFilingPenalty;
import uk.gov.companieshouse.api.model.latefilingpenalty.PayableLateFilingPenaltySession;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;

public interface PayableLateFilingPenaltyService {

    PayableLateFilingPenalty getPayableLateFilingPenalty(String companyNumber, String penaltyId) throws ServiceException;

    PayableLateFilingPenaltySession createLateFilingPenaltySession(String companyNumber, String penaltyNumber, Integer amount)
            throws ServiceException;
}
