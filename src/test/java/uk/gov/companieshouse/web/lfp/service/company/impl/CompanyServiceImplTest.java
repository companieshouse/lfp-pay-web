package uk.gov.companieshouse.web.lfp.service.company.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.web.lfp.service.company.CompanyService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CompanyServiceImplTest {

    @InjectMocks
    private CompanyService mockCompanyService = new CompanyServiceImpl();

    private static final String COMPANY_NUMBER_WITH_LETTERS = "SE123456";

    private static final String COMPANY_NUMBER_WITH_EIGHT_DIGITS = "12345678";

    private static final String COMPANY_NUMBER_WITH_SIX_DIGITS = "123456";

    private static final String APPENDED_SIX_DIGIT_COMPANY_NUMBER = "00123456";

    @Test
    @DisplayName("Append - Do not append to company number with letters")
    void validateCompanyNumberWithLettersNotAppended() throws Exception {

        String companyNumber = mockCompanyService.appendToCompanyNumber(COMPANY_NUMBER_WITH_LETTERS);
        assertEquals(companyNumber, COMPANY_NUMBER_WITH_LETTERS);
    }

    @Test
    @DisplayName("Append - Eight Digit company number returned the same")
    void validationEightDigitCompanyNumberReturnedTheSame() throws Exception {

        String companyNumber = mockCompanyService.appendToCompanyNumber(COMPANY_NUMBER_WITH_EIGHT_DIGITS);
        assertEquals(companyNumber, COMPANY_NUMBER_WITH_EIGHT_DIGITS);
    }

    @Test
    @DisplayName("Append - Six Digit company number should have 0's appended to beginning")
    void validationSixDigitCompanyNumberReturnedWithAppendedZeros() throws Exception {

        String companyNumber = mockCompanyService.appendToCompanyNumber(COMPANY_NUMBER_WITH_SIX_DIGITS);
        assertEquals(companyNumber, APPENDED_SIX_DIGIT_COMPANY_NUMBER);
    }
}
