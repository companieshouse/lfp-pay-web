package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.gov.companieshouse.web.lfp.controller.BaseController;

@Controller
@RequestMapping("/late-filing-penalty/accessibility-statement")
public class AccessibilityStatementController extends BaseController {

    private static String LFP_ACCESSIBILITY = "lfp/accessibilityStatement";

    @Override protected String getTemplateName() {
        return LFP_ACCESSIBILITY;
    }

    @GetMapping
    public String getLfpAccessibilityStatement() {

        return getTemplateName();
    }
}