package edu.ksu.canvas.aviation.controller;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.aviation.config.AppConfig;
import edu.ksu.canvas.aviation.factory.SectionInfoFactory;
import edu.ksu.canvas.aviation.form.RosterForm;
import edu.ksu.canvas.aviation.model.SectionInfo;
import edu.ksu.canvas.aviation.services.PersistenceService;
import edu.ksu.canvas.aviation.util.RoleChecker;
import edu.ksu.canvas.entity.lti.OauthToken;
import edu.ksu.canvas.enums.SectionIncludes;
import edu.ksu.canvas.error.InvalidInstanceException;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.canvas.error.OauthTokenRequiredException;
import edu.ksu.canvas.interfaces.EnrollmentsReader;
import edu.ksu.canvas.interfaces.SectionReader;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;


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
    private SectionInfoFactory sectionInfoFactory;

    @RequestMapping("/")
    public ModelAndView home(HttpServletRequest request) {
        LOG.info("Showing Activity Reporting configuration XML");
        String ltiLaunchUrl = AppConfig.getApplicationBaseUrl(request, true) + "/launch";
        LOG.debug("LTI launch URL: " + ltiLaunchUrl);
        return new ModelAndView("ltiConfigure", "url", ltiLaunchUrl);
    }

    @RequestMapping("/showRoster")
    public ModelAndView showRoster(@RequestParam(required = false) Date date) throws NoLtiSessionException, OauthTokenRequiredException, InvalidInstanceException, IOException {
        RosterForm rosterForm = new RosterForm();
        //Sets the date to today if not already set
        if (date == null){
            date = new Date();
        }
        rosterForm.setCurrentDate(date);
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
        for (Section section : sections) {
            sectionInfoList.add(sectionInfoFactory.getSectionInfo(section, enrollmentsReader));
        }

        rosterForm.setSectionInfoList(sectionInfoList);
        rosterForm = persistenceService.loadOrCreateCourseMinutes(rosterForm, ltiSession.getCanvasCourseId());
        rosterForm = persistenceService.populateAttendanceForDay(rosterForm, date);
        ModelAndView page = new ModelAndView("showRoster");
        page.addObject("sectionList", sections);
        page.addObject("rosterForm", rosterForm);

        return page;
    }

    @RequestMapping("/attendanceSummary/{sectionId}")
    public ModelAndView attendanceSummary(@PathVariable String sectionId) throws NoLtiSessionException, OauthTokenRequiredException, InvalidInstanceException, IOException {
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
        for (Section section : sections) {
            SectionInfo sectionInfo = new SectionInfo(section, enrollmentsReader);
            if(sectionInfo.getTotalStudents() > 0) {
                sectionInfoList.add(sectionInfo);
            }
        }
        rosterForm.setSectionInfoList(sectionInfoList);
        persistenceService.loadOrCreateCourseMinutes(rosterForm, ltiSession.getCanvasCourseId());
        ModelAndView page = new ModelAndView("attendanceSummary");
        page.addObject("sectionList", sections);
        page.addObject("rosterForm", rosterForm);

        return page;
    }

    // TODO: implement
    private RosterForm getRosterForm() {
        return null;
    }
    
    
    @RequestMapping(value= "/save", params = "changeDate", method = RequestMethod.POST)
    public ModelAndView changeDate(@ModelAttribute("rosterForm") RosterForm rosterForm) throws IOException, NoLtiSessionException {
        return showRoster(rosterForm.getCurrentDate());
    }

    @RequestMapping(value = "/save", params ="saveClassMinutes", method = RequestMethod.POST)
    public ModelAndView saveTotalClassMinutes(@ModelAttribute("rosterForm") @Valid RosterForm rosterForm, BindingResult bindingResult) throws IOException, NoLtiSessionException {
        //TODO: Figure out way to show roster form appropriately (maybe just call ShowRoster()..)
        ModelAndView page = new ModelAndView("forward:showRoster");
        if (bindingResult.hasErrors()){
            LOG.info("There were errors submitting the minutes form"+ bindingResult.getAllErrors());
            page.addObject("error", "Invalid input for minutes, please enter valid minutes");
        }
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info(ltiSession.getEid() + " saving course settings for " + ltiSession.getCanvasCourseId() + ", minutes: "
                 + rosterForm.getTotalClassMinutes() + ", per session: " + rosterForm.getDefaultMinutesPerSession());

        persistenceService.saveCourseMinutes(rosterForm, ltiSession.getCanvasCourseId());
        return page;
    }

    @RequestMapping(value = "/save", params = "saveAttendance", method = RequestMethod.POST)
    public ModelAndView saveAttendance(@ModelAttribute("rosterForm") RosterForm rosterForm, @ModelAttribute("sectionId") String sectionId) throws IOException, NoLtiSessionException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info("Attempting to save section attendance for section : " + sectionId + " User: " + ltiSession.getEid());
        OauthToken oauthToken = ltiSession.getCanvasOauthToken();
        rosterForm.getSectionInfoList().stream().forEachOrdered(section -> {
            LOG.debug("Max attendances in section: " + section.getStudents().stream().map(student -> student.getAttendances().size()).max(Integer::max).get());
        });

        persistenceService.saveClassAttendance(rosterForm);
        return showRoster(rosterForm.getCurrentDate());
    }

    @RequestMapping(value = "/selectSectionDropdown", method = RequestMethod.POST)
    public ModelAndView selectSection(@ModelAttribute("selectedSection") SectionInfo sectionInfo, @ModelAttribute RosterForm rosterForm) {
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

    private Section getSection(String sectionId, OauthToken oauthToken) throws IOException {
        SectionReader sectionReader = canvasApiFactory.getReader(SectionReader.class, oauthToken.getToken());
        return sectionReader.getSingleSection(sectionId);
    }

    
}
