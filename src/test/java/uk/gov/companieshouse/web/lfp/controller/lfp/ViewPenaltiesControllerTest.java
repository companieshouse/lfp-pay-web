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
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalties;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.web.lfp.service.lfp.EnterLFPDetailsService;
import uk.gov.companieshouse.web.lfp.service.navigation.NavigatorService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ViewPenaltiesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EnterLFPDetailsService mockEnterLFPDetailsService;

    @Mock
    private NavigatorService mockNavigatorService;

    @Mock
    private List<LateFilingPenalty> lateFilingPenalties;

    @Mock
    private LateFilingPenalty lateFilingPenalty;

    @Mock
    private CompanyProfileApi companyProfileApi;

    @InjectMocks
    private ViewPenaltiesController controller;

    private static final String COMPANY_NUMBER = "12345678";
    private static final String PENALTY_NUMBER = "44444444";
    private static final String MADE_UP_DATE = "2016-01-01";
    private static final String DUE_DATE = "2016-12-12";
    private static final String COMPANY_NAME = "TEST COMPANY";

    private static final String VIEW_PENALTIES_PATH = "/company/" + COMPANY_NUMBER + "/penalty/" + PENALTY_NUMBER + "/lfp/view-penalties";

    private static final String ENTER_LFP_DETAILS_VIEW = "lfp/viewPenalties";
    private static final String ERROR_VIEW = "error";

    private static final String OUTSTANDING_MODEL_ATTR = "outstanding";
    private static final String MADE_UP_DATE_MODEL_ATTR = "madeUpDate";
    private static final String DUE_DATE_MODEL_ATTR = "dueDate";
    private static final String COMPANY_NAME_MODEL_ATTR = "companyName";

    private static final String MOCK_CONTROLLER_PATH = UrlBasedViewResolver.REDIRECT_URL_PREFIX + "mockControllerPath";

    @BeforeEach
    private void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Get View LFP - success path")
    void getRequestSuccess() throws Exception {
        lateFilingPenalty.setId(PENALTY_NUMBER);

        when(mockNavigatorService.getPreviousControllerPath(any(), any())).thenReturn(MOCK_CONTROLLER_PATH);
        when(mockEnterLFPDetailsService.getPayableLateFilingPenalties(COMPANY_NUMBER, PENALTY_NUMBER)).thenReturn(lateFilingPenalties);
        when(lateFilingPenalties.get(0)).thenReturn(getValidLateFilingPenalty());
        when(mockEnterLFPDetailsService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        when(companyProfileApi.getCompanyName()).thenReturn(COMPANY_NAME);

        this.mockMvc.perform(get(VIEW_PENALTIES_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(ENTER_LFP_DETAILS_VIEW))
                .andExpect(model().attributeExists(OUTSTANDING_MODEL_ATTR))
                .andExpect(model().attributeExists(MADE_UP_DATE_MODEL_ATTR))
                .andExpect(model().attributeExists(DUE_DATE_MODEL_ATTR))
                .andExpect(model().attributeExists(COMPANY_NAME_MODEL_ATTR));

        verify(mockEnterLFPDetailsService, times(1)).getCompanyProfile(COMPANY_NUMBER);
        verify(mockEnterLFPDetailsService, times(1)).getPayableLateFilingPenalties(COMPANY_NUMBER, PENALTY_NUMBER);

    }

    LateFilingPenalty getValidLateFilingPenalty() {
        LateFilingPenalty lateFilingPenalty = new LateFilingPenalty();
        lateFilingPenalty.setId(PENALTY_NUMBER);
        lateFilingPenalty.setOutstanding(1500);
        lateFilingPenalty.setMadeUpDate(MADE_UP_DATE);
        lateFilingPenalty.setDueDate(DUE_DATE);

        return lateFilingPenalty;
    }

}
