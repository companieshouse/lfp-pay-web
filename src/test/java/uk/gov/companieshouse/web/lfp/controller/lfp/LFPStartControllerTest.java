package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.security.WebSecurity;
import uk.gov.companieshouse.web.lfp.service.latefilingpenalty.LateFilingPenaltyService;
import uk.gov.companieshouse.web.lfp.service.navigation.NavigatorService;
import uk.gov.companieshouse.web.lfp.util.LFPTestUtility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({WebSecurity.class})
class LFPStartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LateFilingPenaltyService mockLateFilingPenaltyService;

    @Mock
    private NavigatorService mockNavigatorService;

    @InjectMocks
    private LFPStartController controller;

    @BeforeEach
    private void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private static final String LFP_START_PATH = "/late-filing-penalty";
    private static final String LFP_START_PATH_PARAM = "/late-filing-penalty?start=0";
    private static final String MOCK_CONTROLLER_PATH = UrlBasedViewResolver.REDIRECT_URL_PREFIX + "mockControllerPath";

    private static final String LFP_START_VIEW = "lfp/home";
    private static final String ERROR_VIEW = "error";
    private static final String LFP_SERVICE_UNAVAILABLE = "lfp/serviceUnavailable";

    private static final String DATE_MODEL_ATTR = "date";

    private static final String MAINTENANCE_END_TIME = "2001-02-03T04:05:06-00:00";

    @Test
    @DisplayName("Get View Start Page - success path")
    void getRequestSuccess() throws Exception {

        configureValidFinanceHealthcheckResponse();

        this.mockMvc.perform(get(LFP_START_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(LFP_START_VIEW));

        verify(mockLateFilingPenaltyService, times(1)).checkFinanceSystemAvailableTime();

    }

    @Test
    @DisplayName("Get View Start Page - error checking finance system")
    void getRequestErrorCheckingFinanceSystem() throws Exception {

        configureErrorFinanceHealthcheckResponse();

        this.mockMvc.perform(get(LFP_START_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_VIEW));

        verify(mockLateFilingPenaltyService, times(1)).checkFinanceSystemAvailableTime();

    }

    @Test
    @DisplayName("Get View Start Page - finance system offline")
    void getRequestFinanceSystemOffline() throws Exception {

        configureUnhealthyFinanceHealthcheckResponse(MAINTENANCE_END_TIME);

        this.mockMvc.perform(get(LFP_START_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(LFP_SERVICE_UNAVAILABLE))
                .andExpect(model().attributeExists(DATE_MODEL_ATTR))
                .andExpect(model().attribute(DATE_MODEL_ATTR, convertTimeToModelFormat(MAINTENANCE_END_TIME)));

        verify(mockLateFilingPenaltyService, times(1)).checkFinanceSystemAvailableTime();

    }

    @Test
    @DisplayName("Get View Start Page - redirect to sign in")
    void getRequestRedirectToSignInWhenVisitFromGovUk() throws Exception {

        configureValidFinanceHealthcheckResponse();
        configureNextController();

        this.mockMvc.perform(get(LFP_START_PATH_PARAM))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(MOCK_CONTROLLER_PATH));

        verify(mockLateFilingPenaltyService, times(1)).checkFinanceSystemAvailableTime();
    }

    @Test
    @DisplayName("Get View Start Page - invalid finance healthcheck state")
    void getRequestFinanceSystemInvalidState() throws Exception {

        configureInvalidFinanceHealthcheckResponse();

        this.mockMvc.perform(get(LFP_START_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_VIEW));

        verify(mockLateFilingPenaltyService, times(1)).checkFinanceSystemAvailableTime();

    }

    @Test
    @DisplayName("Post View Start Page - success path")
    void postRequestSuccess() throws Exception {

        configureNextController();

        this.mockMvc.perform(post(LFP_START_PATH))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(MOCK_CONTROLLER_PATH));
    }

    private void configureValidFinanceHealthcheckResponse()
            throws ServiceException {

        when(mockLateFilingPenaltyService.checkFinanceSystemAvailableTime())
                .thenReturn(LFPTestUtility.financeHealthcheckHealthy());
    }

    private void configureErrorFinanceHealthcheckResponse()
            throws ServiceException {

        doThrow(ServiceException.class)
                .when(mockLateFilingPenaltyService).checkFinanceSystemAvailableTime();
    }

    private void configureUnhealthyFinanceHealthcheckResponse(String maintenanceEndTime)
            throws ServiceException {
        when(mockLateFilingPenaltyService.checkFinanceSystemAvailableTime())
                .thenReturn(LFPTestUtility.financeHealthcheckServiceUnavailable(maintenanceEndTime));
    }

    private void configureInvalidFinanceHealthcheckResponse()
            throws ServiceException {
        when(mockLateFilingPenaltyService.checkFinanceSystemAvailableTime())
                .thenReturn(LFPTestUtility.financeHealthcheckServiceInvalid());
    }

    private String convertTimeToModelFormat(String inputTime) throws ParseException {
        DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        DateFormat displayDateFormat = new SimpleDateFormat("h:mm a z 'on' EEEE d MMMM yyyy");
        return displayDateFormat.format(
                inputDateFormat.parse(inputTime));
    }

    private void configureNextController() {
        when(mockNavigatorService.getNextControllerRedirect(any(), any()))
                .thenReturn(MOCK_CONTROLLER_PATH);
    }
}
