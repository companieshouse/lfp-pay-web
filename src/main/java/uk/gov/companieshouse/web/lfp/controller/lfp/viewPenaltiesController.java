package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.models.EnterLFPDetails;
import uk.gov.companieshouse.web.lfp.service.lfp.EnterLFPDetailsService;

@Controller
@PreviousController(EnterLFPDetailsController.class)
@RequestMapping("/lfp/view-penalties/{companyNumber}")
public class viewPenaltiesController extends BaseController {


    private static String LFP_VIEW_PENALTIES = "lfp/viewPenalties";

    @Override protected String getTemplateName() {
        return LFP_VIEW_PENALTIES;
    }

    @GetMapping
    public String getLFPEnterDetails(Model model) {
//        model.addAttribute("viewPenalties", new EnterLFPDetails());

        addBackPageAttributeToModel(model);


        return getTemplateName();
    }

//    @PostMapping
//    public String postLFPViewPenalties(@ModelAttribute("viewPenalties") Model model) {
////
////        if (bindingResult.hasErrors()) {
////            return getTemplateName();
////        }
//
////        String companyNumber = enterLFPDetailsService.appendToCompanyNumber(enterLFPDetails.getCompanyNumber());
//
//        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + companyNumber;
//    }
}
