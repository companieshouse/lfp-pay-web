package uk.gov.companieshouse.web.lfp.service.navigation.failure;

import uk.gov.companieshouse.web.lfp.controller.BaseController;

/**
 * Mock controller class for testing missing navigation annotations {@code NextController}
 * and {@code PreviousController}.
 *
 * @see 'NavigatorServiceTests'
 */
public class MockControllerThree extends BaseController {

    @Override
    protected String getTemplateName() {
        return null;
    }
}
