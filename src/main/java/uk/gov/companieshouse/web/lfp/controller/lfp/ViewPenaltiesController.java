package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.api.model.latefilingpenalty.LFPItems;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.lfp.ViewPenaltiesService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Controller
@PreviousController(EnterLFPDetailsController.class)
@RequestMapping("/lfp/view-penalties/{companyNumber}")
public class ViewPenaltiesController extends BaseController {


    @Autowired
    private ViewPenaltiesService viewPenaltiesService;

    private static String LFP_VIEW_PENALTIES = "lfp/viewPenalties";

    @Override protected String getTemplateName() {
        return LFP_VIEW_PENALTIES;
    }

    @GetMapping
    public String getLFPEnterDetails(@PathVariable String companyNumber, Model model, HttpServletRequest request) {

        addBackPageAttributeToModel(model);

        try {
            LateFilingPenalty lateFilingPenalty = viewPenaltiesService.getLateFilingPenalty(companyNumber);

            //TODO: - Use filter to determine which error page to return
            if (!lateFilingPenalty.getTotalResults().equals(1)) {
                return ERROR_VIEW;
            }
            LFPItems lfpItems = lateFilingPenalty.getItems().get(0);

            model.addAttribute("outstanding", lfpItems.getOutstanding());

            model.addAttribute("madeUpDate",
                    LocalDate.parse(lfpItems.getMadeUpDate(),
                    DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.UK))
                    .format(DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.UK)));

            model.addAttribute("dueDate",
                    LocalDate.parse(lfpItems.getDueDate(),
                    DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.UK))
                    .format(DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.UK)));

            //TODO: - GoTo CompanyDetails API to display Company Name instead of Company Number
            model.addAttribute("companyName", companyNumber);

        } catch (ServiceException e) {

            LOGGER.errorRequest(request, e.getMessage(), e);
            return ERROR_VIEW;
        }

        return getTemplateName();
    }

}
