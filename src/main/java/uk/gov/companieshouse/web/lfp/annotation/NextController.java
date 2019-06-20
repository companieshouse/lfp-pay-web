package uk.gov.companieshouse.web.lfp.annotation;

import uk.gov.companieshouse.web.lfp.controller.BaseController;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines the next controller to navigate to in the linear journey
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NextController {
    Class<? extends BaseController>[] value();
}
