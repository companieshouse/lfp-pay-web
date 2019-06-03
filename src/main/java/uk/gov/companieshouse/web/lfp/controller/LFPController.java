package uk.gov.companieshouse.web.lfp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lfp/temp")
public class LFPController extends BaseController{

    private static String LFP_TEMP_HOME = "lfp/home";
    private static String LFP_ENTER_DETAILS = "lfp/details";

    @Override protected String getTemplateName() {
        return LFP_TEMP_HOME;
    }


    @GetMapping
    public String getLFPHome() {
        return getTemplateName();
    }

    @RequestMapping
    public String getLFPDetails() {
        return LFP_ENTER_DETAILS;
    }

}
