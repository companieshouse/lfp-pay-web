package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalties;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.web.lfp.annotation.NextController;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.models.EnterLFPDetails;
import uk.gov.companieshouse.web.lfp.service.lfp.EnterLFPDetailsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@PreviousController(LFPStartController.class)
@NextController(ViewPenaltiesController.class)
@RequestMapping("/lfp/enter-details")
public class EnterLFPDetailsController extends BaseController {

    private static String LFP_ENTER_DETAILS = "lfp/details";

    @Autowired
    private EnterLFPDetailsService enterLFPDetailsService;

    private static String LFP_NO_PENALTY_FOUND = "/no-penalties-found";

    private static String LFP_PENALTY_PAID = "/penalty-paid";

    private static String LFP_DCA = "/legal-fees-required";

    private static String LFP_ONLINE_PAYMENT_UNAVAILABLE = "/online-payment-unavailable";

    private static final String PENALTY_TYPE = "penalty";

    @Override protected String getTemplateName() {
        return LFP_ENTER_DETAILS;
    }

    @GetMapping
    public String getLFPEnterDetails(Model model) {
        model.addAttribute("enterLFPDetails", new EnterLFPDetails());

        addBackPageAttributeToModel(model);

        return getTemplateName();
    }

    @PostMapping
    public String postLFPEnterDetails(@ModelAttribute("enterLFPDetails") @Valid EnterLFPDetails enterLFPDetails,
                                      BindingResult bindingResult,
                                      HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return getTemplateName();
        }

        String companyNumber = enterLFPDetailsService.appendToCompanyNumber(enterLFPDetails.getCompanyNumber());
        String penaltyNumber = enterLFPDetails.getPenaltyNumber();

        try {
            LateFilingPenalties lateFilingPenalties = enterLFPDetailsService.getLateFilingPenalties(companyNumber, penaltyNumber);

            // If the company has no late filing penalties or doesn't exist, display 'No Penalty Found'
            if (lateFilingPenalties.getTotalResults() == 0) {
                return UrlBasedViewResolver.REDIRECT_URL_PREFIX + urlGenerator(companyNumber, penaltyNumber) + LFP_NO_PENALTY_FOUND;
            }

            // Loop through all Late Filing Penalties for company.
            for (LateFilingPenalty lateFilingPenalty: lateFilingPenalties.getItems()) {
                if (lateFilingPenalty.getId().equals(penaltyNumber)) {
                    // If penalty is paid return 'Penalty Paid'
                    if (lateFilingPenalty.getPaid()) {
                        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + urlGenerator(companyNumber, penaltyNumber) + LFP_PENALTY_PAID;
                    }
                    // If penalty has DCA fees return 'DCA'
                    if (lateFilingPenalty.getDca()) {
                        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + urlGenerator(companyNumber, penaltyNumber) + LFP_DCA;
                    }
                    // If the penalty has a 0 or negative outstanding amount,
                    // Or the outstanding amount is different to the original amount (partially paid),
                    // Or the type is not 'penalty', then return 'Online Payment Unavailable'
                    if (lateFilingPenalty.getOutstanding() <= 0
                            || !lateFilingPenalty.getOriginalAmount().equals(lateFilingPenalty.getOutstanding())
                            || !lateFilingPenalty.getType().equals(PENALTY_TYPE)) {
                        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + urlGenerator(companyNumber, penaltyNumber) + LFP_ONLINE_PAYMENT_UNAVAILABLE;
                    }

                    return navigatorService.getNextControllerRedirect(this.getClass(), companyNumber, penaltyNumber);
                }
            }
        } catch (ServiceException ex) {

            LOGGER.errorRequest(request, ex.getMessage(), ex);
            return ERROR_VIEW;
        }

        // If no company number and penalty match is made, display 'No Penalty Found'
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + urlGenerator(companyNumber, penaltyNumber) + LFP_NO_PENALTY_FOUND;
    }

    private String urlGenerator(String companyNumber, String penaltyNumber) {
        return "/company/" + companyNumber + "/penalty/" + penaltyNumber + "/lfp";
    }

}



