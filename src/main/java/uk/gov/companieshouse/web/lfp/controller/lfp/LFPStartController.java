package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.web.lfp.annotation.NextController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import java.util.Objects;

@Controller
@NextController(EnterLFPDetailsController.class)
@RequestMapping("/late-filing-penalty")
public class LFPStartController extends BaseController {

    private static String LFP_TEMP_HOME = "lfp/home";

    @Override protected String getTemplateName() {
        return LFP_TEMP_HOME;
    }


    @Autowired
    private Environment environment;

    @GetMapping
    public String getLFPHome() {
        if (Objects.equals(environment.getProperty("maintenance"), "1")) {
            return "lfp/serviceUnavailable";
        } else {
            return getTemplateName();
        }
    }


    @PostMapping
    public String postEnterDetails() {

        return navigatorService.getNextControllerRedirect(this.getClass());
    }
}
