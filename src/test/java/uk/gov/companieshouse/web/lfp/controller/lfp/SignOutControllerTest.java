package uk.gov.companieshouse.web.lfp.controller.lfp;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.method.P;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.web.lfp.session.SessionService;
import uk.gov.companieshouse.web.lfp.validation.AllowlistChecker;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SignOutControllerTest {


    private MockMvc mockMvc;


    @Mock
    private SessionService sessionService;


    @Mock
    private Map<String, Object> sessionData;


    @Mock
    private AllowlistChecker allowlistChecker;

    @Mock
    final Environment env = mock(Environment.class);




    @InjectMocks
    private SignOutController controller;
    private static final String MOCK_CONTROLLER_PATH = UrlBasedViewResolver.REDIRECT_URL_PREFIX + "mockControllerPath";
    private static final String HOME = "/late-filing-penalty/";
    private static final String ERROR_VIEW = "error";
    private static final String SIGN_OUT_PATH = "/late-filing-penalty/sign-out";
    private static final String SIGN_OUT_VIEW = "lfp/signOut";
    private static final String SIGN_IN_KEY = "signin_info";
    private static final String RADIO = "radio";
    private static final String PREVIOUS_PATH = "/late-filing-penalty/enter-details";
    private static final String SIGN_OUT = System.getProperty("ACCOUNT_LOCAL_URL");


    @BeforeEach
    private void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Get Sign out page- success path with no referer")
    void getRequestSuccess() throws Exception {
        when(sessionService.getSessionDataFromContext()).thenReturn(sessionData);
        when(sessionData.containsKey(SIGN_IN_KEY)).thenReturn(true);


        this.mockMvc.perform(get(SIGN_OUT_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(SIGN_OUT_VIEW));

    }


    @Test
    @DisplayName("Check if Referer is populated to return a previous page")
    void getPreviousReferer() throws Exception {
        when(sessionService.getSessionDataFromContext()).thenReturn(sessionData);
        when(sessionData.containsKey(SIGN_IN_KEY)).thenReturn(true);
        when(allowlistChecker.checkURL(PREVIOUS_PATH)).thenReturn(PREVIOUS_PATH);


        this.mockMvc.perform(get(SIGN_OUT_PATH).header("Referer", PREVIOUS_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(SIGN_OUT_VIEW));


    }


    @Test
    @DisplayName("Check Sign out set referer then keep alternative path set")
    void getCheckSignOutIsReferer() throws Exception {
        when(sessionService.getSessionDataFromContext()).thenReturn(sessionData);
        when(sessionData.containsKey(SIGN_IN_KEY)).thenReturn(true);
        when(allowlistChecker.checkSignOutIsReferer(SIGN_OUT)).thenReturn(true);

        this.mockMvc.perform(get(SIGN_OUT_PATH).header("Referer", PREVIOUS_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(SIGN_OUT_VIEW));


    }


    @Test
    @DisplayName("Test sign out page- cannot get sign out page when no session data is present")
    void noSuccessGet() throws Exception {

        this.mockMvc.perform(get(SIGN_OUT_PATH))
                .andExpect(view().name(ERROR_VIEW))
                .andExpect(status().isOk());


    }

    @Test
    @DisplayName("Post Sign Out - yes radio button selected")
    void postRequestRadioYes() throws Exception {

        this.mockMvc.perform(post(SIGN_OUT_PATH)
                .param(RADIO, "yes"))
                .andExpect(redirectedUrl(SIGN_OUT+"/signout"));

    }


    @Test
    @DisplayName("Post Sign Out - no on radio button with previous referer")
    void postRequestRadioNoWithValidReferer() throws Exception {
        HashMap<String, Object> sessionattr = new HashMap<String, Object>();
        sessionattr.put("url_prior_signout", PREVIOUS_PATH);

        this.mockMvc.perform(post(SIGN_OUT_PATH).header("Referer", PREVIOUS_PATH)
                .sessionAttrs(sessionattr).param("url_prior_signout", PREVIOUS_PATH)
                .param(RADIO, "no"))
                .andExpect(redirectedUrl(PREVIOUS_PATH));

    }


      @Test
    @DisplayName("Post Sign Out - error message - a radio button has not been selected")
    void postRequestRadioNull() throws Exception {

        this.mockMvc.perform(post(SIGN_OUT_PATH))
                .andExpect(redirectedUrl(SIGN_OUT_PATH))
                .andExpect(MockMvcResultMatchers.flash().attribute("errorMessage",true));
    }
}
