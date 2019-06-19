package uk.gov.companieshouse.web.lfp.controller;

import uk.gov.companieshouse.web.lfp.exception.ServiceException;

/**
 * The {@code ConditionalController} interface defines a single method that
 * should be implemented in controller classes whose template is rendered
 * conditionally (i.e. the controller will only render its template
 * dependent on some condition, such as the presence or absence of data).
 * <p>
 * The {@code willRender} implementation should return boolean true to
 * indicate that the template will be rendered, otherwise false.
 */
public interface ConditionalController {

    /**
     * Returns a boolean value indicating whether the template associated
     * with a controller will be rendered or not. Any {@link ServiceException}
     * generated when determining whether the controller should render or
     * not should be thrown from this method and will be handled by the
     * navigation service and result in an error page being returned to the
     * user agent.
     *
     * @param companyNumber     the company number
     * @param transactionId     the transaction identifier
     * @param companyAccountsId the company accounts identifier
     *
     * @see uk.gov.companieshouse.web.lfp.service.navigation.NavigatorService
     * @see uk.gov.companieshouse.web.lfp.exception.NavigationException
     *
     * @return true if the template for a controller will be rendered
     */
    boolean willRender(String companyNumber, String transactionId, String companyAccountsId) throws ServiceException;
}
