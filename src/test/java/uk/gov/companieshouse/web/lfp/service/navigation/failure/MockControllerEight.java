package uk.gov.companieshouse.web.lfp.service.navigation.failure;

import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.controller.ConditionalController;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;

/**
 * Mock conditional controller class for testing exception handling.
 *
 * @see 'NavigatorServiceTests'
 * @see uk.gov.companieshouse.web.lfp.exception.NavigationException
 */
@RequestMapping("/mock-controller-eight")
@PreviousController(MockControllerSeven.class)
public class MockControllerEight extends BaseController implements ConditionalController {

    @Override
    protected String getTemplateName() {
        return null;
    }

    @Override
    public boolean willRender(String companyNumber, String transactionId, String companylfpId) throws ServiceException {
        throw new ServiceException("Test exception", null);
    }
}
