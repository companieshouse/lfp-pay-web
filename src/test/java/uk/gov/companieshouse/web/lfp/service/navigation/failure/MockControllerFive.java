package uk.gov.companieshouse.web.lfp.service.navigation.failure;

import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.web.lfp.annotation.NextController;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.service.navigation.NavigatorServiceTests;

/**
 * Mock controller class.
 *
 * @see NavigatorServiceTests
 */
@NextController(MockControllerSix.class)
@PreviousController(MockControllerFour.class)
@RequestMapping("/mock-controller-five")
public class MockControllerFive extends BaseController {

    @Override
    protected String getTemplateName() {
        return null;
    }
}
