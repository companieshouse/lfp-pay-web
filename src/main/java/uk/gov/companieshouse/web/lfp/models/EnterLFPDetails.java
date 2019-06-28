package uk.gov.companieshouse.web.lfp.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;
import uk.gov.companieshouse.web.lfp.annotation.Penalty;

@Getter
@Setter
public class EnterLFPDetails {

    /**
     * Returns different error messages depending on entered penalty.
     * No entered penalty returns `message`
     * Entered penalty but not a matching length to stringSize returns `messageNotLongEnough`
     */
    @Penalty(messageNotLongEnough = "{enterLfpDetails.penaltyNumber.wrongLength}",
            message = "{enterLfpDetails.penaltyNumber.emptyField}",
            stringSize = 8)
    private String penaltyNumber;

    /**
     * Allows any length of number under 8. e.g "6400" is allowed.
     * Only allows letters if the total length is 8.
     * Doesn't allow spaces or empty strings
     */
    @NotNull
    @Pattern(regexp = "^([a-zA-Z0-9]{8}|[0-9]{1,8})$", message = "{enterLfpDetails.companyNumber.wrongLength}")
    private String companyNumber;
}
