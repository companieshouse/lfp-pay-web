package uk.gov.companieshouse.web.pps.controller.pps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import uk.gov.companieshouse.web.pps.annotation.NextController;
import uk.gov.companieshouse.web.pps.annotation.PreviousController;
import uk.gov.companieshouse.web.pps.controller.BaseController;
import uk.gov.companieshouse.web.pps.session.SessionService;

import uk.gov.companieshouse.web.pps.validation.AllowlistChecker;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;


@Controller
@NextController(PPSStartController.class)
@PreviousController(EnterPPSDetailsController.class)
@RequestMapping("/late-filing-penalty/sign-out")
public class SignOutController extends BaseController {

    @Autowired
    private SessionService sessionService;


    @Autowired
    private AllowlistChecker allowlistChecker;


    private static final String PPS_SIGN_OUT = "pps/signOut";
    private static final String SIGN_IN_KEY = "signin_info";
    private static final String ACCOUNT_URL = System.getenv("ACCOUNT_URL");
    private static final String SIGN_OUT_URL = "/late-filing-penalty/sign-out";
    private static final String HOME = "/late-filing-penalty/";
    private static final String BACK_BUTTON = "backButton";


    @Override
    protected String getTemplateName() {
        return PPS_SIGN_OUT;
    }


    @GetMapping
    public String getSignOut(final HttpServletRequest request, Model model) {



        Map<String, Object> sessionData = sessionService.getSessionDataFromContext();
        if (!sessionData.containsKey(SIGN_IN_KEY)) {
            LOGGER.info("No session data present: " + sessionData);
            return ERROR_VIEW;
        }

        LOGGER.debug("Processing sign out");

        String referrer = request.getHeader("Referer");
        if (referrer == null) {
            model.addAttribute(BACK_BUTTON, HOME);
            LOGGER.info("No Referer has been found");
        } else {
            String allowedUrl = allowlistChecker.checkURL(referrer);
            if (allowlistChecker.checkSignOutIsReferer(allowedUrl)) {
                LOGGER.info("Refer is sign-out- not updating attribute");
                return getTemplateName();
            }
            LOGGER.info("Referer is " + allowedUrl);
            request.getSession().setAttribute("url_prior_signout", allowedUrl);
            model.addAttribute(BACK_BUTTON, allowedUrl);
        }
        return getTemplateName();
    }


    @PostMapping
    public RedirectView postSignOut(HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
        LOGGER.debug("Processing sign out POST");
        String valueGet = request.getParameter("radio");
        String url =  (String) request.getSession().getAttribute("url_prior_signout");

        if (valueGet == null || valueGet.equals("")) {
            redirectAttributes.addFlashAttribute("errorMessage", true);
            redirectAttributes.addFlashAttribute(BACK_BUTTON, url);
            return new RedirectView(SIGN_OUT_URL, true, false);
        }
        if (valueGet.equals("yes")) {
            return new RedirectView(ACCOUNT_URL + "/signout");
        }
        if (valueGet.equals("no")) {
            return new RedirectView(url);
        }
        return new RedirectView(HOME);
    }
}