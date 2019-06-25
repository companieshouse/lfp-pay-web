package uk.gov.companieshouse.web.lfp.models.lfp;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnterLFPDetails {

    /**
     * Allows any length of number e.g "6400" is allowed.
     * Only allows letters if the total length is 8.
     * Doesn't allow spaces or empty strings
     */
    @NotNull
    @Pattern(regexp = "^([a-zA-Z0-9]{8}|[0-9]+$)$", message = "{enterLfpDetails.companyNumber.wrongLength}")

    private String companyNumber;
}
