package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.company.CompanyService;
import uk.gov.companieshouse.web.lfp.service.latefilingpenalty.LateFilingPenaltyService;
import uk.gov.companieshouse.web.lfp.service.navigation.NavigatorService;
import uk.gov.companieshouse.web.lfp.util.LFPTestUtility;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PenaltyPaidControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LateFilingPenaltyService mockLateFilingPenaltyService;

    @Mock
    private CompanyService mockCompanyService;

    @Mock
    private NavigatorService mockNavigatorService;

    @InjectMocks
    private PenaltyPaidController controller;

    private static final String COMPANY_NUMBER = "12345678";
    private static final String PENALTY_NUMBER = "44444444";

    private static final String PENALTY_PAID_PATH = "/late-filing-penalty/company/" + COMPANY_NUMBER + "/penalty/" + PENALTY_NUMBER + "/penalty-paid";

    private static final String LFP_PENALTY_PAID = "lfp/penaltyPaid";
    private static final String ERROR_VIEW = "error";
    private static final String BACK_BUTTON_MODEL_ATTR = "backButton";
    private static final String COMPANY_NAME_ATTR = "companyName";
    private static final String PENALTY_NUMBER_ATTR = "penaltyNumber";

    private static final String MOCK_CONTROLLER_PATH = UrlBasedViewResolver.REDIRECT_URL_PREFIX + "mockControllerPath";

    @BeforeEach
    private void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Get Penalty Paid - success path")
    void getRequestSuccess() throws Exception {

        configurePreviousController();
        configureValidCompanyProfile(COMPANY_NUMBER);

        this.mockMvc.perform(get(PENALTY_PAID_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(LFP_PENALTY_PAID))
                .andExpect(model().attributeExists(BACK_BUTTON_MODEL_ATTR))
                .andExpect(model().attributeExists(COMPANY_NAME_ATTR))
                .andExpect(model().attributeExists(PENALTY_NUMBER_ATTR));

        verify(mockCompanyService, times(1)).getCompanyProfile(COMPANY_NUMBER);
    }

    @Test
    @DisplayName("Get Penalty Paid - error retrieving company details")
    void getRequestErrorRetrievingCompanyDetails() throws Exception {

        configureErrorRetrievingCompany(COMPANY_NUMBER);

        this.mockMvc.perform(get(PENALTY_PAID_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_VIEW));

        verify(mockCompanyService, times(1)).getCompanyProfile(COMPANY_NUMBER);
    }

    private void configurePreviousController() {
        when(mockNavigatorService.getPreviousControllerPath(any(), any()))
                .thenReturn(MOCK_CONTROLLER_PATH);
    }

    private void configureValidCompanyProfile(String companyNumber) throws ServiceException {
        when(mockCompanyService.getCompanyProfile(companyNumber))
                .thenReturn(LFPTestUtility.validCompanyProfile(companyNumber));
    }

    private void configureErrorRetrievingCompany(String companyNumber) throws ServiceException {

        doThrow(ServiceException.class)
                .when(mockCompanyService).getCompanyProfile(companyNumber);
    }
}
