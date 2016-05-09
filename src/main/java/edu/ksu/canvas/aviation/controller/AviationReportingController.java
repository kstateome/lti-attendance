package edu.ksu.canvas.aviation.controller;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.aviation.config.AppConfig;
import edu.ksu.canvas.aviation.form.RosterForm;
import edu.ksu.canvas.aviation.model.SectionInfo;
import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.repository.AttendanceRepository;
import edu.ksu.canvas.aviation.repository.AviationCourseRepository;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;
import edu.ksu.canvas.aviation.repository.MakeupTrackerRepository;
import edu.ksu.canvas.aviation.services.PersistenceService;
import edu.ksu.canvas.aviation.util.RoleChecker;
import edu.ksu.canvas.entity.lti.OauthToken;
import edu.ksu.canvas.enums.EnrollmentType;
import edu.ksu.canvas.enums.SectionIncludes;
import edu.ksu.canvas.error.InvalidInstanceException;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.canvas.error.OauthTokenRequiredException;
import edu.ksu.canvas.interfaces.EnrollmentsReader;
import edu.ksu.canvas.interfaces.SectionReader;
import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Section;
import edu.ksu.lti.LtiLaunch;
import edu.ksu.lti.LtiLaunchData;
import edu.ksu.lti.controller.LtiLaunchController;
import edu.ksu.lti.model.LtiSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Qualifier;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


@Controller
@Scope("session")
@SessionAttributes("rosterForm")
public class AviationReportingController extends LtiLaunchController {
    
    private static final Logger LOG = Logger.getLogger(AviationReportingController.class);

    @Autowired
    protected LtiLaunch ltiLaunch;

    @Autowired
    private PersistenceService persistenceService;

    @Autowired
    private RoleChecker roleChecker;

    @Autowired
    private CanvasApiFactory canvasApiFactory;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private AviationCourseRepository aviationCourseRepository;

    @Autowired
    private AviationStudentRepository aviationStudentRepository;

    @Autowired
    private MakeupTrackerRepository makeupTrackerRepository;

    @RequestMapping("/")
    public ModelAndView home(HttpServletRequest request) {
        LOG.info("Showing Activity Reporting configuration XML");
        String ltiLaunchUrl = AppConfig.getApplicationBaseUrl(request, true) + "/launch";
        LOG.debug("LTI launch URL: " + ltiLaunchUrl);
        return new ModelAndView("ltiConfigure", "url", ltiLaunchUrl);
    }

    @RequestMapping("/showRoster")
    public ModelAndView showRoster() throws NoLtiSessionException, OauthTokenRequiredException, InvalidInstanceException, IOException {
        RosterForm rosterForm = new RosterForm();
        ltiLaunch.ensureApiTokenPresent(getApplicationName());
        ltiLaunch.validateOAuthToken();
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        assertPrivilegedUser(ltiSession);
        OauthToken oauthToken = ltiSession.getCanvasOauthToken();

        // FIXME: Timeouts need to change
        EnrollmentsReader enrollmentsReader = canvasApiFactory.getReader(EnrollmentsReader.class, oauthToken.getToken());

        String courseID = ltiSession.getCanvasCourseId();
        SectionReader sectionReader = canvasApiFactory.getReader(SectionReader.class, oauthToken.getToken());
        List<Section> sections = sectionReader.listCourseSections(Integer.parseInt(courseID), Collections.singletonList(SectionIncludes.students));

        // Get section data
        // FIXME: Retrieve data for dates, attendance, from a database
        List<SectionInfo> sectionInfoList = new ArrayList<>();
        for (Section s : sections) {
            SectionInfo sectionInfo = new SectionInfo();
            List<Enrollment> enrollments = enrollmentsReader.getSectionEnrollments((int) s.getId(), Collections.singletonList(EnrollmentType.STUDENT));
            List<AviationStudent> students = new ArrayList<>();
            for (Enrollment e : enrollments) {
                AviationStudent student = new AviationStudent();
                student.setSisUserId(e.getUser().getSisUserId());
                student.setName(e.getUser().getSortableName());
                students.add(student);
            }
            sectionInfo.setTotalStudents(students.size());
            if (students.size() > 0) {
                sectionInfo.setStudents(students);
                sectionInfo.setSectionId(s.getId());
                sectionInfo.setSectionName(s.getName());
                sectionInfo.setCourseId(s.getCourseId());
                sectionInfoList.add(sectionInfo);
            }
        }
        rosterForm.setSectionInfoList(sectionInfoList);
        persistenceService.getCourseMinutes(rosterForm, ltiSession.getCanvasCourseId());
        ModelAndView page = new ModelAndView("showRoster");
        page.addObject("sectionList", sections);
        page.addObject("rosterForm", rosterForm);

        return page;
    }

    // TODO: implement
    private RosterForm getRosterForm() {
        return null;
    }

    @RequestMapping(value = "/editTotalClassMinutes", method = RequestMethod.POST)
    public ModelAndView saveTotalClassMinutes(@ModelAttribute("rosterForm") @Valid RosterForm rosterForm, BindingResult bindingResult) throws IOException, NoLtiSessionException {
        //TODO: Figure out way to show roster form appropriately (maybe just call ShowRoster()..)
        ModelAndView page = new ModelAndView("forward:showRoster");
        if (bindingResult.hasErrors()){
            LOG.info("There were errors submitting the minutes form"+ bindingResult.getAllErrors());
            page.addObject("error", "Invalid input for minutes, please enter valid minutes");
        }
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info(ltiSession.getEid() + " saving course settings for " + ltiSession.getCanvasCourseId() + ", minutes: "
                 + rosterForm.getClassTotalMinutes() + ", per session: " + rosterForm.getDefaultMinutesPerSession());

        persistenceService.saveCourseMinutes(rosterForm, ltiSession.getCanvasCourseId());
        return page;
    }

    // TODO: Implement Save
    @RequestMapping(value = "/saveAttendance")
    public String saveAttendance(@ModelAttribute("rosterForm") RosterForm rosterForm) throws IOException, NoLtiSessionException {
        return "showRoster";
    }

    @RequestMapping(value = "/selectSectionDropdown", method = RequestMethod.POST)
    public ModelAndView selectSection(@ModelAttribute("selectedSection") SectionInfo sectionInfo, @ModelAttribute RosterForm rosterForm) {

        //NOTE: this is temporary, set to get date 2016/04/21
        Calendar myCal = Calendar.getInstance();
        myCal.set(Calendar.YEAR, 2016);
        myCal.set(Calendar.MONTH, 4);
        myCal.set(Calendar.DAY_OF_MONTH, 21);

        ModelAndView page = new ModelAndView("showRoster");
        page.addObject("selectedSection", sectionInfo);
        page.addObject("rosterForm", rosterForm);
        return page;
    }




    @Override
    protected String getInitialViewPath() {
        return "/showRoster";
    }

    @Override
    protected String getApplicationName() {
        return "AviationReporting";
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