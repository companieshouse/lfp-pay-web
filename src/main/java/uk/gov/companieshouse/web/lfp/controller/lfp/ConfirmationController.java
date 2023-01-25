package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.api.model.latefilingpenalty.PayableLateFilingPenalty;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.latefilingpenalty.PayableLateFilingPenaltyService;
import uk.gov.companieshouse.web.lfp.session.SessionService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/late-filing-penalty/company/{companyNumber}/penalty/{penaltyId}/confirmation")
public class ConfirmationController extends BaseController {

    private static final String LFP_CONFIRMATION_PAGE = "lfp/confirmationPage";

    private static final String PAYMENT_STATE = "payment_state";

    @Override protected String getTemplateName() {
        return LFP_CONFIRMATION_PAGE;
    }

    @Autowired
    private PayableLateFilingPenaltyService payableLateFilingPenaltyService;

    @Autowired
    private SessionService sessionService;

    @GetMapping
    public String getConfirmation(@PathVariable String companyNumber,
                                  @PathVariable String penaltyId,
                                  @RequestParam("ref") Optional<String> reference,
                                  @RequestParam("state") String paymentState,
                                  @RequestParam("status") String paymentStatus,
                                  HttpServletRequest request,
                                  Model model) {

        Map<String, Object> sessionData = sessionService.getSessionDataFromContext();

        // Check that the session state is present
        if (!sessionData.containsKey(PAYMENT_STATE)) {
            LOGGER.errorRequest(request, "Payment state value is not present in session, Expected: " + paymentState);
            return ERROR_VIEW;
        }

        String sessionPaymentState = (String) sessionData.get(PAYMENT_STATE);
        sessionData.remove(PAYMENT_STATE);

        // Check that the session state has not been tampered with
        if (!paymentState.equals(sessionPaymentState)) {
            LOGGER.errorRequest(request, "Payment state value in session is not as expected, possible tampering of session "
                    + "Expected: " + sessionPaymentState + ", Received: " + paymentState);
            return ERROR_VIEW;
        }


        // If the payment is anything but paid return user to beginning of journey
        if (!paymentStatus.equals("paid")) {
            LOGGER.info("Payment status is " + paymentStatus + " and not of status 'paid', returning to beginning of journey");
            PayableLateFilingPenalty payableLateFilingPenalty;
            try {
                payableLateFilingPenalty = payableLateFilingPenaltyService
                        .getPayableLateFilingPenalty(companyNumber, penaltyId);
            } catch (ServiceException ex) {
                LOGGER.errorRequest(request, ex.getMessage(), ex);
                return ERROR_VIEW;
            }

            Map<String, String> links = payableLateFilingPenalty.getLinks();
            return UrlBasedViewResolver.REDIRECT_URL_PREFIX + links.get("resume_journey_uri");

        }

        model.addAttribute("companyNumber", companyNumber);
        model.addAttribute("penaltyNumber", penaltyId);

        return getTemplateName();

    }
}
