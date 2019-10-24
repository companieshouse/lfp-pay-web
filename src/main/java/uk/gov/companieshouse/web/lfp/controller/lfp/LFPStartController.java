package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.web.lfp.annotation.NextController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.latefilingpenalty.LateFilingPenaltyService;

@Controller
@NextController(EnterLFPDetailsController.class)
@RequestMapping("/late-filing-penalty")
public class LFPStartController extends BaseController {

    private static String LFP_TEMP_HOME = "lfp/home";
    private static String LFP_SERVICE_UNAVAILABLE = "lfp/serviceUnavailable";

    @Autowired
    private LateFilingPenaltyService LateFilingPenaltyService;

    @Override
    protected String getTemplateName() {
        return LFP_TEMP_HOME;
    }

    @Autowired
    private Environment environment;

    @GetMapping
    public String getLFPHome(Model model) {
        Object serviceAvailableTime = null;
        try {
            serviceAvailableTime = LateFilingPenaltyService.checkFinanceSystemAvailableTime();
        } catch (ServiceException ex) {

            if (serviceAvailableTime != null) {
                model.addAttribute("date", serviceAvailableTime);
                return LFP_SERVICE_UNAVAILABLE;
            }
            return getTemplateName();
        }
        if (serviceAvailableTime == null) {

            return LFP_SERVICE_UNAVAILABLE;
        }
        return getTemplateName();
    }


    @PostMapping
    public String postEnterDetails() {

        return navigatorService.getNextControllerRedirect(this.getClass());
    }
}
