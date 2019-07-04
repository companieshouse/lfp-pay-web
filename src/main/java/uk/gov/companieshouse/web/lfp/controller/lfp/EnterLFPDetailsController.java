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
import uk.gov.companieshouse.web.lfp.annotation.NextController;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.models.EnterLFPDetails;
import uk.gov.companieshouse.web.lfp.service.lfp.EnterLFPDetailsService;

import javax.validation.Valid;

@Controller
@PreviousController(LFPStartController.class)
@NextController(ViewPenaltiesController.class)
@RequestMapping("/lfp/enter-details")
public class EnterLFPDetailsController extends BaseController {

    private static String LFP_ENTER_DETAILS = "lfp/details";

    @Autowired
    private EnterLFPDetailsService enterLFPDetailsService;

    @Override protected String getTemplateName() {
        return LFP_ENTER_DETAILS;
    }

    @GetMapping
    public String getLFPEnterDetails(Model model) {
        model.addAttribute("enterLFPDetails", new EnterLFPDetails());

        addBackPageAttributeToModel(model);

        return getTemplateName();
    }

    @PostMapping
    public String postLFPEnterDetails(@ModelAttribute("enterLFPDetails") @Valid EnterLFPDetails enterLFPDetails,
                                      BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return getTemplateName();
        }

        String companyNumber = enterLFPDetailsService.appendToCompanyNumber(enterLFPDetails.getCompanyNumber());

        if (companyNumber.equals("11111111")){
            return "lfp/onlinePaymentUnavailable";
        }

        return navigatorService.getNextControllerRedirect(this.getClass(), companyNumber);
    }

}



