package uk.gov.companieshouse.web.lfp.service.latefilingpenalty.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalties;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenaltySession;
import uk.gov.companieshouse.api.model.latefilingpenalty.PayableLateFilingPenaltySession;
import uk.gov.companieshouse.api.model.latefilingpenalty.Transaction;
import uk.gov.companieshouse.web.lfp.api.ApiClientService;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.latefilingpenalty.LateFilingPenaltyService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LateFilingPenaltyServiceImpl implements LateFilingPenaltyService {

    private static final UriTemplate GET_LFP_URI =
            new UriTemplate("/company/{companyNumber}/penalties/late-filing");

    private static final UriTemplate POST_LFP_URI =
            new UriTemplate("/company/{companyNumber}/penalties/late-filing/payable");

    private static final String PENALTY_TYPE = "penalty";

    @Autowired
    private ApiClientService apiClientService;

    @Override
    public List<LateFilingPenalty> getPayableLateFilingPenalties(String companyNumber, String penaltyNumber) throws ServiceException {
        ApiClient apiClient = apiClientService.getPublicApiClient();
        LateFilingPenalties lateFilingPenalties;

        try {
            String uri = GET_LFP_URI.expand(companyNumber).toString();
            lateFilingPenalties = apiClient.lateFilingPenalty().get(uri).execute().getData();
        } catch (ApiErrorResponseException ex) {
            throw new ServiceException("Error retrieving Late Filing Penalty", ex);
        } catch (URIValidationException ex) {
            throw new ServiceException("Invalid URI for Late Filing Penalty", ex);
        }

        List<LateFilingPenalty> payableLateFilingPenalties = new ArrayList<>();

        // If no Late Filing Penalties for company return an empty list.
        if (lateFilingPenalties.getTotalResults() == 0) {
            return payableLateFilingPenalties;
        }

        // Compile all payable penalties into one List to be returned.
        // Always include penalty with the ID provided so the correct error page can be displayed.
        for (LateFilingPenalty lateFilingPenalty: lateFilingPenalties.getItems()) {
            if ((!lateFilingPenalty.getPaid() && lateFilingPenalty.getType().equals(PENALTY_TYPE))
                    || lateFilingPenalty.getId().equals(penaltyNumber)) {
                payableLateFilingPenalties.add(lateFilingPenalty);
            }
        }

        return payableLateFilingPenalties;
    }

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