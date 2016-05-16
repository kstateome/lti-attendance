package edu.ksu.canvas.aviation.controller;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.aviation.config.AppConfig;
import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.entity.MakeupTracker;
import edu.ksu.canvas.aviation.factory.SectionInfoFactory;
import edu.ksu.canvas.aviation.form.MakeupTrackerForm;
import edu.ksu.canvas.aviation.form.RosterForm;
import edu.ksu.canvas.aviation.repository.AviationSectionRepository;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;
import edu.ksu.canvas.aviation.repository.MakeupTrackerRepository;
import edu.ksu.canvas.aviation.repository.ReportRepository;
import edu.ksu.canvas.aviation.repository.ReportRepository.AttendanceSummaryForSection;
import edu.ksu.canvas.aviation.services.PersistenceService;
import edu.ksu.canvas.aviation.util.DropDownOrganizer;
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
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
    private ReportRepository reportRepository;
    
    @Autowired
    private SectionInfoFactory sectionInfoFactory;
    
    @Autowired
    private AviationStudentRepository studentRepository;
    
    @Autowired
    private MakeupTrackerRepository makeupTrackerRepository;
    
    @Autowired
    private AviationSectionRepository sectionRepository;

    
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
    
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
        OauthToken oauthToken = ltiSession.getCanvasOauthToken();

        EnrollmentsReader enrollmentsReader = canvasApiFactory.getReader(EnrollmentsReader.class, oauthToken.getToken());

        String courseID = ltiSession.getCanvasCourseId();
        SectionReader sectionReader = canvasApiFactory.getReader(SectionReader.class, oauthToken.getToken());
        List<Section> sections = sectionReader.listCourseSections(Integer.parseInt(courseID), Collections.singletonList(SectionIncludes.students));

        persistenceService.synchronizeCourseFromCanvasToDb(Long.valueOf(ltiSession.getCanvasCourseId()));
        persistenceService.synchronizeSectionsFromCanvasToDb(sections);
        persistenceService.synchronizeStudentsFromCanvasToDb(sections, enrollmentsReader);
        
        return showRoster(new Date(), String.valueOf(sections.get(0).getId()));
    }
    
    private ModelAndView setupPage(Date date, String sectionId, String viewName) throws NoLtiSessionException {
        ltiLaunch.ensureApiTokenPresent(getApplicationName()); //should be present on each call
        
        AviationSection aviationSection;
        List<AviationSection> aviationSections;
        if(sectionId == null) {
            LtiSession ltiSession = ltiLaunch.getLtiSession();
            aviationSections = sectionRepository.findByCanvasCourseId(Long.valueOf(ltiSession.getCanvasCourseId()));
            aviationSection = aviationSections.get(0);
            sectionId = String.valueOf(aviationSection.getCanvasSectionId());
        } else {
            aviationSection = sectionRepository.findByCanvasSectionId(Long.valueOf(sectionId));
            aviationSections = sectionRepository.findByCanvasCourseId(aviationSection.getCanvasCourseId());
        }
        
        //Sets the date to today if not already set
        if (date == null){
            date = new Date();
        }
        RosterForm rosterForm = new RosterForm();
        rosterForm.setCurrentDate(date);
        rosterForm.setSectionInfoList(sectionInfoFactory.getSectionInfos(aviationSections));
        persistenceService.loadCourseInfoIntoRoster(rosterForm, aviationSection.getCanvasCourseId());
        persistenceService.loadAttendanceIntoRoster(rosterForm, date);
        
        ModelAndView page = new ModelAndView(viewName);
        page.addObject("rosterForm", rosterForm);
        page.addObject("sectionList", DropDownOrganizer.sortWithSelectedSectionFirst(aviationSections, sectionId));
        page.addObject("selectedSectionId", sectionId);
        
        return page;
    }
    
    @RequestMapping("/showRoster")
    public ModelAndView showRoster(@RequestParam(required = false) Date date) throws NoLtiSessionException, OauthTokenRequiredException, InvalidInstanceException, IOException {
        return showRoster(date, null);
    }
  
    @RequestMapping("/showRoster/{sectionId}")
    public ModelAndView showRoster(@RequestParam(required = false) Date date, @PathVariable String sectionId) throws NoLtiSessionException, OauthTokenRequiredException, InvalidInstanceException, IOException {
        return setupPage(date, sectionId, "showRoster");
    }
    
    @RequestMapping("/classSetup")
    public ModelAndView classSetup() throws OauthTokenRequiredException, NoLtiSessionException, NumberFormatException, IOException {
        return classSetup(null);
    }

    @RequestMapping("/classSetup/{sectionId}")
    public ModelAndView classSetup(@PathVariable String sectionId) throws OauthTokenRequiredException, NoLtiSessionException, NumberFormatException, IOException {
        return setupPage(new Date(), sectionId, "setupClass");
    }

    
    @RequestMapping("/attendanceSummary")
    public ModelAndView attendanceSummary() throws NoLtiSessionException, OauthTokenRequiredException, InvalidInstanceException, IOException {
            LtiSession ltiSession = ltiLaunch.getLtiSession();
            List<AviationSection> aviationSections = sectionRepository.findByCanvasCourseId(Long.valueOf(ltiSession.getCanvasCourseId()));
            String sectionId = aviationSections.get(0).getCanvasSectionId().toString();
            
            return attendanceSummary(sectionId);
    }

    @RequestMapping("/attendanceSummary/{sectionId}")
    public ModelAndView attendanceSummary(@PathVariable String sectionId) throws NoLtiSessionException, OauthTokenRequiredException, InvalidInstanceException, IOException {
        ModelAndView page = setupPage(new Date(), sectionId, "attendanceSummary");
        
        List<AttendanceSummaryForSection> summaryForSections = reportRepository.getAttendanceSummary(new Long(sectionId));
        page.addObject("attendanceSummaryForSections", summaryForSections);
        
        return page;
    }
    
    
    @RequestMapping("/studentMakeup/{sectionId}/{studentId}")
    public ModelAndView studentMakeup(@PathVariable String sectionId, @PathVariable String studentId) {
        return studentMakeup(sectionId, studentId, false);
    }

    private ModelAndView studentMakeup(String sectionId, String studentId, boolean addEmptyEntry) {
        AviationStudent student = studentRepository.findByStudentId(new Long(studentId));
        List<MakeupTracker> makeupTrackers = makeupTrackerRepository.findByAviationStudent(student);
        if(addEmptyEntry) {
            makeupTrackers.add(new MakeupTracker());
        }
        
        MakeupTrackerForm makeupTrackerForm = new MakeupTrackerForm();
        makeupTrackerForm.setEntries(makeupTrackers);
        makeupTrackerForm.setSectionId(Long.valueOf(sectionId));
        makeupTrackerForm.setStudentId(Long.valueOf(studentId));
        
        ModelAndView page = new ModelAndView("studentMakeup");
        page.addObject("sectionId", sectionId);
        page.addObject("student", student);
        page.addObject("makeupTrackerForm", makeupTrackerForm);
        
        return page;
    }
    
    @RequestMapping(value = "/deleteMakeup/{sectionId}/{studentId}/{makeupTrackerId}")
    public ModelAndView deleteMakeup(@PathVariable String sectionId, @PathVariable String studentId, @PathVariable String makeupTrackerId) throws NoLtiSessionException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info("Attempting to delete makeup data... User: " + ltiSession.getEid());
        
        persistenceService.deleteMakeup(makeupTrackerId);
        return studentMakeup(sectionId, studentId);
    }

    
    @RequestMapping(value= "/save", params = "changeDate", method = RequestMethod.POST)
    public ModelAndView changeDate(@ModelAttribute("rosterForm") RosterForm rosterForm, @ModelAttribute("sectionId") String sectionId) throws IOException, NoLtiSessionException {
        return showRoster(rosterForm.getCurrentDate(), sectionId);
    }

    @RequestMapping(value = "/save", params ="saveClassMinutes", method = RequestMethod.POST)
    public ModelAndView saveTotalClassMinutes(@ModelAttribute("rosterForm") @Valid RosterForm rosterForm, BindingResult bindingResult) throws IOException, NoLtiSessionException {
        ModelAndView page = new ModelAndView("forward:classSetup");
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

        persistenceService.saveClassAttendance(rosterForm);
        return showRoster(rosterForm.getCurrentDate(), sectionId);
    }
    
    
    @RequestMapping(value = "/save", params = "saveMakeup", method = RequestMethod.POST)
    public ModelAndView saveMakeup(@ModelAttribute MakeupTrackerForm makeupTrackerForm, BindingResult bindingResult) throws NoLtiSessionException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info("Attempting to save makeup data... User: " + ltiSession.getEid());
        
        //FIXME: This is not appropriate!
        if (bindingResult.hasErrors()) {
            LOG.info("There were errors saving the Makeup form"+ bindingResult.getAllErrors());
            String errorMessage = "Invalid user input...";
            
            ModelAndView page = new ModelAndView("studentMakeup");
            AviationStudent student = studentRepository.findByStudentId(makeupTrackerForm.getStudentId());
            page.addObject("sectionId", String.valueOf(makeupTrackerForm.getSectionId()));
            page.addObject("student", student);
            page.addObject("makeupTrackerForm", makeupTrackerForm);
            page.addObject("error", errorMessage);
            
            return page;
        } else {
            persistenceService.saveMakeups(makeupTrackerForm);    
        }
        
        return studentMakeup(String.valueOf(makeupTrackerForm.getSectionId()), String.valueOf(makeupTrackerForm.getStudentId()), false);
    }
    
    @RequestMapping(value = "/save", params = "addMakeup", method = RequestMethod.POST)
    public ModelAndView addMakeup(@ModelAttribute MakeupTrackerForm makeupTrackerForm, BindingResult bindingResult) throws NoLtiSessionException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info("Attempting to save makeup data and add new entry... User: " + ltiSession.getEid());
        
        //FIXME: This is not appropriate!
        if (bindingResult.hasErrors()){
            LOG.info("There were errors saving the Makeup form"+ bindingResult.getAllErrors());
            String errorMessage = "Invalid user input...";
            
            ModelAndView page = new ModelAndView("studentMakeup");
            AviationStudent student = studentRepository.findByStudentId(makeupTrackerForm.getStudentId());
            page.addObject("sectionId", String.valueOf(makeupTrackerForm.getSectionId()));
            page.addObject("student", student);
            page.addObject("makeupTrackerForm", makeupTrackerForm);
            page.addObject("error", errorMessage);
            
            return page;
        } else {
            persistenceService.saveMakeups(makeupTrackerForm);
            makeupTrackerForm.getEntries().add(new MakeupTracker());
        }
        
        return studentMakeup(String.valueOf(makeupTrackerForm.getSectionId()), String.valueOf(makeupTrackerForm.getStudentId()), true);
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
