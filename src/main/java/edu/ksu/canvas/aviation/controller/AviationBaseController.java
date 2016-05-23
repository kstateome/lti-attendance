package edu.ksu.canvas.aviation.controller;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.aviation.config.AppConfig;
import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.services.SynchronizationService;
import edu.ksu.canvas.aviation.services.AviationSectionService;
import edu.ksu.canvas.aviation.util.RoleChecker;
import edu.ksu.canvas.error.InvalidInstanceException;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.canvas.error.OauthTokenRequiredException;
import edu.ksu.lti.LtiLaunch;
import edu.ksu.lti.LtiLaunchData;
import edu.ksu.lti.controller.LtiLaunchController;
import edu.ksu.lti.model.LtiSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;


@Controller
@Scope("session")
public class AviationBaseController extends LtiLaunchController {
    
    private static final Logger LOG = Logger.getLogger(AviationBaseController.class);

    @Autowired
    protected LtiLaunch ltiLaunch;

    @Autowired
    protected SynchronizationService canvasSynchronizationService;
    
    @Autowired
    protected CanvasApiFactory canvasApiFactory;

    @Autowired
    protected RoleChecker roleChecker;
    
    @Autowired
    protected AviationSectionService sectionService;

    
    @Override
    protected String getInitialViewPath() {
        return "/initialize";
    }

    @Override
    protected String getApplicationName() {
        return "AviationReporting";
    }

    @RequestMapping("/")
    public ModelAndView home(HttpServletRequest request) {
        LOG.info("Showing Activity Reporting configuration XML");
        String ltiLaunchUrl = AppConfig.getApplicationBaseUrl(request, true) + "/launch";
        LOG.debug("LTI launch URL: " + ltiLaunchUrl);
        return new ModelAndView("ltiConfigure", "url", ltiLaunchUrl);
    }
    
    @RequestMapping("/initialize")
    public ModelAndView initialize() throws OauthTokenRequiredException, InvalidInstanceException, NoLtiSessionException, IOException {
        ltiLaunch.ensureApiTokenPresent(getApplicationName());
        ltiLaunch.validateOAuthToken();
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        assertPrivilegedUser(ltiSession);

        long canvasCourseId = Long.valueOf(ltiSession.getCanvasCourseId());
        canvasSynchronizationService.synchronizeWhenCourseNotExistsInDB(ltiSession, canvasCourseId);
        
        return new ModelAndView("forward:roster");
    }
    
    protected AviationSection getSelectedSection(String previousSelectedSectionId) throws NoLtiSessionException {

        if(previousSelectedSectionId == null) {
            LtiSession ltiSession = ltiLaunch.getLtiSession();
            long canvasCourseId = Long.valueOf(ltiSession.getCanvasCourseId());
            return sectionService.getFirstSectionOfCourse(canvasCourseId);
            
        } else {
            long sectionId = Long.valueOf(previousSelectedSectionId);
            return sectionService.getSection(sectionId);
        }
    }
    

    private void assertPrivilegedUser(LtiSession ltiSession) throws NoLtiSessionException, AccessDeniedException {
        if (ltiSession.getEid() == null || ltiSession.getEid().isEmpty()) {
            throw new AccessDeniedException("You cannot access this content without a valid session");
        }
        LtiLaunchData launchData = ltiSession.getLtiLaunchData();
        List<LtiLaunchData.InstitutionRole> roles = launchData.getRolesList();
        if (!roleChecker.roleAllowed(roles)) {
            LOG.error("User (" + ltiSession.getEid() + ") with insufficient privileges tried to launch. Roles: " + ltiSession.getLtiLaunchData().getRoles());
            throw new AccessDeniedException("You do not have sufficient privileges to use this tool");
        }
    }

}
