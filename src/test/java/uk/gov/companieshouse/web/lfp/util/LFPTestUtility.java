package uk.gov.companieshouse.web.lfp.util;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.api.model.latefilingpenalty.PayableLateFilingPenaltySession;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LFPTestUtility {

    public static final Integer VALID_AMOUNT = 750;
    public static final Integer PARTIAL_PAID_AMOUNT = 300;
    public static final String PENALTY_TYPE = "penalty";
    public static final String LEGAL_FEES_TYPE = "legal-fees";
    public static final String DATE = "2018-12-12";
    public static final String PENALTY_ID = "EXAMPLE1234";

    private LFPTestUtility() {
        throw new IllegalAccessError("Utility class");
    }


    public static LateFilingPenalty validLateFilingPenalty(String ID) {
        LateFilingPenalty lateFilingPenalty = new LateFilingPenalty();
        lateFilingPenalty.setId(ID);
        lateFilingPenalty.setPaid(false);
        lateFilingPenalty.setDca(false);
        lateFilingPenalty.setOriginalAmount(VALID_AMOUNT);
        lateFilingPenalty.setOutstanding(VALID_AMOUNT);
        lateFilingPenalty.setType(PENALTY_TYPE);
        lateFilingPenalty.setMadeUpDate(DATE);
        lateFilingPenalty.setDueDate(DATE);

        return lateFilingPenalty;
    }

    public static CompanyProfileApi validCompanyProfile(String ID) {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyNumber(ID);
        companyProfileApi.setCompanyName("TEST_COMPANY");

        return companyProfileApi;
    }

    public static LateFilingPenalty dcaLateFilingPenalty(String ID) {
        LateFilingPenalty lateFilingPenalty = new LateFilingPenalty();
        lateFilingPenalty.setId(ID);
        lateFilingPenalty.setPaid(false);
        lateFilingPenalty.setDca(true);
        lateFilingPenalty.setOriginalAmount(VALID_AMOUNT);
        lateFilingPenalty.setOutstanding(VALID_AMOUNT);
        lateFilingPenalty.setType(PENALTY_TYPE);

        return lateFilingPenalty;
    }

    public static LateFilingPenalty paidLateFilingPenalty(String ID) {
        LateFilingPenalty lateFilingPenalty = new LateFilingPenalty();
        lateFilingPenalty.setId(ID);
        lateFilingPenalty.setPaid(true);
        lateFilingPenalty.setDca(false);
        lateFilingPenalty.setOriginalAmount(VALID_AMOUNT);
        lateFilingPenalty.setOutstanding(VALID_AMOUNT);
        lateFilingPenalty.setType(PENALTY_TYPE);

        return lateFilingPenalty;
    }

    public static LateFilingPenalty negativeOustandingLateFilingPenalty(String ID) {
        LateFilingPenalty lateFilingPenalty = new LateFilingPenalty();
        lateFilingPenalty.setId(ID);
        lateFilingPenalty.setPaid(false);
        lateFilingPenalty.setDca(false);
        lateFilingPenalty.setOriginalAmount(-VALID_AMOUNT);
        lateFilingPenalty.setOutstanding(-VALID_AMOUNT);
        lateFilingPenalty.setType(PENALTY_TYPE);

        return lateFilingPenalty;
    }

    public static LateFilingPenalty partialPaidLateFilingPenalty(String ID) {
        LateFilingPenalty lateFilingPenalty = new LateFilingPenalty();
        lateFilingPenalty.setId(ID);
        lateFilingPenalty.setPaid(false);
        lateFilingPenalty.setDca(false);
        lateFilingPenalty.setOriginalAmount(VALID_AMOUNT);
        lateFilingPenalty.setOutstanding(PARTIAL_PAID_AMOUNT);
        lateFilingPenalty.setType(PENALTY_TYPE);

        return lateFilingPenalty;
    }

    public static LateFilingPenalty notPenaltyTypeLateFilingPenalty(String ID) {
        LateFilingPenalty lateFilingPenalty = new LateFilingPenalty();
        lateFilingPenalty.setId(ID);
        lateFilingPenalty.setPaid(false);
        lateFilingPenalty.setDca(false);
        lateFilingPenalty.setOriginalAmount(VALID_AMOUNT);
        lateFilingPenalty.setOutstanding(VALID_AMOUNT);
        lateFilingPenalty.setType(LEGAL_FEES_TYPE);

        return lateFilingPenalty;
    }

    public static PayableLateFilingPenaltySession payableLateFilingPenaltySession(String companyNumber) {
        PayableLateFilingPenaltySession payableLateFilingPenaltySession = new PayableLateFilingPenaltySession();
        Map<String, String> links = new HashMap<String, String>() {{
            put("self", "/company/" + companyNumber + "/penalties/late-filing/payable/" + PENALTY_ID);
        }};

        payableLateFilingPenaltySession.setId(PENALTY_ID);
        payableLateFilingPenaltySession.setLinks(links);

        return payableLateFilingPenaltySession;
    }
}
