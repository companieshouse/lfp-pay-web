package uk.gov.companieshouse.web.lfp.validation;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.web.lfp.LFPWebApplication;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
public class AllowlistChecker {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(LFPWebApplication.APPLICATION_NAME_SPACE);

    private static final String HOME = "/late-filing-penalty";
    private static final String REGEX = "\\/late-filing-penalty[\\/[a-zA-Z0-9-]+]+$";
    private static final String SIGN_OUT = "late-filing-penalty/sign-out";
    private String sanitizedUrl = "";

    public String checkURL(String url) {
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(url);
        if (m.find()) {
            LOGGER.info("URL valid, returning to " + url);
            if (!checkSignOutIsReferer(url)) {
                setAssignedURL(url);
                return url;
            }
            LOGGER.info("Referer is sign out, returning to previous url: " + getAssignedURL());
            return getAssignedURL();
        }
        LOGGER.error("URL not valid. Returning home...");
        return HOME;
    }

    boolean checkSignOutIsReferer(String url) {
        Pattern p = Pattern.compile(SIGN_OUT);
        Matcher m = p.matcher(url);
        return m.find();
    }

    private void setAssignedURL(String url) {
        sanitizedUrl = url;
    }

    private String getAssignedURL() {
        return sanitizedUrl;
    }
}



