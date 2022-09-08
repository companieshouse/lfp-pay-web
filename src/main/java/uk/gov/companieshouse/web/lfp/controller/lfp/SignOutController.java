package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.view.RedirectView;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.handler.SessionHandler;
import uk.gov.companieshouse.web.lfp.annotation.NextController;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.session.SessionService;
import uk.gov.companieshouse.web.lfp.session.impl.SessionServiceImpl;
import uk.gov.companieshouse.web.lfp.validation.AllowlistChecker;
import uk.gov.companieshouse.session.handler.SessionHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.logging.Logger;

@Controller
@NextController(LFPStartController.class)
@PreviousController(EnterLFPDetailsController.class)
@RequestMapping("/late-filing-penalty/sign-out")
public class SignOutController extends BaseController {

    private static String LFP_SIGN_OUT = "lfp/signOut";


    @Autowired
    private SessionService sessionService;


    @Autowired
    private AllowlistChecker allowlistChecker;


    private static final String USER_EMAIL = "userEmail";

    private static final String SIGN_IN_KEY = "signin_info";
    private static final String USER_PROFILE_KEY = "user_profile";
    private static final String EMAIL_KEY = "email";


    @Override
    protected String getTemplateName() {
        return LFP_SIGN_OUT;
    }


    @GetMapping
    public String getSignOut(HttpServletResponse response,
                             final HttpServletRequest request, Model model) {


        LOGGER.debug("Processing sign out");
        Map<String, Object> sessionData = sessionService.getSessionDataFromContext();
        String referrer = request.getHeader("Referer");
        // LOGGER.info("session data 1: " + sessionData);

        if (referrer != null) {
            LOGGER.info("url: " + referrer);
            String allowedUrl = allowlistChecker.checkURL(referrer);
            request.getSession().setAttribute("url_prior_signout", allowedUrl);
        }
//        //check session is there first
//        if (!sessionData.containsKey(SIGN_IN_KEY)) {
//            LOGGER.info("No session data present: " + sessionData);
//            return ERROR_VIEW;
//        }
        return getTemplateName();
    }



    @PostMapping
    public RedirectView postSignOut(HttpServletRequest request, Model model) {
        LOGGER.debug("Processing sign out POST");
        // Map<String, Object> sessionData = sessionService.getSessionDataFromContext();
        // HttpSession session = request.getSession(false);
        String account = System.getenv("CHS_URL");
        String valueGet = request.getParameter("radio");

        Session s = sessionService.getSessionFromContext();
        Map<String, Object> sessionData = sessionService.getSessionDataFromContext();

        if(valueGet == null || valueGet.equals("")) {
            LOGGER.info("radio: " + valueGet);
            model.addAttribute("errorMessage",true);
            return new RedirectView("late-filing-penalty/sign-out");
        }
        if (valueGet.equals("yes")) {
            LOGGER.info("Before : " + s );


            LOGGER.info("sign in info : " + s.getSignInInfo() );
            s.clear();
            sessionData.clear();


            LOGGER.info("sign in info 2: " + s.getSignInInfo() );
            LOGGER.info("session data " + sessionData);



            //LOGGER.info("session data 2: " + sessionData2);

            return new RedirectView(account);
        }
        if (valueGet.equals("no")) {
            if (s != null) {
                String url = (String) request.getSession().getAttribute("url_prior_signout");
                return new RedirectView(url);
            }
        }
        return new RedirectView("/late-filing-penalty/sign-out");
    }




}