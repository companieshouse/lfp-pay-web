package uk.gov.companieshouse.web.lfp.service.latefilingpenalty.impl;

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
import uk.gov.companieshouse.api.handler.latefilingpenalty.e5latefilingpenalty.LateFilingPenaltyResourceHandler;
import uk.gov.companieshouse.api.handler.latefilingpenalty.e5latefilingpenalty.request.LateFilingPenaltyGet;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalties;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.api.model.latefilingpenalty.PayableLateFilingPenaltySession;
import uk.gov.companieshouse.web.lfp.api.ApiClientService;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.latefilingpenalty.LateFilingPenaltyService;
import uk.gov.companieshouse.web.lfp.util.LFPTestUtility;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LateFilingPenaltyServiceImplTest {

    @Mock
    private ApiClient apiClient;

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private LateFilingPenaltyResourceHandler lateFilingPenaltyResourceHandler;

    @Mock
    private LateFilingPenaltyGet lateFilingPenaltyGet;

    @Mock
    private ApiResponse<LateFilingPenalties> responseWithData;

    @Mock
    private ApiResponse<PayableLateFilingPenaltySession> sessionResponseWithData;

    @InjectMocks
    private LateFilingPenaltyService mockLateFilingPenaltyService = new LateFilingPenaltyServiceImpl();

    private static final String COMPANY_NUMBER = "12345678";

    private static final String PENALTY_NUMBER = "98765432";

    private static final Integer AMOUNT = 750;

    private static final String GET_LFP_URI = "/company/" + COMPANY_NUMBER + "/penalties/late-filing";

    private static final String POST_LFP_URI = "/company/" + COMPANY_NUMBER + "/penalties/late-filing/payable";

    @BeforeEach
    private void init() {

        when(apiClientService.getPublicApiClient()).thenReturn(apiClient);

        when(apiClient.lateFilingPenalty()).thenReturn(lateFilingPenaltyResourceHandler);
    }

    /**
     * Get Payable Late Filing Penalty tests.
     */
    @Test
    @DisplayName("Get Payable Late Filing Penalties - Success Path")
    void getPayableLateFilingPenaltiesSuccess() throws ServiceException, ApiErrorResponseException, URIValidationException {

        LateFilingPenalty validLateFilingPenalty = LFPTestUtility.validLateFilingPenalty(PENALTY_NUMBER);

        when(lateFilingPenaltyResourceHandler.get(GET_LFP_URI)).thenReturn(lateFilingPenaltyGet);
        when(lateFilingPenaltyGet.execute()).thenReturn(responseWithData);

        when(responseWithData.getData()).thenReturn(
                LFPTestUtility.oneLateFilingPenalties(validLateFilingPenalty)
        );

        List<LateFilingPenalty> payableLateFilingPenalties =
                mockLateFilingPenaltyService.getlateFilingPenalties(COMPANY_NUMBER, PENALTY_NUMBER);

        assertEquals(1, payableLateFilingPenalties.size());
        assertEquals(validLateFilingPenalty, payableLateFilingPenalties.get(0));
    }

    @Test
    @DisplayName("Get Payable Late Filing Penalties - Two Unpaid Penalties")
    void getPayableLateFilingPenaltiesTwoUnpaid() throws ServiceException, ApiErrorResponseException, URIValidationException {

        LateFilingPenalty validLateFilingPenalty1 = LFPTestUtility.validLateFilingPenalty(PENALTY_NUMBER);
        LateFilingPenalty validLateFilingPenalty2 = LFPTestUtility.validLateFilingPenalty("77777777");

        when(lateFilingPenaltyResourceHandler.get(GET_LFP_URI)).thenReturn(lateFilingPenaltyGet);
        when(lateFilingPenaltyGet.execute()).thenReturn(responseWithData);

        when(responseWithData.getData()).thenReturn(
                LFPTestUtility.twoLateFilingPenalties(validLateFilingPenalty1, validLateFilingPenalty2)
        );

        List<LateFilingPenalty> payableLateFilingPenalties =
                mockLateFilingPenaltyService.getlateFilingPenalties(COMPANY_NUMBER, PENALTY_NUMBER);

        assertEquals(2, payableLateFilingPenalties.size());
        assertEquals(validLateFilingPenalty1, payableLateFilingPenalties.get(0));
        assertEquals(validLateFilingPenalty2, payableLateFilingPenalties.get(1));
    }

    @Test
    @DisplayName("Get Payable Late Filing Penalties - No Unpaid Penalties")
    void getPayableLateFilingPenaltiesNoPenalties() throws ServiceException, ApiErrorResponseException, URIValidationException {

        when(lateFilingPenaltyResourceHandler.get(GET_LFP_URI)).thenReturn(lateFilingPenaltyGet);
        when(lateFilingPenaltyGet.execute()).thenReturn(responseWithData);

        when(responseWithData.getData()).thenReturn(
                LFPTestUtility.noPenalties()
        );

        List<LateFilingPenalty> payableLateFilingPenalties =
                mockLateFilingPenaltyService.getlateFilingPenalties(COMPANY_NUMBER, PENALTY_NUMBER);

        assertEquals(0, payableLateFilingPenalties.size());
    }

    @Test
    @DisplayName("Get Payable Late Filing Penalties - Paid Penalty")
    void getPayableLateFilingPenaltiesPaidPenalty() throws ServiceException, ApiErrorResponseException, URIValidationException {

        LateFilingPenalty paidLateFilingPenalty = LFPTestUtility.paidLateFilingPenalty(PENALTY_NUMBER);

        when(lateFilingPenaltyResourceHandler.get(GET_LFP_URI)).thenReturn(lateFilingPenaltyGet);
        when(lateFilingPenaltyGet.execute()).thenReturn(responseWithData);

        when(responseWithData.getData()).thenReturn(
                LFPTestUtility.oneLateFilingPenalties(paidLateFilingPenalty)
        );

        List<LateFilingPenalty> payableLateFilingPenalties =
                mockLateFilingPenaltyService.getlateFilingPenalties(COMPANY_NUMBER, "84738483");

        assertEquals(0, payableLateFilingPenalties.size());
    }

    @Test
    @DisplayName("Get Payable Late Filing Penalties - Throws ApiErrorResponseException")
    void getPayableLateFilingPenaltiesThrowsApiErrorResponseException() throws ApiErrorResponseException, URIValidationException {

        when(lateFilingPenaltyResourceHandler.get(GET_LFP_URI)).thenReturn(lateFilingPenaltyGet);
        when(lateFilingPenaltyGet.execute()).thenThrow(ApiErrorResponseException.class);

        assertThrows(ServiceException.class, () ->
                mockLateFilingPenaltyService.getlateFilingPenalties(COMPANY_NUMBER, PENALTY_NUMBER));
    }

    @Test
    @DisplayName("Get Payable Late Filing Penalties - Throws URIValidationException")
    void getPayableLateFilingPenaltiesThrowsURIValidationException() throws ApiErrorResponseException, URIValidationException {

        when(lateFilingPenaltyResourceHandler.get(GET_LFP_URI)).thenReturn(lateFilingPenaltyGet);
        when(lateFilingPenaltyGet.execute()).thenThrow(URIValidationException.class);

        assertThrows(ServiceException.class, () ->
                mockLateFilingPenaltyService.getlateFilingPenalties(COMPANY_NUMBER, PENALTY_NUMBER));
    }
}
