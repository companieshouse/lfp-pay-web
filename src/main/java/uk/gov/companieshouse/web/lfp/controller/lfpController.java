package uk.gov.companieshouse.web.lfp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class lfpController extends BaseController{

    private static String LFP_START = "lfp/home";
    private static String LFP_ENTER_DETAILS = "lfp/lfpenterdetails";

    @GetMapping
    public String getPaymentSummary() {

        return getTemplateName();
    }

    @Override protected String getTemplateName() {
        return LFP_START;
    }

    @RequestMapping
    public String getLfpEnterDetails() {

        return LFP_ENTER_DETAILS;
    }

}
