package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalties;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.lfp.EnterLFPDetailsService;

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
    private EnterLFPDetailsService enterLFPDetailsService;

    @GetMapping
    public String getViewPenalties(@PathVariable String companyNumber,
                                   @PathVariable String penaltyNumber,
                                   Model model,
                                   HttpServletRequest request) {

        addBackPageAttributeToModel(model);

        LateFilingPenalties lateFilingPenalties;
        CompanyProfileApi companyProfileApi;

        try {
            companyProfileApi = enterLFPDetailsService.getCompanyProfile(companyNumber);
            lateFilingPenalties = enterLFPDetailsService.getLateFilingPenalties(companyNumber, penaltyNumber);
        } catch (ServiceException ex) {
            LOGGER.errorRequest(request, ex.getMessage(), ex);
            return ERROR_VIEW;
        }

        // Loop through all penalties for company and find provided one.
        for (LateFilingPenalty lateFilingPenalty: lateFilingPenalties.getItems()) {
            if (lateFilingPenalty.getId().equals(penaltyNumber)) {
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
            } else {
                // If penalty doesn't exist for company after check in previous controller an error has happened.
                return ERROR_VIEW;
            }
        }

        return getTemplateName();
    }

}
