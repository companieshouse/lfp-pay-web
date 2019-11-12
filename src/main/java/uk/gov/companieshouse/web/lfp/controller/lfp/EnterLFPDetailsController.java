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
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.web.lfp.annotation.NextController;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.models.EnterLFPDetails;
import uk.gov.companieshouse.web.lfp.service.company.CompanyService;
import uk.gov.companieshouse.web.lfp.service.latefilingpenalty.LateFilingPenaltyService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@PreviousController(LFPStartController.class)
@NextController(ViewPenaltiesController.class)
@RequestMapping("/late-filing-penalty/enter-details")
public class EnterLFPDetailsController extends BaseController {

    private static final String LFP_ENTER_DETAILS = "lfp/details";

    @Autowired
    private LateFilingPenaltyService lateFilingPenaltyService;

    @Autowired
    private CompanyService companyService;

    private static final String LFP_NO_PENALTY_FOUND = "/no-penalties-found";

    private static final String LFP_PENALTY_PAID = "/penalty-paid";

    private static final String LFP_DCA = "/legal-fees-required";

    private static final String LFP_ONLINE_PAYMENT_UNAVAILABLE = "/online-payment-unavailable";

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

        String companyNumber = companyService.appendToCompanyNumber(enterLFPDetails.getCompanyNumber());
        String penaltyNumber = enterLFPDetails.getPenaltyNumber();

        try {
            List<LateFilingPenalty> payableLateFilingPenalties = lateFilingPenaltyService
                    .getLateFilingPenalties(companyNumber, penaltyNumber);

            // If there are no payable late filing penalties either the company does not exist or has no penalties.
            if (payableLateFilingPenalties.isEmpty()) {
                return UrlBasedViewResolver.REDIRECT_URL_PREFIX + urlGenerator(companyNumber, penaltyNumber) + LFP_NO_PENALTY_FOUND;
            }

            // If there is more than one payable penalty.
            if (payableLateFilingPenalties.size() > 1) {
                return UrlBasedViewResolver.REDIRECT_URL_PREFIX + urlGenerator(companyNumber, penaltyNumber) + LFP_ONLINE_PAYMENT_UNAVAILABLE;
            }

            LateFilingPenalty lateFilingPenalty;
            // If the only penalty in the List does not have the provided penalty number return Penalty Not Found.
            if (payableLateFilingPenalties.get(0).getId().equals(penaltyNumber)) {
                lateFilingPenalty = payableLateFilingPenalties.get(0);
            } else {
                return UrlBasedViewResolver.REDIRECT_URL_PREFIX + urlGenerator(companyNumber, penaltyNumber) + LFP_NO_PENALTY_FOUND;
            }

            // If the payable penalty has DCA payments.
            if (lateFilingPenalty.getDca()) {
                return UrlBasedViewResolver.REDIRECT_URL_PREFIX + urlGenerator(companyNumber, penaltyNumber) + LFP_DCA;
            }

            // If the penalty is already paid.
            if (lateFilingPenalty.getPaid()) {
                return UrlBasedViewResolver.REDIRECT_URL_PREFIX + urlGenerator(companyNumber, penaltyNumber) + LFP_PENALTY_PAID;
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

        } catch (ServiceException ex) {

            LOGGER.errorRequest(request, ex.getMessage(), ex);
            return ERROR_VIEW;
        }
    }

    private String urlGenerator(String companyNumber, String penaltyNumber) {
        return "/late-filing-penalty/company/" + companyNumber + "/penalty/" + penaltyNumber;
    }

}



