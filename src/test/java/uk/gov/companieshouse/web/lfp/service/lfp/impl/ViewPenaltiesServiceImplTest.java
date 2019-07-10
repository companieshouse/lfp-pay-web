package uk.gov.companieshouse.web.lfp.service.lfp.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.latefilingpenalty.LateFilingPenaltyResourceHandler;
import uk.gov.companieshouse.api.handler.latefilingpenalty.request.LateFilingPenaltyGet;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.web.lfp.api.ApiClientService;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.lfp.ViewPenaltiesService;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ViewPenaltiesServiceImplTest {

    @Mock
    private ApiClient apiClient;

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private LateFilingPenaltyResourceHandler lateFilingPenaltyResourceHandler;

    @Mock
    private LateFilingPenaltyGet lateFilingPenaltyGet;

    @Mock
    private ApiResponse<LateFilingPenalty> responseWithData;

    @Mock
    private LateFilingPenalty lateFilingPenalty;

    @InjectMocks
    private ViewPenaltiesService viewPenaltiesService = new ViewPenaltiesServiceImpl();

    private static final String COMPANY_NUMBER = "12345678";

    private static final String LFP_URI = "/company/" + COMPANY_NUMBER + "/penalties/late-filing";

    @BeforeEach
    private void init() {

        when(apiClientService.getPublicApiClient()).thenReturn(apiClient);

        when(apiClient.lateFilingPenalty()).thenReturn(lateFilingPenaltyResourceHandler);

        when(lateFilingPenaltyResourceHandler.get(LFP_URI)).thenReturn(lateFilingPenaltyGet);
    }

    @Test
    @DisplayName("Get Late Filing Penalty - Success Path")
    void getLateFilingPenaltySuccess() throws ServiceException, ApiErrorResponseException, URIValidationException {

        when(lateFilingPenaltyGet.execute()).thenReturn(responseWithData);

        when(responseWithData.getData()).thenReturn(lateFilingPenalty);

        LateFilingPenalty returnedLateFilingPenalty = viewPenaltiesService.getLateFilingPenalty(COMPANY_NUMBER);

        assertEquals(lateFilingPenalty, returnedLateFilingPenalty);
    }

    @Test
    @DisplayName("Get Late Filing Penalty - Throws APIErrorResponseException")
    void getLateFilingPenaltyThrowsAPIErrorResponseException() throws ApiErrorResponseException, URIValidationException {

        when(lateFilingPenaltyGet.execute()).thenThrow(ApiErrorResponseException.class);

        assertThrows(ServiceException.class, () ->
                viewPenaltiesService.getLateFilingPenalty(COMPANY_NUMBER));
    }

    @Test
    @DisplayName("Get Late Filing Penalty - Throws URIValidationException")
    void getLateFilingPenaltyThrowsURIValidationException() throws ApiErrorResponseException, URIValidationException {

        when(lateFilingPenaltyGet.execute()).thenThrow(URIValidationException.class);

        assertThrows(ServiceException.class, () ->
                viewPenaltiesService.getLateFilingPenalty(COMPANY_NUMBER));
    }
}
