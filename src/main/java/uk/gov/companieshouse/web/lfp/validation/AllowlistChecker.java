package uk.gov.companieshouse.web.lfp.validation;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.web.lfp.LFPWebApplication;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
public class AllowlistChecker {


    protected static final Logger LOGGER = LoggerFactory
            .getLogger(LFPWebApplication.APPLICATION_NAME_SPACE);

    List<String> uris = new ArrayList<String>();
    private static final String HOME = "late-filing-penalty";
    private static final String ENTER_DETAILS = "http://chs.local/late-filing-penalty/enter-details";
    private static final String REGEX = "\\/late-filing-penalty[\\/[a-zA-Z0-9-]+]+$";
    private static final String REGEX1 = "https?://[^/]+\\/late-filing-penalty(\\/[a-z-]+)+$/";
    private static final String REGEX2 = "https?://[^/]+\\/late-filing-penalty(\\/[a-z-]+)+$/,(\\/[a-z-]+), (\\/[a-z-]+), (\\/[a-z-]+) ";
//    private static final String STRIKE_OFF_OBJECTIONS_DOCUMENT_UPLOAD =  "/\/strike-off-objections\/document-upload/";


    private void addUrisToList() {
        uris.add(HOME);
        uris.add(ENTER_DETAILS);

    }

    public String checkURL(String url) {
        addUrisToList();
        LOGGER.info("url, 1: "+ url.matches(ENTER_DETAILS));
        LOGGER.info("url, 1: "+ url);
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(url);
        if (m.find()) {
            return url;
        }
        return "/late-filing-penalty/";
    }






}



