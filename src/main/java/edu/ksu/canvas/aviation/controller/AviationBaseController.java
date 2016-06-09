package edu.ksu.canvas.aviation.controller;

import edu.ksu.canvas.aviation.config.AppConfig;
import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.services.AviationSectionService;
import edu.ksu.canvas.aviation.services.CanvasApiWrapperService;
import edu.ksu.canvas.aviation.services.SynchronizationService;
import edu.ksu.canvas.aviation.util.RoleChecker;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.lti.LtiLaunchData;
import edu.ksu.lti.controller.LtiLaunchController;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
@Scope("session")
public class AviationBaseController extends LtiLaunchController {

    private static final Logger LOG = Logger.getLogger(AviationBaseController.class);

    @Autowired
    protected SynchronizationService synchronizationService;

    @Autowired
    protected RoleChecker roleChecker;

    @Autowired
    protected AviationSectionService sectionService;

    @Autowired
    protected CanvasApiWrapperService canvasService;


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

    protected void ensureCanvasApiTokenPresent() throws NoLtiSessionException {
        canvasService.ensureApiTokenPresent(getApplicationName());
    }

    @RequestMapping("/initialize")
    public ModelAndView initialize() throws NoLtiSessionException {
        ensureCanvasApiTokenPresent();
        canvasService.validateOAuthToken();

        assertPrivilegedUser();

        synchronizationService.synchronizeWhenCourseNotExistsInDB(canvasService.getCourseId());

        return new ModelAndView("forward:roster");
    }

    protected AviationSection getSelectedSection(Long previousSelectedSectionId) throws NoLtiSessionException {
        if (previousSelectedSectionId == null) {
            return sectionService.getFirstSectionOfCourse(canvasService.getCourseId());
        } else {
            return sectionService.getSection(previousSelectedSectionId);
        }
    }


    /**
     * @throws NoLtiSessionException if the user doesn't have a LTISession
     * @throws AccessDeniedException if the user isn't authorized
     */
    private void assertPrivilegedUser() throws NoLtiSessionException {
        if (canvasService.getEid() == null || canvasService.getEid().isEmpty()) {
            throw new AccessDeniedException("You cannot access this content without a valid session");
        }

        List<LtiLaunchData.InstitutionRole> roles = canvasService.getRoles();
        if (!roleChecker.roleAllowed(roles)) {
            LOG.error("User (" + canvasService.getEid() + ") with insufficient privileges tried to launch. Roles: " + roles);
            throw new AccessDeniedException("You do not have sufficient privileges to use this tool");
        }
    }

}
