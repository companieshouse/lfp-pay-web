package uk.gov.companieshouse.web.lfp.validation;

import org.thymeleaf.util.StringUtils;
import uk.gov.companieshouse.web.lfp.annotation.Penalty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PenaltyValidator implements ConstraintValidator<Penalty, String> {

    private String message;
    private String messageNotLongEnough;

    @Override
    public void initialize(Penalty penalty) {
        message = penalty.message();
        messageNotLongEnough = penalty.messageNotLongEnough();
    }

    @Override
    public boolean isValid(String penaltyNumber, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        if(penaltyNumber == null || StringUtils.isEmpty(penaltyNumber)) {
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        } else if (!penaltyNumber.matches("^([0-9]{8,16})$")) {
            context.buildConstraintViolationWithTemplate(messageNotLongEnough).addConstraintViolation();
            return false;
        }
        return true;
    }
}
