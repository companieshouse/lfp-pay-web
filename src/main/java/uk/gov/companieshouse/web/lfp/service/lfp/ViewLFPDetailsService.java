package uk.gov.companieshouse.web.lfp.service.lfp;

import uk.gov.companieshouse.api.model.latefilingpenalty.PayableLateFilingPenaltySession;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;

public interface ViewLFPDetailsService {

    PayableLateFilingPenaltySession createLateFilingPenaltySession(String companyNumber, String penaltyNumber, Integer amount)
            throws ServiceException;
}
