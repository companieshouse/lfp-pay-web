package uk.gov.companieshouse.web.lfp.service.company.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.web.lfp.api.ApiClientService;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.company.CompanyService;

@Service
public class CompanyServiceImpl implements CompanyService {

    private static final UriTemplate GET_COMPANY_URI =
            new UriTemplate("/company/{companyNumber}");

    @Autowired
    private ApiClientService apiClientService;

    @Override
    public String appendToCompanyNumber(String companyNumber) {

        //If the Company Number contains any letters don't append 0's to the beginning of the Company Number.
        if(!companyNumber.matches("^[0-9]*$")) {
            return companyNumber;
        }

        //If company number is less than 8 digits long append 0's to the beginning.
        return String.format("%8s", companyNumber).replace(' ', '0');
    }

    @Override
    public CompanyProfileApi getCompanyProfile(String companyNumber) throws ServiceException {
        ApiClient apiClient = apiClientService.getPublicApiClient();
        CompanyProfileApi companyProfileApi;

        try {
            String uri = GET_COMPANY_URI.expand(companyNumber).toString();
            companyProfileApi = apiClient.company().get(uri).execute().getData();
        } catch (ApiErrorResponseException ex) {
            throw new ServiceException("Error retrieving Company Details", ex);
        } catch (URIValidationException ex) {
            throw new ServiceException("Invalid URI for Company Details", ex);
        }

        return companyProfileApi;
    }
}
