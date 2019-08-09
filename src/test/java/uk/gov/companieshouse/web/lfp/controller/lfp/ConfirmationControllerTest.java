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
import uk.gov.companieshouse.web.lfp.session.SessionService;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConfirmationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SessionService sessionService;

    @Mock
    private Map<String, Object> sessionData;

    @InjectMocks
    private ConfirmationController controller;

    private static final String PENALTY_ID = "EXAMPLE12345";

    private static final String VIEW_CONFIRMATION_PATH = "/lfp/penalty/" + PENALTY_ID + "/confirmation";

    //TODO - Update with correct resume-url once implemented.
    private static final String RESUME_URL_PATH = UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/lfp/enter-details";

    private static final String CONFIRMATION_VIEW = "lfp/confirmationPage";
    private static final String ERROR_VIEW = "error";
    private static final String PENALTY_ID_MODEL_ATTR = "penaltyNumber";

    private static final String REF = "ref";
    private static final String STATE = "state";
    private static final String MISMATCHED_STATE = "mismatchedState";
    private static final String PAYMENT_STATUS_PAID = "paid";
    private static final String PAYMENT_STATUS_CANCELLED = "cancelled";

    private static final String PAYMENT_STATE = "payment_state";

    @BeforeEach
    private void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Get View Confirmation Screen - success path")
    void getRequestSuccess() throws Exception {

        when(sessionService.getSessionDataFromContext()).thenReturn(sessionData);
        when(sessionData.containsKey(PAYMENT_STATE)).thenReturn(true);

        when(sessionData.get(PAYMENT_STATE)).thenReturn(STATE);

        this.mockMvc.perform(get(VIEW_CONFIRMATION_PATH)
        .param("ref", REF)
        .param("state", STATE)
        .param("status", PAYMENT_STATUS_PAID))
                .andExpect(view().name(CONFIRMATION_VIEW))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(PENALTY_ID_MODEL_ATTR));


        verify(sessionData).remove(PAYMENT_STATE);
    }

    @Test
    @DisplayName("Get Confirmation Screen - missing payment state from session")
    void getRequestMissingPaymentState() throws Exception {

        when(sessionService.getSessionDataFromContext()).thenReturn(sessionData);
        when(sessionData.containsKey(PAYMENT_STATE)).thenReturn(false);

        this.mockMvc.perform(get(VIEW_CONFIRMATION_PATH)
                .param("ref", REF)
                .param("state", STATE)
                .param("status", PAYMENT_STATUS_PAID))
                .andExpect(view().name(ERROR_VIEW))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get Confirmation Screen - mismatched payment states")
    void getRequestMismatchedPaymentStates() throws Exception {

        when(sessionService.getSessionDataFromContext()).thenReturn(sessionData);
        when(sessionData.containsKey(PAYMENT_STATE)).thenReturn(true);

        when(sessionData.get(PAYMENT_STATE)).thenReturn(MISMATCHED_STATE);

        this.mockMvc.perform(get(VIEW_CONFIRMATION_PATH)
                .param("ref", REF)
                .param("state", STATE)
                .param("status", PAYMENT_STATUS_PAID))
                .andExpect(view().name(ERROR_VIEW))
                .andExpect(status().isOk());


        verify(sessionData).remove(PAYMENT_STATE);
    }

    @Test
    @DisplayName("Get View Confirmation Screen - payment status cancelled")
    void getRequestStatusIsCancelled() throws Exception {

        when(sessionService.getSessionDataFromContext()).thenReturn(sessionData);
        when(sessionData.containsKey(PAYMENT_STATE)).thenReturn(true);

        when(sessionData.get(PAYMENT_STATE)).thenReturn(STATE);


        this.mockMvc.perform(get(VIEW_CONFIRMATION_PATH)
                .param("ref", REF)
                .param("state", STATE)
                .param("status", PAYMENT_STATUS_CANCELLED))
                //TODO - Update with correct RESUME_URL_PATH once resume-url is implemented.
                .andExpect(view().name(RESUME_URL_PATH))
                .andExpect(status().is3xxRedirection());


        verify(sessionData).remove(PAYMENT_STATE);
    }

}
