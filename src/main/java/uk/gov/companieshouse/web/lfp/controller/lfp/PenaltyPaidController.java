package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.company.CompanyService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@PreviousController(EnterLFPDetailsController.class)
@RequestMapping("/late-filing-penalty/company/{companyNumber}/penalty/{penaltyNumber}/penalty-paid")
public class PenaltyPaidController extends BaseController {

    private static final String LFP_PENALTY_PAID = "lfp/penaltyPaid";

    @Override protected String getTemplateName() {
        return LFP_PENALTY_PAID;
    }

    @Autowired
    private CompanyService companyService;

    @GetMapping
    public String getLfpNoPenaltyFound(@PathVariable String companyNumber,
                                       @PathVariable String penaltyNumber,
                                       Model model,
                                       HttpServletRequest request) {

        CompanyProfileApi companyProfileApi;

        try {
            companyProfileApi = companyService.getCompanyProfile(companyNumber);
        } catch (ServiceException ex) {
            LOGGER.errorRequest(request, ex.getMessage(), ex);
            return ERROR_VIEW;
        }

        model.addAttribute("companyName", companyProfileApi.getCompanyName());
        model.addAttribute("penaltyNumber", penaltyNumber);

        addBackPageAttributeToModel(model);

        return getTemplateName();
    }

}
