package edu.ksu.canvas.attendance.controller;

import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import edu.ksu.canvas.attendance.services.AttendanceSectionService;
import edu.ksu.canvas.attendance.services.AttendanceStudentService;
import edu.ksu.canvas.attendance.services.CanvasApiWrapperService;
import edu.ksu.canvas.attendance.services.SynchronizationService;
import edu.ksu.canvas.attendance.util.RoleChecker;
import edu.ksu.lti.launch.controller.LtiLaunchController;
import edu.ksu.lti.launch.controller.OauthController;
import edu.ksu.lti.launch.exception.NoLtiSessionException;
import edu.ksu.lti.launch.model.LtiLaunchData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;


@Controller
@Scope("session")
public class AttendanceBaseController extends LtiLaunchController {

    private static final Logger LOG = Logger.getLogger(AttendanceBaseController.class);

    @Autowired
    protected SynchronizationService synchronizationService;

    @Autowired
    protected RoleChecker roleChecker;

    @Autowired
    protected AttendanceSectionService sectionService;

    @Autowired
    protected CanvasApiWrapperService canvasService;

    @Autowired
    protected AttendanceStudentService attendanceStudentService;


    @Override
    protected String getInitialViewPath() {
        return "/initialize";
    }

    @Override
    protected String getApplicationName() {
        return "Attendance";
    }

    @RequestMapping("/")
    public ModelAndView home(HttpServletRequest request) {
        LOG.info("Showing Activity Reporting configuration XML");
        String ltiLaunchUrl = OauthController.getApplicationBaseUrl(request, true) + "/launch";
        LOG.debug("LTI launch URL: " + ltiLaunchUrl);
        return new ModelAndView("ltiConfigure", "url", ltiLaunchUrl);
    }

    protected void ensureCanvasApiTokenPresent() throws NoLtiSessionException {
        canvasService.ensureApiTokenPresent();
    }

    @RequestMapping("/initialize")
    public ModelAndView initialize() throws NoLtiSessionException, IOException {
        ensureCanvasApiTokenPresent();
        canvasService.validateOAuthToken();

        assertPrivilegedUser();

        synchronizationService.synchronizeWhenCourseNotExistsInDB(canvasService.getCourseId());

        List<LtiLaunchData.InstitutionRole> roleList = canvasService.getRoles();
        boolean hasOneAuthorityRole = roleList != null && (roleList.contains(LtiLaunchData.InstitutionRole.Instructor) || roleList.contains(LtiLaunchData.InstitutionRole.TeachingAssistant)  || roleList.contains(LtiLaunchData.InstitutionRole.));

        for(LtiLaunchData.InstitutionRole role : roleList) {
            if (role.compareTo(LtiLaunchData.InstitutionRole.Learner) == 0 && !hasOneAuthorityRole) {
                LOG.info(canvasService.getEid() + " is accessing student summary information");
                AttendanceStudent attendanceStudent = attendanceStudentService.getStudent(canvasService.getSisID());
                if (attendanceStudent == null) {
                    LOG.info("Adding synchronizing to Attendance Database");

                    synchronizationService.synchronize(canvasService.getCourseId());
                    attendanceStudent = attendanceStudentService.getStudent(canvasService.getSisID());
                    if (attendanceStudent == null) {
                        LOG.info("Failed synchronizing student into Attendance Database");
                        return new ModelAndView("studentSyncFailed");
                    }
                }
                return new ModelAndView("forward:studentSummary/"+ attendanceStudent.getCanvasSectionId().toString()+"/"+ attendanceStudent.getStudentId().toString());
            }
        }
        return new ModelAndView("forward:roster");
    }

    protected AttendanceSection getSelectedSection(Long previousSelectedSectionId) throws NoLtiSessionException {
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
