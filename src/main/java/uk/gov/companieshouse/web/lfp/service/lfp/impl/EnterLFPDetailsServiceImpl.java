package uk.gov.companieshouse.web.lfp.service.lfp.impl;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.web.lfp.service.lfp.EnterLFPDetailsService;

@Service
public class EnterLFPDetailsServiceImpl implements EnterLFPDetailsService {

    @Override
    public String appendToCompanyNumber(String companyNumber) {

        //If the Company Number contains any letters don't append 0's to the beginning of the Company Number.
        if(!companyNumber.matches("^[0-9]*$")) {
            return companyNumber;
        }

        //If company number is less than 8 digits long append 0's to the beginning.
        companyNumber = String.format("%8s", companyNumber).replace(' ', '0');

        return companyNumber;
    }
}
