package uk.gov.companieshouse.web.lfp.service.latefilingpenalty.impl;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
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
import uk.gov.companieshouse.api.handler.latefilingpenalty.e5latefilingpenalty.FinanceHealthcheckResourceHandler;
import uk.gov.companieshouse.api.handler.latefilingpenalty.e5latefilingpenalty.LateFilingPenaltyResourceHandler;
import uk.gov.companieshouse.api.handler.latefilingpenalty.e5latefilingpenalty.request.HealthcheckGet;
import uk.gov.companieshouse.api.handler.latefilingpenalty.e5latefilingpenalty.request.LateFilingPenaltyGet;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.latefilingpenalty.FinanceHealthcheck;
import uk.gov.companieshouse.api.model.latefilingpenalty.FinanceHealthcheckStatus;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalties;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.web.lfp.api.ApiClientService;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.latefilingpenalty.LateFilingPenaltyService;
import uk.gov.companieshouse.web.lfp.util.LFPTestUtility;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LateFilingPenaltyServiceImplTest {

    @Mock
    private ApiClient apiClient;

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private LateFilingPenaltyResourceHandler lateFilingPenaltyResourceHandler;

    @Mock
    private FinanceHealthcheckResourceHandler financeHealthcheckResourceHandler;

    @Mock
    private LateFilingPenaltyGet lateFilingPenaltyGet;

    @Mock
    private HealthcheckGet healthcheckGet;

    @Mock
    private ApiResponse<LateFilingPenalties> responseWithData;

    @Mock
    private ApiResponse<FinanceHealthcheck> healthcheckApiResponse;

    @InjectMocks
    private LateFilingPenaltyService mockLateFilingPenaltyService = new LateFilingPenaltyServiceImpl();

    private static final String COMPANY_NUMBER = "12345678";

    private static final String PENALTY_NUMBER = "98765432";

    private static final String GET_LFP_URI = "/company/" + COMPANY_NUMBER + "/penalties/late-filing";

    private static final String GET_FINANCE_HEALTHCHECK_URI = "/healthcheck/finance-system";

    private static final String MAINTENANCE_END_TIME = "2019-11-08T23:00:12Z";

    @BeforeEach
    private void init() {

        when(apiClientService.getPublicApiClient()).thenReturn(apiClient);
    }

    /**
     * Get Payable Late Filing Penalty tests.
     */
    @Test
    @DisplayName("Get Payable Late Filing Penalties - Success Path")
    void getPayableLateFilingPenaltiesSuccess() throws ServiceException, ApiErrorResponseException, URIValidationException {
        when(apiClient.lateFilingPenalty()).thenReturn(lateFilingPenaltyResourceHandler);

        LateFilingPenalty validLateFilingPenalty = LFPTestUtility.validLateFilingPenalty(PENALTY_NUMBER);

        when(lateFilingPenaltyResourceHandler.get(GET_LFP_URI)).thenReturn(lateFilingPenaltyGet);
        when(lateFilingPenaltyGet.execute()).thenReturn(responseWithData);

        when(responseWithData.getData()).thenReturn(
                LFPTestUtility.oneLateFilingPenalties(validLateFilingPenalty)
        );

        List<LateFilingPenalty> payableLateFilingPenalties =
                mockLateFilingPenaltyService.getLateFilingPenalties(COMPANY_NUMBER, PENALTY_NUMBER);

        assertEquals(1, payableLateFilingPenalties.size());
        assertEquals(validLateFilingPenalty, payableLateFilingPenalties.get(0));
    }

    @Test
    @DisplayName("Get Payable Late Filing Penalties - Two Unpaid Penalties")
    void getPayableLateFilingPenaltiesTwoUnpaid() throws ServiceException, ApiErrorResponseException, URIValidationException {
        when(apiClient.lateFilingPenalty()).thenReturn(lateFilingPenaltyResourceHandler);

        LateFilingPenalty validLateFilingPenalty1 = LFPTestUtility.validLateFilingPenalty(PENALTY_NUMBER);
        LateFilingPenalty validLateFilingPenalty2 = LFPTestUtility.validLateFilingPenalty(PENALTY_NUMBER);

        when(lateFilingPenaltyResourceHandler.get(GET_LFP_URI)).thenReturn(lateFilingPenaltyGet);
        when(lateFilingPenaltyGet.execute()).thenReturn(responseWithData);

        when(responseWithData.getData()).thenReturn(
                LFPTestUtility.twoLateFilingPenalties(validLateFilingPenalty1, validLateFilingPenalty2)
        );

        List<LateFilingPenalty> payableLateFilingPenalties =
                mockLateFilingPenaltyService.getLateFilingPenalties(COMPANY_NUMBER, PENALTY_NUMBER);

        assertEquals(2, payableLateFilingPenalties.size());
        assertEquals(validLateFilingPenalty1, payableLateFilingPenalties.get(0));
        assertEquals(validLateFilingPenalty2, payableLateFilingPenalties.get(1));
    }

    @Test
    @DisplayName("Get Payable Late Filing Penalties - No Unpaid Penalties")
    void getPayableLateFilingPenaltiesNoPenalties() throws ServiceException, ApiErrorResponseException, URIValidationException {
        when(apiClient.lateFilingPenalty()).thenReturn(lateFilingPenaltyResourceHandler);

        when(lateFilingPenaltyResourceHandler.get(GET_LFP_URI)).thenReturn(lateFilingPenaltyGet);
        when(lateFilingPenaltyGet.execute()).thenReturn(responseWithData);

        when(responseWithData.getData()).thenReturn(
                LFPTestUtility.noPenalties()
        );

        List<LateFilingPenalty> payableLateFilingPenalties =
                mockLateFilingPenaltyService.getLateFilingPenalties(COMPANY_NUMBER, PENALTY_NUMBER);

        assertEquals(0, payableLateFilingPenalties.size());
    }

    @Test
    @DisplayName("Get Payable Late Filing Penalties - Paid Penalty")
    void getPayableLateFilingPenaltiesPaidPenalty() throws ServiceException, ApiErrorResponseException, URIValidationException {
        when(apiClient.lateFilingPenalty()).thenReturn(lateFilingPenaltyResourceHandler);

        LateFilingPenalty paidLateFilingPenalty = LFPTestUtility.paidLateFilingPenalty(PENALTY_NUMBER);

        when(lateFilingPenaltyResourceHandler.get(GET_LFP_URI)).thenReturn(lateFilingPenaltyGet);
        when(lateFilingPenaltyGet.execute()).thenReturn(responseWithData);

        when(responseWithData.getData()).thenReturn(
                LFPTestUtility.oneLateFilingPenalties(paidLateFilingPenalty)
        );

        List<LateFilingPenalty> payableLateFilingPenalties =
                mockLateFilingPenaltyService.getLateFilingPenalties(COMPANY_NUMBER, "84738483");

        assertEquals(0, payableLateFilingPenalties.size());
    }

    @Test
    @DisplayName("Get Payable Late Filing Penalties - Throws ApiErrorResponseException")
    void getPayableLateFilingPenaltiesThrowsApiErrorResponseException() throws ApiErrorResponseException, URIValidationException {
        when(apiClient.lateFilingPenalty()).thenReturn(lateFilingPenaltyResourceHandler);

        when(lateFilingPenaltyResourceHandler.get(GET_LFP_URI)).thenReturn(lateFilingPenaltyGet);
        when(lateFilingPenaltyGet.execute()).thenThrow(ApiErrorResponseException.class);

        assertThrows(ServiceException.class, () ->
                mockLateFilingPenaltyService.getLateFilingPenalties(COMPANY_NUMBER, PENALTY_NUMBER));
    }

    @Test
    @DisplayName("Get Payable Late Filing Penalties - Throws URIValidationException")
    void getPayableLateFilingPenaltiesThrowsURIValidationException() throws ApiErrorResponseException, URIValidationException {
        when(apiClient.lateFilingPenalty()).thenReturn(lateFilingPenaltyResourceHandler);

        when(lateFilingPenaltyResourceHandler.get(GET_LFP_URI)).thenReturn(lateFilingPenaltyGet);
        when(lateFilingPenaltyGet.execute()).thenThrow(URIValidationException.class);

        assertThrows(ServiceException.class, () ->
                mockLateFilingPenaltyService.getLateFilingPenalties(COMPANY_NUMBER, PENALTY_NUMBER));
    }

    @Test
    @DisplayName("Get Finance Healthcheck - Success Path")
    void getFinanceHealthcheckSuccessPath() throws ServiceException, ApiErrorResponseException, URIValidationException {
        when(apiClient.financeHealthcheckResourceHandler()).thenReturn(financeHealthcheckResourceHandler);

        FinanceHealthcheck financeHealthcheckHealthy = LFPTestUtility.financeHealthcheckHealthy();

        when(financeHealthcheckResourceHandler.get(GET_FINANCE_HEALTHCHECK_URI)).thenReturn(healthcheckGet);
        when(healthcheckGet.execute()).thenReturn(healthcheckApiResponse);
        when(healthcheckApiResponse.getData()).thenReturn(financeHealthcheckHealthy);

        FinanceHealthcheck financeHealthcheck = mockLateFilingPenaltyService.checkFinanceSystemAvailableTime();

        assertEquals(FinanceHealthcheckStatus.HEALTHY.getStatus(), financeHealthcheck.getMessage());
        assertNull(financeHealthcheck.getMaintenanceEndTime());
    }

    @Test
    @DisplayName("Get Finance Healthcheck - Planned Maintenance")
    void getFinanceHealthcheckPlannedMaintenance() throws ServiceException, ApiErrorResponseException, URIValidationException {
        when(apiClient.financeHealthcheckResourceHandler()).thenReturn(financeHealthcheckResourceHandler);

        when(financeHealthcheckResourceHandler.get(GET_FINANCE_HEALTHCHECK_URI)).thenReturn(healthcheckGet);
        when(healthcheckGet.execute()).thenThrow(new ApiErrorResponseException(serviceUnavailablePlannedMaintenance()));

        FinanceHealthcheck financeHealthcheck = mockLateFilingPenaltyService.checkFinanceSystemAvailableTime();

        assertEquals(FinanceHealthcheckStatus.UNHEALTHY_PLANNED_MAINTENANCE.getStatus(), financeHealthcheck.getMessage());
        assertEquals(MAINTENANCE_END_TIME, financeHealthcheck.getMaintenanceEndTime());
    }

    @Test
    @DisplayName("Get Finance Healthcheck - Throws URIValidationException not Planned Maintenance")
    void getFinanceHealthcheckThrowsURIValidationException() throws ApiErrorResponseException, URIValidationException {
        when(apiClient.financeHealthcheckResourceHandler()).thenReturn(financeHealthcheckResourceHandler);

        when(financeHealthcheckResourceHandler.get(GET_FINANCE_HEALTHCHECK_URI)).thenReturn(healthcheckGet);
        when(healthcheckGet.execute()).thenThrow(URIValidationException.class);

        assertThrows(ServiceException.class, () ->
                mockLateFilingPenaltyService.checkFinanceSystemAvailableTime());
    }

    @Test
    @DisplayName("Get Finance Healthcheck - Throws ApiErrorResponseException")
    void getFinanceHealthcheckThrowsApiErrorResponseException() throws ApiErrorResponseException, URIValidationException {
        when(apiClient.financeHealthcheckResourceHandler()).thenReturn(financeHealthcheckResourceHandler);

        when(financeHealthcheckResourceHandler.get(GET_FINANCE_HEALTHCHECK_URI)).thenReturn(healthcheckGet);
        when(healthcheckGet.execute()).thenThrow(ApiErrorResponseException.class);

        assertThrows(ServiceException.class, () ->
                mockLateFilingPenaltyService.checkFinanceSystemAvailableTime());
    }

    public static HttpResponseException.Builder serviceUnavailablePlannedMaintenance() {
        HttpHeaders headers = new HttpHeaders();
        HttpResponseException.Builder response =
                new HttpResponseException.Builder(503, "message: test", headers);
        response.setContent(
                "{\"message\":\"" + FinanceHealthcheckStatus.UNHEALTHY_PLANNED_MAINTENANCE.getStatus()
                + "\",\"maintenance_end_time\":\"" + MAINTENANCE_END_TIME + "\"}");

        return response;
    }
}
