package uk.gov.companieshouse.web.lfp.service.navigation.success;

import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.web.lfp.annotation.NextController;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.controller.ConditionalController;

/**
 * Mock controller class for success scenario testing of navigation.
 */
@NextController(MockSuccessJourneyControllerThree.class)
@PreviousController(MockSuccessJourneyControllerOne.class)
@RequestMapping("/mock-success-journey-controller-two/{companyNumber}/{transactionId}/{companylfpId}")
public class MockSuccessJourneyControllerTwo extends BaseController implements ConditionalController {

    @Override
    protected String getTemplateName() {
        return null;
    }

    @Override
    public boolean willRender(String companyNumber, String transactionId, String companylfpId) {
        return false;
    }
}
