package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import uk.gov.companieshouse.session.Session;

import uk.gov.companieshouse.web.lfp.annotation.NextController;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.session.SessionService;

import uk.gov.companieshouse.web.lfp.validation.AllowlistChecker;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;


@Controller
@NextController(LFPStartController.class)
@PreviousController(EnterLFPDetailsController.class)
@RequestMapping("/late-filing-penalty/sign-out")
public class SignOutController extends BaseController {

    @Autowired
    private SessionService sessionService;


    @Autowired
    private AllowlistChecker allowlistChecker;


    private static final String LFP_SIGN_OUT = "lfp/signOut";
    private static final String SIGN_IN_KEY = "signin_info";
    private static final String ACCOUNT_URL = System.getenv("ACCOUNT_URL");


    @Override
    protected String getTemplateName() {
        return LFP_SIGN_OUT;
    }


    @GetMapping
    public String getSignOut(final HttpServletRequest request) {
        Map<String, Object> sessionData = sessionService.getSessionDataFromContext();
        if (!sessionData.containsKey(SIGN_IN_KEY)) {
            LOGGER.info("No session data present: " + sessionData);
            return ERROR_VIEW;
        }

        LOGGER.debug("Processing sign out");

        String referrer = request.getHeader("Referer");
        if (referrer == null) {
            LOGGER.info("No Referer has been found");
        } else {
            String allowedUrl = allowlistChecker.checkURL(referrer);
            if (allowlistChecker.checkSignOutIsReferer(allowedUrl)) {
                LOGGER.info("Refer is sign-out- not updating attribute");
                return getTemplateName();
            }
            LOGGER.info("Referer is " + allowedUrl);
            request.getSession().setAttribute("url_prior_signout", allowedUrl);
        }

        return getTemplateName();
    }


    @PostMapping
    public RedirectView postSignOut(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        LOGGER.debug("Processing sign out POST");
        String valueGet = request.getParameter("radio");

        Session s = sessionService.getSessionFromContext();

        if (valueGet == null || valueGet.equals("")) {
            LOGGER.info("radio: " + valueGet);
            redirectAttributes.addFlashAttribute("errorMessage", true);
            return new RedirectView("/late-filing-penalty/sign-out");
        }
        if (valueGet.equals("yes")) {
            return new RedirectView(ACCOUNT_URL + "/signout");
        }
        if (valueGet.equals("no") && s != null) {
            String url = (String) request.getSession().getAttribute("url_prior_signout");
            return new RedirectView(url);
        }
        return new RedirectView("/late-filing-penalty/");
    }


}