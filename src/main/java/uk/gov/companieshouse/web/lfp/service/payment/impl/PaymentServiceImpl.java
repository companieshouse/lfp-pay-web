package uk.gov.companieshouse.web.lfp.service.payment.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.latefilingpenalty.PayableLateFilingPenaltySession;
import uk.gov.companieshouse.api.model.payment.PaymentApi;
import uk.gov.companieshouse.api.model.payment.PaymentSessionApi;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.web.lfp.LFPWebApplication;
import uk.gov.companieshouse.web.lfp.api.ApiClientService;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.payment.PaymentService;
import uk.gov.companieshouse.web.lfp.session.SessionService;

import java.util.Arrays;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private ApiClientService apiClientService;

    private SessionService sessionService;

    private String chsUrl;

    private String apiUrl;

    private static final String CHS_URL = "CHS_URL";

    private static final String API_URL = "API_URL";

    private static final String JOURNEY_LINK = "journey";

    private static final String PAYMENT_URL = "/payments";

    private static final String PAYMENT_STATE = "payment_state";

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(LFPWebApplication.APPLICATION_NAME_SPACE);

    @Autowired
    public PaymentServiceImpl(ApiClientService apiClientService, SessionService sessionService, EnvironmentReader environmentReader) {

        this.apiClientService = apiClientService;
        this.sessionService = sessionService;
        this.chsUrl = environmentReader.getMandatoryString(CHS_URL);
        this.apiUrl = environmentReader.getMandatoryString(API_URL);
    }

    @Override
    public String createPaymentSession(PayableLateFilingPenaltySession payableLateFilingPenaltySession, String companyNumber)
            throws ServiceException {

        String paymentState = UUID.randomUUID().toString();

        PaymentSessionApi paymentSessionApi = new PaymentSessionApi();
        paymentSessionApi.setRedirectUri(chsUrl + "/late-filing-penalty/company/" + companyNumber + "/penalty/" + payableLateFilingPenaltySession.getId() + "/confirmation");
        paymentSessionApi.setResource(apiUrl + payableLateFilingPenaltySession.getLinks().get("self") + "/payment");
        paymentSessionApi.setReference("late_filing_penalty_" + payableLateFilingPenaltySession.getId());
        paymentSessionApi.setState(paymentState);
        LOGGER.info("SESSION REDIRECT URI: " + paymentSessionApi.getRedirectUri());
        LOGGER.info("SESSION REFERENCE: " + paymentSessionApi.getReference());
        LOGGER.info("SESSION RESOURCE: " + paymentSessionApi.getResource());
        LOGGER.info("SESSION STATE: " + paymentSessionApi.getState());

        try {
            ApiResponse<PaymentApi> apiResponse = apiClientService.getPublicApiClient()
                    .payment().create(PAYMENT_URL, paymentSessionApi).execute();

            setPaymentStateOnSession(paymentState);

            return apiResponse.getData().getLinks().get(JOURNEY_LINK);
        } catch (ApiErrorResponseException e) {
            LOGGER.info("API RESPONSE HEADERS: " + e.getHeaders());
            LOGGER.info("API RESPONSE STACKTRACE: " + Arrays.toString(e.getStackTrace()));
            LOGGER.info("API RESPONSE DETAILS: " + e.getDetails());
            LOGGER.info("API RESPONSE MESSAGE: " + e.getMessage());
            LOGGER.info("API RESPONSE CONTENT: " + e.getContent());

            throw new ServiceException("Error creating payment session, status code: " + e.getStatusCode(), e);
        } catch (URIValidationException e) {

            throw new ServiceException("Invalid URI for payment resource", e);
        }
    }

    private void setPaymentStateOnSession(String paymentState) {

        sessionService.getSessionDataFromContext().put(PAYMENT_STATE, paymentState);
    }

}
