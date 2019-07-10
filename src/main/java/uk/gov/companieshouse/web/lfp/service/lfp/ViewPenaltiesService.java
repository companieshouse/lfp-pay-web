package uk.gov.companieshouse.web.lfp.service.lfp;

import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;

public interface ViewPenaltiesService {

    LateFilingPenalty getLateFilingPenalty (String companyNumber) throws ServiceException;
}
