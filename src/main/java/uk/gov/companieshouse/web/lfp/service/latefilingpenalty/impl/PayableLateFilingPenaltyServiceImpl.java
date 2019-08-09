package uk.gov.companieshouse.web.lfp.service.latefilingpenalty.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.latefilingpenalty.PayableLateFilingPenalty;
import uk.gov.companieshouse.web.lfp.api.ApiClientService;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.latefilingpenalty.PayableLateFilingPenaltyService;

@Service
public class PayableLateFilingPenaltyServiceImpl implements PayableLateFilingPenaltyService {

    private static final UriTemplate GET_PAYABLE_LFP_URI =
            new UriTemplate("/company/{companyNumber}/penalties/late-filing/payable/{penaltyNumber}");

    @Autowired
    private ApiClientService apiClientService;

    @Override
    public PayableLateFilingPenalty getPayableLateFilingPenalty(String companyNumber, String penaltyNumber) throws ServiceException {
        ApiClient apiClient = apiClientService.getPublicApiClient();
        PayableLateFilingPenalty payableLateFilingPenalty;

        try {
            String uri = GET_PAYABLE_LFP_URI.expand(companyNumber, penaltyNumber).toString();
            payableLateFilingPenalty = apiClient.payableLateFilingPenalty().get(uri).execute().getData();
        } catch (ApiErrorResponseException ex) {
            throw new ServiceException("Error retrieving Payable Late Filing Penalty", ex);
        } catch (URIValidationException ex) {
            throw new ServiceException("Invalid URI for Payable Late Filing Penalty", ex);
        }

        return payableLateFilingPenalty;

    }

}
