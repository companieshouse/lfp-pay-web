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
@RequestMapping("/company/{companyNumber}/penalty/{penaltyNumber}/lfp/online-payment-unavailable")
public class OnlinePaymentUnavailableController extends BaseController {

    private static String LFP_ONLINE_PAYMENT_UNAVAILABLE= "lfp/onlinePaymentUnavailable";

    @Override protected String getTemplateName() {
        return LFP_ONLINE_PAYMENT_UNAVAILABLE;
    }

    @GetMapping
    public String getLfpNoPenaltyFound(@PathVariable String companyNumber,
                                       @PathVariable String penaltyNumber,
                                       Model model) {

        addBackPageAttributeToModel(model);

        return getTemplateName();
    }

}
