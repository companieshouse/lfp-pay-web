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
import uk.gov.companieshouse.web.lfp.service.lfp.LFPDetailsService;
import uk.gov.companieshouse.web.lfp.service.payment.PaymentService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Controller
@PreviousController(EnterLFPDetailsController.class)
@RequestMapping("/company/{companyNumber}/penalty/{penaltyNumber}/lfp/view-penalties")
public class ViewPenaltiesController extends BaseController {

    private static String LFP_VIEW_PENALTIES = "lfp/viewPenalties";

    @Override protected String getTemplateName() {
        return LFP_VIEW_PENALTIES;
    }

    @Autowired
    private LFPDetailsService LFPDetailsService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public String getViewPenalties(@PathVariable String companyNumber,
                                   @PathVariable String penaltyNumber,
                                   Model model,
                                   HttpServletRequest request) {

        addBackPageAttributeToModel(model);

        LateFilingPenalty lateFilingPenalty;
        CompanyProfileApi companyProfileApi;

        try {
            companyProfileApi = LFPDetailsService.getCompanyProfile(companyNumber);
            lateFilingPenalty = LFPDetailsService.getPayableLateFilingPenalties(companyNumber, penaltyNumber).get(0);
        } catch (ServiceException ex) {
            LOGGER.errorRequest(request, ex.getMessage(), ex);
            return ERROR_VIEW;
        }

        if (lateFilingPenalty == null) {
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
                                    Model model,
                                    HttpServletRequest request) {

        PayableLateFilingPenaltySession payableLateFilingPenaltySession;

        try {
            // Call penalty details for create request
            LateFilingPenalty lateFilingPenalty = LFPDetailsService.getPayableLateFilingPenalties(companyNumber, penaltyNumber).get(0);

            // Create payable session
            payableLateFilingPenaltySession = LFPDetailsService.createLateFilingPenaltySession(
                    companyNumber,
                    penaltyNumber,
                    lateFilingPenalty.getOutstanding());

        } catch (ServiceException e) {

            LOGGER.errorRequest(request, e.getMessage(), e);
            return ERROR_VIEW;
        }

        try {
            return UrlBasedViewResolver.REDIRECT_URL_PREFIX +
                    paymentService.createPaymentSession(payableLateFilingPenaltySession);
        } catch (ServiceException e) {

            LOGGER.errorRequest(request, e.getMessage(), e);
            return ERROR_VIEW;
        }
    }

}
