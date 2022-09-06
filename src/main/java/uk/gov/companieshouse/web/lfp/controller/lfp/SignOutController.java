package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.web.lfp.annotation.NextController;
import uk.gov.companieshouse.web.lfp.annotation.PreviousController;
import uk.gov.companieshouse.web.lfp.controller.BaseController;
import uk.gov.companieshouse.web.lfp.session.SessionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@NextController(LFPStartController.class)
@PreviousController(EnterLFPDetailsController.class)
@RequestMapping("/late-filing-penalty/sign-out")
public class SignOutController extends BaseController {

    private static String LFP_SIGN_OUT = "lfp/signOut";


    @Autowired
    private SessionService sessionService;




    private static final String USER_EMAIL = "userEmail";

    private static final String SIGN_IN_KEY = "signin_info";
    private static final String USER_PROFILE_KEY = "user_profile";
    private static final String EMAIL_KEY = "email";



    @Override
    protected String getTemplateName() {
        return LFP_SIGN_OUT;
    }

    private static final String REDIRECT_PAGE = "lfp/home";

    @GetMapping
    public String getSignOut(HttpServletResponse response,
                             final HttpServletRequest request, Model model) {

        LOGGER.debug("Processing sign out");
        Map<String, Object> sessionData = sessionService.getSessionDataFromContext();

        //check session is there first
        if (!sessionData.containsKey(SIGN_IN_KEY)) {
            return ERROR_VIEW;
        }

        return getTemplateName();

    }


    @PostMapping
    public String postSignOut( HttpServletRequest request, Model model) {
        Map<String, Object> sessionData = sessionService.getSessionDataFromContext();

        String valueGet = request.getParameter("radio");

        if(valueGet == null || valueGet == "") {
            LOGGER.info("radio: " + valueGet);
            model.addAttribute("errorMessage",true);
            return getTemplateName();
        }
        if (valueGet.equals("yes")) {

            LOGGER.info("value: " + valueGet);
            sessionData.clear();
            /*needs to go to generic ch local page, if you go there manually cache is cleared & no email, but if you go to first
            page of service, it signs you back in...
            */
            navigatorService.getNextControllerRedirect(this.getClass());
        }
        if (valueGet.equals("no")) {
//this needs to go to previous page (a the moment it is said via previous controller annotation to details page
            return navigatorService.getPreviousControllerPath(this.getClass());
        }
        return navigatorService.getNextControllerRedirect(this.getClass());
    }

}