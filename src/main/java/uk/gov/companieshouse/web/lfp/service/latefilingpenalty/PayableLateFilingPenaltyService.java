package uk.gov.companieshouse.web.lfp.service.latefilingpenalty;

import uk.gov.companieshouse.api.model.latefilingpenalty.PayableLateFilingPenalty;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;

public interface PayableLateFilingPenaltyService {

    PayableLateFilingPenalty getPayableLateFilingPenalty(String companyNumber, String penaltyNumber) throws ServiceException;

}
