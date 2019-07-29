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
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.lfp.LFPDetailsService;
import uk.gov.companieshouse.web.lfp.service.navigation.NavigatorService;
import uk.gov.companieshouse.web.lfp.util.LFPTestUtility;

import java.util.ArrayList;
import java.util.List;

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
public class EnterLFPDetailsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LFPDetailsService mockLFPDetailsService;

    @Mock
    private NavigatorService mockNavigatorService;

    @InjectMocks
    private EnterLFPDetailsController controller;

    private static final String VALID_PENALTY_NUMBER = "12345678";

    private static final String VALID_COMPANY_NUMBER = "00987654";

    private static final String SIX_DIGIT_COMPANY_NUMBER = "987654";

    private static final String ENTER_LFP_DETAILS_PATH = "/lfp/enter-details";

    private static final String NO_PENALTY_FOUND_PATH = "redirect:/company/" + VALID_COMPANY_NUMBER + "/penalty/" + VALID_PENALTY_NUMBER + "/lfp/no-penalties-found";

    private static final String ONLINE_PAYMENT_UNAVAILABLE_PATH = "redirect:/company/" + VALID_COMPANY_NUMBER + "/penalty/" + VALID_PENALTY_NUMBER + "/lfp/online-payment-unavailable";

    private static final String DCA_PAYMENTS_PATH = "redirect:/company/" + VALID_COMPANY_NUMBER + "/penalty/" + VALID_PENALTY_NUMBER + "/lfp/legal-fees-required";

    private static final String ALREADY_PAID_PATH = "redirect:/company/" + VALID_COMPANY_NUMBER + "/penalty/" + VALID_PENALTY_NUMBER + "/lfp/penalty-paid";

    private static final String ERROR_PAGE = "error";

    private static final String TEMPLATE_NAME_MODEL_ATTR = "templateName";

    private static final String ENTER_LFP_DETAILS_VIEW = "lfp/details";

    private static final String ENTER_LFP_DETAILS_MODEL_ATTR = "enterLFPDetails";
    
    private static final String PENALTY_NUMBER_ATTRIBUTE = "penaltyNumber";

    private static final String COMPANY_NUMBER_ATTRIBUTE = "companyNumber";

    private static final String BACK_BUTTON_MODEL_ATTR = "backButton";

    private static final String MOCK_CONTROLLER_PATH = UrlBasedViewResolver.REDIRECT_URL_PREFIX + "mockControllerPath";

    @BeforeEach
    private void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Get LFP Details view success path")
    void getRequestSuccess() throws Exception {

        configurePreviousController();

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
                .param(PENALTY_NUMBER_ATTRIBUTE, VALID_PENALTY_NUMBER))
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
                .param(COMPANY_NUMBER_ATTRIBUTE, VALID_COMPANY_NUMBER))
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
                .param(PENALTY_NUMBER_ATTRIBUTE, SIX_DIGIT_COMPANY_NUMBER))
                .andExpect(status().isOk())
                .andExpect(view().name(ENTER_LFP_DETAILS_VIEW))
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeHasFieldErrors(ENTER_LFP_DETAILS_MODEL_ATTR, PENALTY_NUMBER_ATTRIBUTE))
                .andExpect(model().attributeHasFieldErrors(ENTER_LFP_DETAILS_MODEL_ATTR, COMPANY_NUMBER_ATTRIBUTE))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 2));
    }

    @Test
    @DisplayName("Post LFP Details failure path - no payable late filing penalties found")
    void postRequestNoPayableLateFilingPenaltyFound() throws Exception {

        configureValidAppendCompanyNumber(VALID_COMPANY_NUMBER);

        this.mockMvc.perform(post(ENTER_LFP_DETAILS_PATH)
                .param(PENALTY_NUMBER_ATTRIBUTE, VALID_PENALTY_NUMBER)
                .param(COMPANY_NUMBER_ATTRIBUTE, VALID_COMPANY_NUMBER))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 0))
                .andExpect(view().name(NO_PENALTY_FOUND_PATH));

        verify(mockLFPDetailsService, times(1)).appendToCompanyNumber(VALID_COMPANY_NUMBER);
    }

    @Test
    @DisplayName("Post LFP Details failure path - multiple payable penalties")
    void postRequestMultiplePayablePenalties() throws Exception {

        configureValidAppendCompanyNumber(VALID_COMPANY_NUMBER);
        configureMultiplePenalties(VALID_COMPANY_NUMBER, VALID_PENALTY_NUMBER);

        this.mockMvc.perform(post(ENTER_LFP_DETAILS_PATH)
                .param(PENALTY_NUMBER_ATTRIBUTE, VALID_PENALTY_NUMBER)
                .param(COMPANY_NUMBER_ATTRIBUTE, VALID_COMPANY_NUMBER))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 0))
                .andExpect(view().name(ONLINE_PAYMENT_UNAVAILABLE_PATH));

        verify(mockLFPDetailsService, times(1)).appendToCompanyNumber(VALID_COMPANY_NUMBER);
    }

    @Test
    @DisplayName("Post LFP Details failure path - payable penalty does not match provided penalty number")
    void postRequestPenaltyNumbersDoNotMatch() throws Exception {

        configureValidAppendCompanyNumber(VALID_COMPANY_NUMBER);
        configurePenaltyWrongID(VALID_COMPANY_NUMBER, VALID_PENALTY_NUMBER);

        this.mockMvc.perform(post(ENTER_LFP_DETAILS_PATH)
                .param(PENALTY_NUMBER_ATTRIBUTE, VALID_PENALTY_NUMBER)
                .param(COMPANY_NUMBER_ATTRIBUTE, VALID_COMPANY_NUMBER))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 0))
                .andExpect(view().name(NO_PENALTY_FOUND_PATH));

        verify(mockLFPDetailsService, times(1)).appendToCompanyNumber(VALID_COMPANY_NUMBER);
    }

    @Test
    @DisplayName("Post LFP Details failure path - penalty has legal fees (DCA)")
    void postRequestPenaltyWithDCAPayments() throws Exception {

        configureValidAppendCompanyNumber(VALID_COMPANY_NUMBER);
        configurePenaltyDCA(VALID_COMPANY_NUMBER, VALID_PENALTY_NUMBER);

        this.mockMvc.perform(post(ENTER_LFP_DETAILS_PATH)
                .param(PENALTY_NUMBER_ATTRIBUTE, VALID_PENALTY_NUMBER)
                .param(COMPANY_NUMBER_ATTRIBUTE, VALID_COMPANY_NUMBER))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 0))
                .andExpect(view().name(DCA_PAYMENTS_PATH));

        verify(mockLFPDetailsService, times(1)).appendToCompanyNumber(VALID_COMPANY_NUMBER);
    }

    @Test
    @DisplayName("Post LFP Details failure path - penalty is already paid")
    void postRequestPenaltyHasAlreadyBeenPaid() throws Exception {

        configureValidAppendCompanyNumber(VALID_COMPANY_NUMBER);
        configurePenaltyAlreadyPaid(VALID_COMPANY_NUMBER, VALID_PENALTY_NUMBER);

        this.mockMvc.perform(post(ENTER_LFP_DETAILS_PATH)
                .param(PENALTY_NUMBER_ATTRIBUTE, VALID_PENALTY_NUMBER)
                .param(COMPANY_NUMBER_ATTRIBUTE, VALID_COMPANY_NUMBER))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 0))
                .andExpect(view().name(ALREADY_PAID_PATH));

        verify(mockLFPDetailsService, times(1)).appendToCompanyNumber(VALID_COMPANY_NUMBER);
    }

    @Test
    @DisplayName("Post LFP Details failure path - penalty has negative outstanding amount")
    void postRequestPenaltyHasNegativeOutstandingAmount() throws Exception {

        configureValidAppendCompanyNumber(VALID_COMPANY_NUMBER);
        configurePenaltyNegativeOustanding(VALID_COMPANY_NUMBER, VALID_PENALTY_NUMBER);

        this.mockMvc.perform(post(ENTER_LFP_DETAILS_PATH)
                .param(PENALTY_NUMBER_ATTRIBUTE, VALID_PENALTY_NUMBER)
                .param(COMPANY_NUMBER_ATTRIBUTE, VALID_COMPANY_NUMBER))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 0))
                .andExpect(view().name(ONLINE_PAYMENT_UNAVAILABLE_PATH));

        verify(mockLFPDetailsService, times(1)).appendToCompanyNumber(VALID_COMPANY_NUMBER);
    }

    @Test
    @DisplayName("Post LFP Details failure path - penalty has been partially paid")
    void postRequestPenaltyIsPartiallyPaid() throws Exception {

        configureValidAppendCompanyNumber(VALID_COMPANY_NUMBER);
        configurePenaltyPartiallyPaid(VALID_COMPANY_NUMBER, VALID_PENALTY_NUMBER);

        this.mockMvc.perform(post(ENTER_LFP_DETAILS_PATH)
                .param(PENALTY_NUMBER_ATTRIBUTE, VALID_PENALTY_NUMBER)
                .param(COMPANY_NUMBER_ATTRIBUTE, VALID_COMPANY_NUMBER))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 0))
                .andExpect(view().name(ONLINE_PAYMENT_UNAVAILABLE_PATH));

        verify(mockLFPDetailsService, times(1)).appendToCompanyNumber(VALID_COMPANY_NUMBER);
    }

    @Test
    @DisplayName("Post LFP Details failure path - penalty is not of type 'penalty'")
    void postRequestPenaltyIsNotPenaltyType() throws Exception {

        configureValidAppendCompanyNumber(VALID_COMPANY_NUMBER);
        configurePenaltyNotPenaltyType(VALID_COMPANY_NUMBER, VALID_PENALTY_NUMBER);

        this.mockMvc.perform(post(ENTER_LFP_DETAILS_PATH)
                .param(PENALTY_NUMBER_ATTRIBUTE, VALID_PENALTY_NUMBER)
                .param(COMPANY_NUMBER_ATTRIBUTE, VALID_COMPANY_NUMBER))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 0))
                .andExpect(view().name(ONLINE_PAYMENT_UNAVAILABLE_PATH));

        verify(mockLFPDetailsService, times(1)).appendToCompanyNumber(VALID_COMPANY_NUMBER);
    }

    @Test
    @DisplayName("Post LFP Details failure path - error retrieving Late Filing Penalty")
    void postRequestErrorRetrievingPenalty() throws Exception {

        configureValidAppendCompanyNumber(VALID_COMPANY_NUMBER);
        configureErrorRetrievingPenalty(VALID_COMPANY_NUMBER, VALID_PENALTY_NUMBER);

        this.mockMvc.perform(post(ENTER_LFP_DETAILS_PATH)
                .param(PENALTY_NUMBER_ATTRIBUTE, VALID_PENALTY_NUMBER)
                .param(COMPANY_NUMBER_ATTRIBUTE, VALID_COMPANY_NUMBER))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 0))
                .andExpect(view().name(ERROR_PAGE));

        verify(mockLFPDetailsService, times(1)).appendToCompanyNumber(VALID_COMPANY_NUMBER);
    }

    @Test
    @DisplayName("Post LFP Details success path")
    void postRequestPenaltySuccess() throws Exception {
        configureNextController();
        configureValidAppendCompanyNumber(VALID_COMPANY_NUMBER);
        configureValidPenalty(VALID_COMPANY_NUMBER, VALID_PENALTY_NUMBER);

        this.mockMvc.perform(post(ENTER_LFP_DETAILS_PATH)
                .param(PENALTY_NUMBER_ATTRIBUTE, VALID_PENALTY_NUMBER)
                .param(COMPANY_NUMBER_ATTRIBUTE, VALID_COMPANY_NUMBER))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeErrorCount(ENTER_LFP_DETAILS_MODEL_ATTR, 0))
                .andExpect(view().name(MOCK_CONTROLLER_PATH));

        verify(mockLFPDetailsService, times(1)).appendToCompanyNumber(VALID_COMPANY_NUMBER);
    }

    private void configurePreviousController() {
        when(mockNavigatorService.getPreviousControllerPath(any(), any()))
                .thenReturn(MOCK_CONTROLLER_PATH);
    }

    private void configureNextController() {
        when(mockNavigatorService.getNextControllerRedirect(any(), any()))
                .thenReturn(MOCK_CONTROLLER_PATH);
    }

    private void configureValidAppendCompanyNumber(String companyNumber) {
        when(mockLFPDetailsService.appendToCompanyNumber(companyNumber))
                .thenReturn(VALID_COMPANY_NUMBER);
    }

    private void configureValidPenalty(String companyNumber, String penaltyNumber) throws ServiceException {
        List<LateFilingPenalty> validLFPs = new ArrayList<>();
        validLFPs.add(LFPTestUtility.validLateFilingPenalty(penaltyNumber));

        when(mockLFPDetailsService.getPayableLateFilingPenalties(companyNumber, penaltyNumber))
                .thenReturn(validLFPs);
    }

    private void configureMultiplePenalties(String companyNumber, String penaltyNumber) throws ServiceException {
        List<LateFilingPenalty> multipleValidLFPs = new ArrayList<>();
        multipleValidLFPs.add(LFPTestUtility.validLateFilingPenalty("12345678"));
        multipleValidLFPs.add(LFPTestUtility.validLateFilingPenalty("23456789"));

        when(mockLFPDetailsService.getPayableLateFilingPenalties(companyNumber, penaltyNumber))
                .thenReturn(multipleValidLFPs);
    }

    private void configurePenaltyWrongID(String companyNumber, String penaltyNumber)
            throws ServiceException {
        List<LateFilingPenalty> wrongIdLfp = new ArrayList<>();
        wrongIdLfp.add(LFPTestUtility.validLateFilingPenalty(companyNumber));

        when(mockLFPDetailsService.getPayableLateFilingPenalties(companyNumber, penaltyNumber))
                .thenReturn(wrongIdLfp);
    }

    private void configurePenaltyDCA(String companyNumber, String penaltyNumber)
            throws ServiceException {
        List<LateFilingPenalty> dcaLfp = new ArrayList<>();
        dcaLfp.add(LFPTestUtility.dcaLateFilingPenalty(penaltyNumber));

        when(mockLFPDetailsService.getPayableLateFilingPenalties(companyNumber, penaltyNumber))
                .thenReturn(dcaLfp);
    }

    private void configurePenaltyAlreadyPaid(String companyNumber, String penaltyNumber)
            throws ServiceException {
        List<LateFilingPenalty> paidLfp = new ArrayList<>();
        paidLfp.add(LFPTestUtility.paidLateFilingPenalty(penaltyNumber));

        when(mockLFPDetailsService.getPayableLateFilingPenalties(companyNumber, penaltyNumber))
                .thenReturn(paidLfp);
    }

    private void configurePenaltyNegativeOustanding(String companyNumber, String penaltyNumber)
            throws ServiceException {
        List<LateFilingPenalty> negativeLFP = new ArrayList<>();
        negativeLFP.add(LFPTestUtility.negativeOustandingLateFilingPenalty(penaltyNumber));

        when(mockLFPDetailsService.getPayableLateFilingPenalties(companyNumber, penaltyNumber))
                .thenReturn(negativeLFP);
    }

    private void configurePenaltyPartiallyPaid(String companyNumber, String penaltyNumber)
            throws ServiceException {
        List<LateFilingPenalty> partialPaidLFP = new ArrayList<>();
        partialPaidLFP.add(LFPTestUtility.partialPaidLateFilingPenalty(penaltyNumber));

        when(mockLFPDetailsService.getPayableLateFilingPenalties(companyNumber, penaltyNumber))
                .thenReturn(partialPaidLFP);
    }

    private void configurePenaltyNotPenaltyType(String companyNumber, String penaltyNumber)
            throws ServiceException {
        List<LateFilingPenalty> notPenaltyTypeLfp = new ArrayList<>();
        notPenaltyTypeLfp.add(LFPTestUtility.notPenaltyTypeLateFilingPenalty(penaltyNumber));

        when(mockLFPDetailsService.getPayableLateFilingPenalties(companyNumber, penaltyNumber))
                .thenReturn(notPenaltyTypeLfp);
    }

    private void configureErrorRetrievingPenalty(String companyNumber, String penaltyNumber)
            throws ServiceException {

        doThrow(ServiceException.class)
                .when(mockLFPDetailsService).getPayableLateFilingPenalties(companyNumber, penaltyNumber);
    }
}
