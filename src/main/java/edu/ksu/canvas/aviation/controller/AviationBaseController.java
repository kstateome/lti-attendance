package edu.ksu.canvas.aviation.controller;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.aviation.config.AppConfig;
import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.repository.AviationSectionRepository;
import edu.ksu.canvas.aviation.services.PersistenceService;
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
    protected PersistenceService persistenceService;
    
    @Autowired
    protected CanvasApiFactory canvasApiFactory;

    @Autowired
    protected RoleChecker roleChecker;
    
    @Autowired
    private AviationSectionRepository sectionRepository;

    
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
        if(persistenceService.shouldAutomaticallySynchornizeWithCanvas(canvasCourseId)) {
            persistenceService.synchronizeWithCanvas(ltiSession, canvasCourseId);
        }
        
        return new ModelAndView("forward:roster");
    }
    
    protected static class SectionState {
        AviationSection selectedSection;
        List<AviationSection> sections;
    }
    
    protected SectionState getSectionState(String sectionId) throws NoLtiSessionException {
        SectionState ret = new SectionState();
        
        if(sectionId == null) {
            LtiSession ltiSession = ltiLaunch.getLtiSession();
            ret.sections = sectionRepository.findByCanvasCourseId(Long.valueOf(ltiSession.getCanvasCourseId()));
            ret.selectedSection = ret.sections.get(0);
            sectionId = String.valueOf(ret.selectedSection.getCanvasSectionId());
        } else {
            ret.selectedSection = sectionRepository.findByCanvasSectionId(Long.valueOf(sectionId));
            ret.sections = sectionRepository.findByCanvasCourseId(ret.selectedSection.getCanvasCourseId());
        }
        
        return ret;
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
