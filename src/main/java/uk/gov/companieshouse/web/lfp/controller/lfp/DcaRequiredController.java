package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;

@Controller
@PreviousController(EnterLFPDetailsController.class)
@RequestMapping("/late-filing-penalty/company/{companyNumber}/penalty/{penaltyNumber}/legal-fees-required")
public class DcaRequiredController extends BaseController {

    private static String LFP_DCA_REQUIRED = "lfp/legalFeesDCA";

    @Override protected String getTemplateName() {
        return LFP_DCA_REQUIRED;
    }

    @GetMapping
    public String getLfpNoPenaltyFound(@PathVariable String companyNumber,
                                       @PathVariable String penaltyNumber,
                                       Model model) {

        addBackPageAttributeToModel(model);

        return getTemplateName();
    }

}
