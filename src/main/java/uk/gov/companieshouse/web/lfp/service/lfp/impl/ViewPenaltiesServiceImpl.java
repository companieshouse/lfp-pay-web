package uk.gov.companieshouse.web.lfp.service.lfp.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.web.lfp.api.ApiClientService;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.lfp.ViewPenaltiesService;

@Service
public class ViewPenaltiesServiceImpl implements ViewPenaltiesService {

    private static final UriTemplate GET_LFP_URI =
            new UriTemplate("/company/{companyNumber}/penalties/late-filing");

    @Autowired
    private ApiClientService apiClientService;

    @Override
    public LateFilingPenalty getLateFilingPenalty(String companyNumber) throws ServiceException {
        ApiClient apiClient = apiClientService.getPublicApiClient();
        ApiResponse<LateFilingPenalty> apiResponse;

        try {
            String uri = GET_LFP_URI.expand(companyNumber).toString();
            apiResponse = apiClient.lateFilingPenalty().get(uri).execute();
        } catch (ApiErrorResponseException ex) {
            throw new ServiceException("Error retrieving Late Filing Penalty", ex);
        } catch (URIValidationException ex) {
            throw new ServiceException("Invalid URI for Late Filing Penalty", ex);
        }

        return apiResponse.getData();
    }

}
