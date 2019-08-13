package uk.gov.companieshouse.web.lfp.service.payment;

import uk.gov.companieshouse.api.model.latefilingpenalty.PayableLateFilingPenaltySession;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;


public interface PaymentService {

    /**
     * Creates a payment session in order to pay for the Late Filing Penalty.
     */
    String createPaymentSession(PayableLateFilingPenaltySession payableLateFilingPenaltySession, String companyNumber)
        throws ServiceException;
}
