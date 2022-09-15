package uk.gov.companieshouse.web.lfp.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AllowListCheckerTest {

    private static final String HOME = "/late-filing-penalty";
    private static final String VALID_PENALTY_NUMBER = "12345678";
    private static final String VALID_COMPANY_NUMBER = "00987654";
    private static final String INVALID_URL  = "/late-filing-pen/enter-details/";
    private static final String VALID_URL  = "/late-filing-penalty/company/" + VALID_COMPANY_NUMBER + "/penalty/" + VALID_PENALTY_NUMBER + "/legal-fees-required";
    private static final String SIGN_OUT = "/late-filing-penalty/sign-out";

    private AllowlistChecker allowListChecker = new AllowlistChecker();

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("test if regex returns invalid value")
    void getInvalidUrl() throws Exception {
        String result = allowListChecker.checkURL(INVALID_URL);
        assertEquals(HOME, result);
    }

    @Test
    @DisplayName("test if regex returns correct value")
    void getValidUrl() throws Exception {
        String result = allowListChecker.checkURL(VALID_URL);
        assertEquals(VALID_URL, result);
    }

    @Test
    @DisplayName("test sign out is detected")
    void checkForSignOut() throws Exception {
        boolean isSignOut = allowListChecker.checkSignOutIsReferer(SIGN_OUT);
        assertTrue(isSignOut);
    }

    @Test
    @DisplayName("test sign out is not detected")
    void checkForSignOutWithNonSignOutUrl() throws Exception {
        boolean isSignOut = allowListChecker.checkSignOutIsReferer(HOME);
        assertFalse(isSignOut);
    }

    @Test
    @DisplayName("test sign out is not returned")
    void checkForSignOutURLIsNotReturned() throws Exception {
        String previousURL = allowListChecker.checkURL(VALID_URL);
        String result = allowListChecker.checkURL(SIGN_OUT);
        assertNotEquals(SIGN_OUT, result);
        assertEquals(VALID_URL, previousURL);
    }
}
