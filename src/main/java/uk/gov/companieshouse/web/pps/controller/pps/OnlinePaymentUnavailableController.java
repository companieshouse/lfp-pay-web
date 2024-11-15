package uk.gov.companieshouse.web.pps.controller.pps;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.web.pps.annotation.PreviousController;
import uk.gov.companieshouse.web.pps.controller.BaseController;

@Controller
@PreviousController(EnterPPSDetailsController.class)
@RequestMapping("/late-filing-penalty/company/{companyNumber}/penalty/{penaltyNumber}/online-payment-unavailable")
public class OnlinePaymentUnavailableController extends BaseController {

    private static final String PPS_ONLINE_PAYMENT_UNAVAILABLE = "pps/onlinePaymentUnavailable";

    @Override protected String getTemplateName() {
        return PPS_ONLINE_PAYMENT_UNAVAILABLE;
    }

    @GetMapping
    public String getPpsNoPenaltyFound(@PathVariable String companyNumber,
                                       @PathVariable String penaltyNumber,
                                       Model model) {

        addBackPageAttributeToModel(model);

        return getTemplateName();
    }

}
