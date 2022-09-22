package uk.gov.companieshouse.web.lfp.service.navigation.failure;

import uk.gov.companieshouse.web.lfp.annotation.NextController;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.service.navigation.NavigatorServiceTests;

/**
 * Mock controller class for testing missing {@code RequestMapping} value
 * when searching backwards in the controller chain.
 *
 * @see NavigatorServiceTests
 */
@NextController(MockControllerEight.class)
@PreviousController(MockControllerSix.class)
public class MockControllerSeven extends BaseController {

    @Override
    protected String getTemplateName() {
        return null;
    }
}

