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
@RequestMapping("/late-filing-penalty/company/{companyNumber}/penalty/{penaltyNumber}/no-penalties-found")
public class NoPenaltyFoundController extends BaseController {

    private static final String LFP_NO_PENALTY_FOUND = "lfp/noPenaltyFound";

    @Override protected String getTemplateName() {
        return LFP_NO_PENALTY_FOUND;
    }

    @GetMapping
    public String getLfpNoPenaltyFound(@PathVariable String companyNumber,
                                       @PathVariable String penaltyNumber,
                                       Model model) {

        model.addAttribute("companyNumber", companyNumber);
        model.addAttribute("penaltyNumber", penaltyNumber);

        addBackPageAttributeToModel(model);

        return getTemplateName();
    }
}
