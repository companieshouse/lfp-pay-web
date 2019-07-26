package uk.gov.companieshouse.web.lfp.service.lfp.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenaltySession;
import uk.gov.companieshouse.api.model.latefilingpenalty.PayableLateFilingPenaltySession;
import uk.gov.companieshouse.api.model.latefilingpenalty.Transaction;
import uk.gov.companieshouse.web.lfp.api.ApiClientService;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.lfp.ViewLFPDetailsService;

import java.util.Collections;

@Service
public class ViewLFPDetailsServiceImpl implements ViewLFPDetailsService {

    private static final UriTemplate POST_LFP_URI =
            new UriTemplate("/company/{companyNumber}/penalties/late-filing/payable");

    @Autowired
    private ApiClientService apiClientService;

    @Override
    public PayableLateFilingPenaltySession createLateFilingPenaltySession(String companyNumber, String penaltyNumber, Integer amount) throws ServiceException {
        ApiClient apiClient = apiClientService.getPublicApiClient();
        ApiResponse<PayableLateFilingPenaltySession> apiResponse;

        try {
            String uri = POST_LFP_URI.expand(companyNumber).toString();
            LateFilingPenaltySession lateFilingPenaltySession = generateLateFilingPenaltySessionData(penaltyNumber, amount);
            apiResponse = apiClient.lateFilingPenalty().create(uri, lateFilingPenaltySession).execute();
        } catch (ApiErrorResponseException ex) {
            throw new ServiceException("Error retrieving Late Filing Penalty", ex);
        } catch (URIValidationException ex) {
            throw new ServiceException("Invalid URI for Late Filing Penalty", ex);
        }

        return apiResponse.getData();

    }

    private LateFilingPenaltySession generateLateFilingPenaltySessionData(String penaltyNumber, Integer amount) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(penaltyNumber);
        transaction.setAmount(amount);

        LateFilingPenaltySession lateFilingPenaltySession = new LateFilingPenaltySession();
        lateFilingPenaltySession.setTransactions(Collections.singletonList(transaction));

        return lateFilingPenaltySession;
    }

}
