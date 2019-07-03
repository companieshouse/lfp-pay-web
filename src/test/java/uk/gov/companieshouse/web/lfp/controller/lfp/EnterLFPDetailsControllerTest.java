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
import uk.gov.companieshouse.web.lfp.service.lfp.EnterLFPDetailsService;
import uk.gov.companieshouse.web.lfp.service.navigation.NavigatorService;

import static org.mockito.ArgumentMatchers.any;
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
public class EnterLFPDetailsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EnterLFPDetailsService mockEnterLFPDetailsService;

    @Mock
    private NavigatorService mockNavigatorService;

    @InjectMocks
    private EnterLFPDetailsController controller;

    private static final String ENTER_LFP_DETAILS_PATH = "/lfp/enter-details";

    private static final String TEMPLATE_NAME_MODEL_ATTR = "templateName";

    private static final String ENTER_LFP_DETAILS_VIEW = "lfp/details";

    private static final String ENTER_LFP_DETAILS_MODEL_ATTR = "enterLFPDetails";
    
    private static final String PENALTY_NUMBER_ATTRIBUTE = "penaltyNumber";

    private static final String COMPANY_NUMBER_ATTRIBUTE = "companyNumber";
    
    private static final String VALID_PENALTY_OR_COMPANY_NUMBER = "12345678";

    private static final String SIX_DIGIT_PENALTY_OR_COMPANY_NUMBER = "123456";

    private static final String BACK_BUTTON_MODEL_ATTR = "backButton";

    private static final String MOCK_CONTROLLER_PATH = UrlBasedViewResolver.REDIRECT_URL_PREFIX + "mockControllerPath";

    @BeforeEach
    private void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Get LFP Details view success path")
    void getRequestSuccess() throws Exception {

        when(mockNavigatorService.getPreviousControllerPath(any(), any())).thenReturn(MOCK_CONTROLLER_PATH);

        this.mockMvc.perform(get(ENTER_LFP_DETAILS_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(ENTER_LFP_DETAILS_VIEW))
                .andExpect(model().attributeExists(ENTER_LFP_DETAILS_MODEL_ATTR))
                .andExpect(model().attributeExists(BACK_BUTTON_MODEL_ATTR));
    }

    @Test
    @DisplayName("Post LFP Details failure path - Blank company number, correct penalty number")
    void postRequestCompanyNumberBlank() throws Exception {

        this.mockMvc.perform(post(ENTER_LFP_DETAILS_PATH)
                .param(PENALTY_NUMBER_ATTRIBUTE, VALID_PENALTY_OR_COMPANY_NUMBER))
                .andExpect(status().isOk())
                .andExpect(view().name(ENTER_LFP_DETAILS_VIEW))
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeHasFieldErrors(ENTER_LFP_DETAILS_MODEL_ATTR, COMPANY_NUMBER_ATTRIBUTE))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 1));
    }

    @Test
    @DisplayName("Post LFP Details failure path - correct company number, blank penalty number")
    void postRequestPenaltyNumberBlank() throws Exception {

        this.mockMvc.perform(post(ENTER_LFP_DETAILS_PATH)
                .param(COMPANY_NUMBER_ATTRIBUTE, VALID_PENALTY_OR_COMPANY_NUMBER))
                .andExpect(status().isOk())
                .andExpect(view().name(ENTER_LFP_DETAILS_VIEW))
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeHasFieldErrors(ENTER_LFP_DETAILS_MODEL_ATTR, PENALTY_NUMBER_ATTRIBUTE))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 1));
    }

    @Test
    @DisplayName("Post LFP Details failure path - blank company number, blank penalty number")
    void postRequestPenaltyNumberBlankAndCompanyNumberBlank() throws Exception {

        this.mockMvc.perform(post(ENTER_LFP_DETAILS_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(ENTER_LFP_DETAILS_VIEW))
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeHasFieldErrors(ENTER_LFP_DETAILS_MODEL_ATTR, PENALTY_NUMBER_ATTRIBUTE))
                .andExpect(model().attributeHasFieldErrors(ENTER_LFP_DETAILS_MODEL_ATTR, COMPANY_NUMBER_ATTRIBUTE))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 2));
    }

    @Test
    @DisplayName("Post LFP Details failure path - blank company number, incorrect penalty number length")
    void postRequestPenaltyNumberIncorrectLengthAndCompanyNumberBlank() throws Exception {

        this.mockMvc.perform(post(ENTER_LFP_DETAILS_PATH)
                .param(PENALTY_NUMBER_ATTRIBUTE, SIX_DIGIT_PENALTY_OR_COMPANY_NUMBER))
                .andExpect(status().isOk())
                .andExpect(view().name(ENTER_LFP_DETAILS_VIEW))
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeHasFieldErrors(ENTER_LFP_DETAILS_MODEL_ATTR, PENALTY_NUMBER_ATTRIBUTE))
                .andExpect(model().attributeHasFieldErrors(ENTER_LFP_DETAILS_MODEL_ATTR, COMPANY_NUMBER_ATTRIBUTE))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 2));
    }

    @Test
    @DisplayName("Post LFP Details success path")
    void postRequestSuccess() throws Exception {

        // Will need to be invoked when next page is added.
        // when(mockNavigatorService.getNextControllerRedirect(any(), ArgumentMatchers.<String>any())).thenReturn(MOCK_CONTROLLER_PATH);

        this.mockMvc.perform(post(ENTER_LFP_DETAILS_PATH)
                .param(PENALTY_NUMBER_ATTRIBUTE, VALID_PENALTY_OR_COMPANY_NUMBER)
                .param(COMPANY_NUMBER_ATTRIBUTE, VALID_PENALTY_OR_COMPANY_NUMBER))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 0))

                // Temporary NextController test until next page is added.
                // .andExpect(view().name(MOCK_CONTROLLER_PATH));
                .andExpect(view().name("lfp/enter-details"));

        verify(mockEnterLFPDetailsService, times(1)).appendToCompanyNumber(VALID_PENALTY_OR_COMPANY_NUMBER);
    }

    @Test
    @DisplayName("Post LFP Details success path - 6 digit company number, correct penalty number")
    void postRequestSuccess6DigitCompanyNumber() throws Exception {

        // Will need to be invoked when next page is added.
        // when(mockNavigatorService.getNextControllerRedirect(any(), ArgumentMatchers.<String>any())).thenReturn(MOCK_CONTROLLER_PATH);

        this.mockMvc.perform(post(ENTER_LFP_DETAILS_PATH)
                .param(PENALTY_NUMBER_ATTRIBUTE, VALID_PENALTY_OR_COMPANY_NUMBER)
                .param(COMPANY_NUMBER_ATTRIBUTE, SIX_DIGIT_PENALTY_OR_COMPANY_NUMBER))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 0))

                // Temporary NextController test until next page is added.
                // .andExpect(view().name(MOCK_CONTROLLER_PATH));
                .andExpect(view().name("lfp/enter-details"));

        verify(mockEnterLFPDetailsService, times(1)).appendToCompanyNumber(SIX_DIGIT_PENALTY_OR_COMPANY_NUMBER);
    }
}
