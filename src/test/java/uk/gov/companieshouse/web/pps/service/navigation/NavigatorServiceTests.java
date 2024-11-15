package uk.gov.companieshouse.web.pps.service.navigation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.web.pps.controller.ConditionalController;
import uk.gov.companieshouse.web.pps.exception.MissingAnnotationException;
import uk.gov.companieshouse.web.pps.exception.NavigationException;
import uk.gov.companieshouse.web.pps.service.navigation.failure.MockControllerEight;
import uk.gov.companieshouse.web.pps.service.navigation.failure.MockControllerFive;
import uk.gov.companieshouse.web.pps.service.navigation.failure.MockControllerFour;
import uk.gov.companieshouse.web.pps.service.navigation.failure.MockControllerOne;
import uk.gov.companieshouse.web.pps.service.navigation.failure.MockControllerSeven;
import uk.gov.companieshouse.web.pps.service.navigation.failure.MockControllerThree;
import uk.gov.companieshouse.web.pps.service.navigation.failure.MockControllerTwo;
import uk.gov.companieshouse.web.pps.service.navigation.success.MockSuccessJourneyControllerOne;
import uk.gov.companieshouse.web.pps.service.navigation.success.MockSuccessJourneyControllerThree;
import uk.gov.companieshouse.web.pps.service.navigation.success.MockSuccessJourneyControllerTwo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NavigatorServiceTests {

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private NavigatorService navigatorService;

    private static final String COMPANY_NUMBER = "companyNumber";
    private static final String TRANSACTION_ID = "transactionId";
    private static final String COMPANY_lfp_ID = "companylfpId";

    @Test
    void missingNextControllerAnnotation() {
        Throwable exception = assertThrows(MissingAnnotationException.class, () ->
                navigatorService.getNextControllerRedirect(MockControllerThree.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID));

        assertEquals("Missing @NextController annotation on class uk.gov.companieshouse.web.pps.service.navigation.failure.MockControllerThree", exception.getMessage());
    }

    @Test
    void missingPreviousControllerAnnotation() {
        Throwable exception = assertThrows(MissingAnnotationException.class, () ->
                navigatorService.getPreviousControllerPath(MockControllerThree.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID));

        assertEquals("Missing @PreviousController annotation on class uk.gov.companieshouse.web.pps.service.navigation.failure.MockControllerThree", exception.getMessage());
    }

    @Test
    void missingRequestMappingAnnotationOnNextController() {
        Throwable exception = assertThrows(MissingAnnotationException.class, () ->
                navigatorService.getNextControllerRedirect(MockControllerOne.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID));

        assertEquals("Missing @RequestMapping annotation on class uk.gov.companieshouse.web.pps.service.navigation.failure.MockControllerTwo", exception.getMessage());
    }

    @Test
    void missingRequestMappingAnnotationOnPreviousController() {
        Throwable exception = assertThrows(MissingAnnotationException.class, () ->
                navigatorService.getPreviousControllerPath(MockControllerTwo.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID));

        assertEquals("Missing @RequestMapping annotation on class uk.gov.companieshouse.web.pps.service.navigation.failure.MockControllerOne", exception.getMessage());
    }

    @Test
    void missingRequestMappingValueOnNextController() {
        Throwable exception = assertThrows(MissingAnnotationException.class, () ->
                navigatorService.getNextControllerRedirect(MockControllerFive.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID));

        assertEquals("Missing @RequestMapping value on class uk.gov.companieshouse.web.pps.service.navigation.failure.MockControllerSix", exception.getMessage());
    }

    @Test
    void missingRequestMappingValueOnPreviousController() {
        Throwable exception = assertThrows(MissingAnnotationException.class, () ->
                navigatorService.getPreviousControllerPath(MockControllerSeven.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID));

        assertEquals("Missing @RequestMapping value on class uk.gov.companieshouse.web.pps.service.navigation.failure.MockControllerSix", exception.getMessage());
    }

    @Test
    void missingExpectedNumberOfPathVariablesForMandatoryController() {

        Throwable exception = assertThrows(NavigationException.class, () ->
                navigatorService.getNextControllerRedirect(MockControllerFour.class, COMPANY_NUMBER));

        assertEquals("No mapping found that matches the number of path variables provided", exception.getMessage());
    }

    @Test
    void successfulRedirectStartingFromMandatoryControllerWithExpectedNumberOfPathVariables() {
        when(applicationContext.getBean(ConditionalController.class))
                .thenReturn(new MockSuccessJourneyControllerTwo())
                .thenReturn(new MockSuccessJourneyControllerThree());

        String redirect = navigatorService.getNextControllerRedirect(MockSuccessJourneyControllerOne.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID);

        assertEquals(UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/mock-success-journey-controller-three/"
                + COMPANY_NUMBER + "/" + TRANSACTION_ID + "/" + COMPANY_lfp_ID, redirect);
    }

    @Test
    void successfulRedirectStartingFromConditionalControllerWithExpectedNumberOfPathVariables() {
        when(applicationContext.getBean(ConditionalController.class)).thenReturn(new MockSuccessJourneyControllerThree());

        String redirect = navigatorService.getNextControllerRedirect(MockSuccessJourneyControllerTwo.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID);

        assertEquals(UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/mock-success-journey-controller-three/"
                + COMPANY_NUMBER + "/" + TRANSACTION_ID + "/" + COMPANY_lfp_ID, redirect);
    }

    @Test
    void successfulPathReturnedWithSingleConditionalControllerInChain() {
        when(applicationContext.getBean(ConditionalController.class))
                .thenReturn(new MockSuccessJourneyControllerTwo())
                .thenReturn(new MockSuccessJourneyControllerThree());

        String redirect = navigatorService.getPreviousControllerPath(MockSuccessJourneyControllerThree.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID);

        assertEquals("/mock-success-journey-controller-one/"
                + COMPANY_NUMBER + "/" + TRANSACTION_ID + "/" + COMPANY_lfp_ID, redirect);
    }

    @Test
    void navigationExceptionThrownWhenWillRenderThrowsServiceException() {
        when(applicationContext.getBean(ConditionalController.class))
                .thenReturn(new MockSuccessJourneyControllerTwo())
                .thenReturn(new MockSuccessJourneyControllerThree());
        when(applicationContext.getBean(ConditionalController.class))
                .thenReturn(new MockControllerSeven())
                .thenReturn(new MockControllerEight());

        assertThrows(NavigationException.class, () -> navigatorService.getNextControllerRedirect(MockControllerSeven.class, COMPANY_NUMBER, TRANSACTION_ID, COMPANY_lfp_ID));

    }
}
