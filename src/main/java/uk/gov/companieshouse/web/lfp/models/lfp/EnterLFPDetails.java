package uk.gov.companieshouse.web.lfp.models.lfp;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class EnterLFPDetails {

    @NotNull
    @Length(min = 8, max = 8, message = "{enterLfpDetails.companyNumber.wrongLength}")

    private String companyNumber;
}
