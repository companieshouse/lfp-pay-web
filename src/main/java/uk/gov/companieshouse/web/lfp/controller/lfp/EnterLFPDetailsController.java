package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.models.lfp.EnterLFPDetails;

import javax.validation.Valid;

@Controller
@PreviousController(LFPStartController.class)
@RequestMapping("/lfp/enter-details")
public class EnterLFPDetailsController extends BaseController {

    private static String LFP_ENTER_DETAILS = "lfp/details";

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
                                      BindingResult bindingResult, Model model, RedirectAttributes attributes) {

        addBackPageAttributeToModel(model);

        if (bindingResult.hasErrors()) {
            return getTemplateName();
        }

        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + enterLFPDetails.getCompanyNumber();
    }

}



