package uk.gov.companieshouse.web.lfp.service.navigation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.web.lfp.exception.MissingAnnotationException;
import uk.gov.companieshouse.web.lfp.exception.NavigationException;
import uk.gov.companieshouse.web.lfp.service.navigation.failure.MockControllerEight;
import uk.gov.companieshouse.web.lfp.service.navigation.failure.MockControllerFive;
import uk.gov.companieshouse.web.lfp.service.navigation.failure.MockControllerFour;
import uk.gov.companieshouse.web.lfp.service.navigation.failure.MockControllerOne;
import uk.gov.companieshouse.web.lfp.service.navigation.failure.MockControllerSeven;
import uk.gov.companieshouse.web.lfp.service.navigation.failure.MockControllerSix;
import uk.gov.companieshouse.web.lfp.service.navigation.failure.MockControllerThree;
import uk.gov.companieshouse.web.lfp.service.navigation.failure.MockControllerTwo;
import uk.gov.companieshouse.web.lfp.service.navigation.success.MockSuccessJourneyControllerOne;
import uk.gov.companieshouse.web.lfp.service.navigation.success.MockSuccessJourneyControllerThree;
import uk.gov.companieshouse.web.lfp.service.navigation.success.MockSuccessJourneyControllerTwo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class NavigatorServiceTests {

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private NavigatorService navigatorService;

    private static final String COMPANY_NUMBER = "companyNumber";
    private static final String TRANSACTION_ID = "transactionId";
    private static final String COMPANY_lfp_ID = "companylfpId";

    @Test
    public void missingNextControllerAnnotation() {
        Throwable exception = assertThrows(MissingAnnotationException.class, () ->
                navigatorService.getNextControllerRedirect(MockControllerThree.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID));

        assertEquals("Missing @NextController annotation on class uk.gov.companieshouse.web.lfp.service.navigation.failure.MockControllerThree", exception.getMessage());
    }

    @Test
    public void missingPreviousControllerAnnotation() {
        Throwable exception = assertThrows(MissingAnnotationException.class, () ->
                navigatorService.getPreviousControllerPath(MockControllerThree.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID));

        assertEquals("Missing @PreviousController annotation on class uk.gov.companieshouse.web.lfp.service.navigation.failure.MockControllerThree", exception.getMessage());
    }

    @Test
    public void missingRequestMappingAnnotationOnNextController() {
        Throwable exception = assertThrows(MissingAnnotationException.class, () ->
                navigatorService.getNextControllerRedirect(MockControllerOne.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID));

        assertEquals("Missing @RequestMapping annotation on class uk.gov.companieshouse.web.lfp.service.navigation.failure.MockControllerTwo", exception.getMessage());
    }

    @Test
    public void missingRequestMappingAnnotationOnPreviousController() {
        Throwable exception = assertThrows(MissingAnnotationException.class, () ->
                navigatorService.getPreviousControllerPath(MockControllerTwo.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID));

        assertEquals("Missing @RequestMapping annotation on class uk.gov.companieshouse.web.lfp.service.navigation.failure.MockControllerOne", exception.getMessage());
    }

    @Test
    public void missingRequestMappingValueOnNextController() {
        Throwable exception = assertThrows(MissingAnnotationException.class, () ->
                navigatorService.getNextControllerRedirect(MockControllerFive.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID));

        assertEquals("Missing @RequestMapping value on class uk.gov.companieshouse.web.lfp.service.navigation.failure.MockControllerSix", exception.getMessage());
    }

    @Test
    public void missingRequestMappingValueOnPreviousController() {
        Throwable exception = assertThrows(MissingAnnotationException.class, () ->
                navigatorService.getPreviousControllerPath(MockControllerSeven.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID));

        assertEquals("Missing @RequestMapping value on class uk.gov.companieshouse.web.lfp.service.navigation.failure.MockControllerSix", exception.getMessage());
    }

    @Test
    public void missingExpectedNumberOfPathVariablesForMandatoryController() {

        Throwable exception = assertThrows(NavigationException.class, () ->
                navigatorService.getNextControllerRedirect(MockControllerFour.class, COMPANY_NUMBER));

        assertEquals("No mapping found that matches the number of path variables provided", exception.getMessage());
    }

    @Test
    public void missingExpectedNumberOfPathVariablesForPreviousController() {

        Throwable exception = assertThrows(NavigationException.class, () ->
                navigatorService.getPreviousControllerPath(MockControllerSix.class, COMPANY_NUMBER));

        assertEquals("No mapping found that matches the number of path variables provided", exception.getMessage());
    }

    @Test
    public void successfulRedirectStartingFromMandatoryControllerWithExpectedNumberOfPathVariables() {
        when(applicationContext.getBean(MockSuccessJourneyControllerTwo.class)).thenReturn(new MockSuccessJourneyControllerTwo());
        when(applicationContext.getBean(MockSuccessJourneyControllerThree.class)).thenReturn(new MockSuccessJourneyControllerThree());

        String redirect = navigatorService.getNextControllerRedirect(MockSuccessJourneyControllerOne.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID);

        assertEquals(UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/mock-success-journey-controller-three/"
                + COMPANY_NUMBER + "/" + TRANSACTION_ID + "/" + COMPANY_lfp_ID, redirect);
    }

    @Test
    public void successfulRedirectStartingFromConditionalControllerWithExpectedNumberOfPathVariables() {
        when(applicationContext.getBean(MockSuccessJourneyControllerThree.class)).thenReturn(new MockSuccessJourneyControllerThree());

        String redirect = navigatorService.getNextControllerRedirect(MockSuccessJourneyControllerTwo.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID);

        assertEquals(UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/mock-success-journey-controller-three/"
                + COMPANY_NUMBER + "/" + TRANSACTION_ID + "/" + COMPANY_lfp_ID, redirect);
    }

    @Test
    public void successfulPathReturnedWithSingleConditionalControllerInChain() {
        when(applicationContext.getBean(MockSuccessJourneyControllerTwo.class)).thenReturn(new MockSuccessJourneyControllerTwo());
        when(applicationContext.getBean(MockSuccessJourneyControllerThree.class)).thenReturn(new MockSuccessJourneyControllerThree());

        String redirect = navigatorService.getPreviousControllerPath(MockSuccessJourneyControllerThree.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID);

        assertEquals("/mock-success-journey-controller-one/"
                + COMPANY_NUMBER + "/" + TRANSACTION_ID + "/" + COMPANY_lfp_ID, redirect);
    }

    @Test
    public void navigationExceptionThrownWhenWillRenderThrowsServiceException() {
        when(applicationContext.getBean(MockControllerSeven.class)).thenReturn(new MockControllerSeven());
        when(applicationContext.getBean(MockControllerEight.class)).thenReturn(new MockControllerEight());

        assertThrows(NavigationException.class, () -> navigatorService.getNextControllerRedirect(MockControllerSeven.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID));

    }
}
