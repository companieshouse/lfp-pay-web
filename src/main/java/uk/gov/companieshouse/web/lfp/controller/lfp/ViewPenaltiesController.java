package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.api.model.latefilingpenalty.PayableLateFilingPenaltySession;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.company.CompanyService;
import uk.gov.companieshouse.web.lfp.service.latefilingpenalty.LateFilingPenaltyService;
import uk.gov.companieshouse.web.lfp.service.latefilingpenalty.PayableLateFilingPenaltyService;
import uk.gov.companieshouse.web.lfp.service.payment.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Controller
@PreviousController(EnterLFPDetailsController.class)
@RequestMapping("/late-filing-penalty/company/{companyNumber}/penalty/{penaltyNumber}/view-penalties")
public class ViewPenaltiesController extends BaseController {

    private static final String LFP_VIEW_PENALTIES = "lfp/viewPenalties";

    private static final String PENALTY_TYPE = "penalty";

    @Override protected String getTemplateName() {
        return LFP_VIEW_PENALTIES;
    }

    @Autowired
    private CompanyService companyService;

    @Autowired
    private LateFilingPenaltyService lateFilingPenaltyService;

    @Autowired
    private PayableLateFilingPenaltyService payableLateFilingPenaltyService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public String getViewPenalties(@PathVariable String companyNumber,
                                   @PathVariable String penaltyNumber,
                                   Model model,
                                   HttpServletRequest request) {

        addBackPageAttributeToModel(model);

        List<LateFilingPenalty> lateFilingPenalties;
        LateFilingPenalty lateFilingPenalty;
        CompanyProfileApi companyProfileApi;

        try {
            companyProfileApi = companyService.getCompanyProfile(companyNumber);
            lateFilingPenalties = lateFilingPenaltyService.getLateFilingPenalties(companyNumber, penaltyNumber);
            lateFilingPenalty = lateFilingPenalties.get(0);
        } catch (ServiceException ex) {
            LOGGER.errorRequest(request, ex.getMessage(), ex);
            return ERROR_VIEW;
        }

        // If this screen is accessed directly for an invalid penalty return an error view.
        if (lateFilingPenalty == null
                || lateFilingPenalties.size() != 1
                || !lateFilingPenalty.getId().equals(penaltyNumber)
                || Boolean.TRUE.equals(lateFilingPenalty.getDca())
                || Boolean.TRUE.equals(lateFilingPenalty.getPaid())
                || lateFilingPenalty.getOutstanding() <= 0
                || !lateFilingPenalty.getOriginalAmount().equals(lateFilingPenalty.getOutstanding())
                || !lateFilingPenalty.getType().equals(PENALTY_TYPE)) {
            LOGGER.info("Penalty" + lateFilingPenalty + " is invalid, cannot access 'view penalty' screen");
            return ERROR_VIEW;
        }

        model.addAttribute("outstanding", lateFilingPenalty.getOutstanding());
        model.addAttribute("madeUpDate",
                LocalDate.parse(lateFilingPenalty.getMadeUpDate(),
                        DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.UK))
                        .format(DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.UK)));
        model.addAttribute("dueDate",
                LocalDate.parse(lateFilingPenalty.getDueDate(),
                        DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.UK))
                        .format(DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.UK)));

        model.addAttribute("companyName", companyProfileApi.getCompanyName());

        return getTemplateName();
    }

    @PostMapping
    public String postViewPenalties(@PathVariable String companyNumber,
                                    @PathVariable String penaltyNumber,
                                    HttpServletRequest request) {

        PayableLateFilingPenaltySession payableLateFilingPenaltySession;

        try {
            // Call penalty details for create request
            LateFilingPenalty lateFilingPenalty = lateFilingPenaltyService.getLateFilingPenalties(companyNumber, penaltyNumber).get(0);

            // Create payable session
            payableLateFilingPenaltySession = payableLateFilingPenaltyService.createLateFilingPenaltySession(
                    companyNumber,
                    penaltyNumber,
                    lateFilingPenalty.getOutstanding());

        } catch (ServiceException e) {

            LOGGER.errorRequest(request, e.getMessage(), e);
            return ERROR_VIEW;
        }

        try {
            // Return the payment session URL and add query parameter to indicate Review Payments screen isn't wanted
            return UrlBasedViewResolver.REDIRECT_URL_PREFIX +
                    paymentService.createPaymentSession(payableLateFilingPenaltySession, companyNumber) + "?summary=false";
        } catch (ServiceException e) {

            LOGGER.errorRequest(request, e.getMessage(), e);
            return ERROR_VIEW;
        }
    }

}
