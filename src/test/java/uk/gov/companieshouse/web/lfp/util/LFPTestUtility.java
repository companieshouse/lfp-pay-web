package uk.gov.companieshouse.web.lfp.util;

import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;

public class LFPTestUtility {

    public static final Integer VALID_AMOUNT = 750;
    public static final Integer PARTIAL_PAID_AMOUNT = 300;
    public static final String PENALTY_TYPE = "penalty";
    public static final String LEGAL_FEES_TYPE = "legal-fees";

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

        return lateFilingPenalty;
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
}
