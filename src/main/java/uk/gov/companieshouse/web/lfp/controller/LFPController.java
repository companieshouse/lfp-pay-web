package uk.gov.companieshouse.web.lfp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.web.lfp.annotation.NextController;

import javax.servlet.http.HttpServletRequest;

@Controller
@NextController(LFPEnterDetailsController.class)
@RequestMapping("/lfp/temp")
public class LFPController extends BaseController{

    private static String LFP_TEMP_HOME = "lfp/home";

    @Override protected String getTemplateName() {
        return LFP_TEMP_HOME;
    }


    @GetMapping
    public String getLFPHome() {
        return getTemplateName();
    }


    @PostMapping
    public String postEnterDetails(HttpServletRequest request) {

        return navigatorService.getNextControllerRedirect(this.getClass());
    }
}
