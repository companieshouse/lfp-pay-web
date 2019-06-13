package uk.gov.companieshouse.web.lfp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;

import javax.servlet.http.HttpServletRequest;

@Controller
@PreviousController(LFPController.class)
@RequestMapping("/lfp/enter-details")
public class LFPEnterDetailsController extends BaseController {

    private static String LFP_ENTER_DETAILS = "lfp/details";

    @Override protected String getTemplateName() {
        return LFP_ENTER_DETAILS;
    }

    @GetMapping
    public String getLFPEnterDetails() {
        return getTemplateName();
    }

//    @PostMapping
//    public String postEnterDetails(HttpServletRequest request) {
//
//        return navigatorService.getNextControllerRedirect(this.getClass());
//    }

}



