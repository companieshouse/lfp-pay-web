package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.api.model.latefilingpenalty.FinanceHealthcheck;
import uk.gov.companieshouse.api.model.latefilingpenalty.FinanceHealthcheckStatus;
import uk.gov.companieshouse.web.lfp.annotation.NextController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.latefilingpenalty.LateFilingPenaltyService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

@Controller
@NextController(EnterLFPDetailsController.class)
@RequestMapping("/late-filing-penalty")
public class LFPStartController extends BaseController {

    @Value("${account.url}")
    private String accountsUrl;

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
    public String getLFPHome(@RequestParam("start") Optional<Integer> startId, Model model) throws ParseException {

        FinanceHealthcheck financeHealthcheck;
        try {
            financeHealthcheck = LateFilingPenaltyService.checkFinanceSystemAvailableTime();
        } catch (ServiceException ex) {
            return ERROR_VIEW;
        }

        if (financeHealthcheck.getMessage().equals(FinanceHealthcheckStatus.HEALTHY.getStatus())) {
            if(startId.isPresent() && startId.get() == 0) {
                return navigatorService.getNextControllerRedirect(this.getClass());
            }

            return getTemplateName();
        } else if (financeHealthcheck.getMessage().equals(FinanceHealthcheckStatus.UNHEALTHY_PLANNED_MAINTENANCE.getStatus())) {
            DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            DateFormat displayDateFormat = new SimpleDateFormat("h:mm a z 'on' EEEE d MMMM yyyy");
            model.addAttribute("date", displayDateFormat.format(
                    inputDateFormat.parse(financeHealthcheck.getMaintenanceEndTime())));
            return LFP_SERVICE_UNAVAILABLE;
        } else {
            return ERROR_VIEW;
        }
    }


    @PostMapping
    public String postEnterDetails() {

        return navigatorService.getNextControllerRedirect(this.getClass());
    }
}
