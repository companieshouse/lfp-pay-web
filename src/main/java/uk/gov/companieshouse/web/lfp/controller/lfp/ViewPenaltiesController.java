package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;

@Controller
@PreviousController(EnterLFPDetailsController.class)
@RequestMapping("/lfp/view-penalties/{companyNumber}")
public class ViewPenaltiesController extends BaseController {


    private static String LFP_VIEW_PENALTIES = "lfp/viewPenalties";

    @Override protected String getTemplateName() {
        return LFP_VIEW_PENALTIES;
    }

    @GetMapping
    public String getViewPenalties(Model model) {

        addBackPageAttributeToModel(model);


        return getTemplateName();
    }

}
