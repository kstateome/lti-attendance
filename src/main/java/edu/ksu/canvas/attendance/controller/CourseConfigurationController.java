package edu.ksu.canvas.attendance.controller;

import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.exception.AttendanceAssignmentException;
import edu.ksu.canvas.attendance.form.CourseConfigurationForm;
import edu.ksu.canvas.attendance.form.CourseConfigurationValidator;
import edu.ksu.canvas.attendance.form.InputValidator;
import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.services.AttendanceCourseService;
import edu.ksu.canvas.attendance.services.AttendanceSectionService;
import edu.ksu.canvas.attendance.services.ReportService;
import edu.ksu.canvas.attendance.services.SynchronizationService;
import edu.ksu.canvas.attendance.submitter.AssignmentSubmitter;
import edu.ksu.canvas.attendance.submitter.CanvasAssignmentAssistant;
import edu.ksu.lti.launch.exception.NoLtiSessionException;
import org.apache.commons.validator.routines.LongValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;



@Controller
@Scope("session")
@RequestMapping("/courseConfiguration")
public class CourseConfigurationController extends AttendanceBaseController {

    private static final Logger LOG = Logger.getLogger(CourseConfigurationController.class);

    @Autowired
    private SynchronizationService synchronizationService;

    @Autowired
    private AttendanceCourseService courseService;

    @Autowired
    private CourseConfigurationValidator validator;

    @Autowired
    private InputValidator inputValidator;

    @Autowired
    private AttendanceSectionService sectionService;

    @Autowired
    private AssignmentSubmitter assignmentSubmitter;

    @Autowired
    private ReportService reportService;

    @Autowired
    private CanvasAssignmentAssistant assignmentAssistant;


    @RequestMapping()
    public ModelAndView classSetup() throws NoLtiSessionException {
        return classSetup(null, false);
    }

    @RequestMapping("/{sectionId}")
    public ModelAndView classSetup(@PathVariable String sectionId,
                                   @RequestParam(defaultValue = "false", value = "updateSuccessful") boolean successful) throws NoLtiSessionException {

        LOG.info("eid: " + canvasService.getEid() + " is viewing course configuration...");

        Long validatedSectionId = LongValidator.getInstance().validate(sectionId);
        AttendanceSection selectedSection = validatedSectionId == null ? null : getSelectedSection(validatedSectionId);
        if(validatedSectionId == null || selectedSection == null) {
            return new ModelAndView("forward:roster");
        }

        ModelAndView page = new ModelAndView("courseConfiguration");

        CourseConfigurationForm courseConfigurationForm = new CourseConfigurationForm();
        courseService.loadIntoForm(courseConfigurationForm, selectedSection.getCanvasCourseId());
        sectionService.loadIntoForm(courseConfigurationForm, selectedSection.getCanvasCourseId());
        page.addObject("courseConfigurationForm", courseConfigurationForm);
        page.addObject("selectedSectionId", selectedSection.getCanvasSectionId());
        page.addObject("updateSuccessful", successful);
        return page;
    }

    @RequestMapping(value = "/{sectionId}/save", params = "saveCourseConfiguration", method = RequestMethod.POST)
    public ModelAndView saveCourseConfiguration(@PathVariable String sectionId, @ModelAttribute("courseConfigurationForm") @Valid CourseConfigurationForm classSetupForm, BindingResult bindingResult) throws NoLtiSessionException {

        inputValidator.validate(classSetupForm, bindingResult);
        if (bindingResult.hasErrors()) {
            ModelAndView page = new ModelAndView("/courseConfiguration");
            List<String> errors = new ArrayList<>();
            bindingResult.getFieldErrors().forEach(error -> errors.add(error.getCode()));
            page.addObject("error", errors);
            page.addObject("selectedSectionId", sectionId);
            return page;
        }

        validator.validate(classSetupForm, bindingResult);
        if (bindingResult.hasErrors()) {
            ModelAndView page = new ModelAndView("/courseConfiguration");
            List<String> errors = new ArrayList<>();
            bindingResult.getFieldErrors().forEach(error -> errors.add(error.getCode()));
            page.addObject("error", errors);
            page.addObject("selectedSectionId", sectionId);
            return page;
        } else {
            LOG.info("eid: " + canvasService.getEid() + " is saving course settings for " + canvasService.getCourseId() + ", minutes: "
                    + classSetupForm.getTotalClassMinutes() + ", per session: " + classSetupForm.getDefaultMinutesPerSession());

            courseService.save(classSetupForm, canvasService.getCourseId());
            sectionService.save(classSetupForm, canvasService.getCourseId());
            return new ModelAndView("forward:/courseConfiguration/" + sectionId + "?updateSuccessful=true");
        }

    }

    @RequestMapping(value = "/{sectionId}/save", params = "synchronizeWithCanvas", method = RequestMethod.POST)
    public ModelAndView synchronizeWithCanvas(@PathVariable String sectionId) throws NoLtiSessionException {
        LOG.info("eid: " + canvasService.getEid() + " is forcing a synchronization with Canvas for Canvas Course ID: " + canvasService.getCourseId());
        synchronizationService.synchronize(canvasService.getCourseId());
        
        ModelAndView page = new ModelAndView("forward:/courseConfiguration/" + sectionId);
        page.addObject("synchronizationSuccessful", true);

        return page;
    }

    @RequestMapping(value = "/{sectionId}/save", params = "pushGradesToCanvas", method = RequestMethod.POST)
    public ModelAndView pushGradesToCanvas(@PathVariable String sectionId, @ModelAttribute("courseConfigurationForm") @Valid CourseConfigurationForm classSetupForm, BindingResult bindingResult) throws NoLtiSessionException{

        inputValidator.validate(classSetupForm, bindingResult);
        if (bindingResult.hasErrors()) {
            ModelAndView page = new ModelAndView("/courseConfiguration");
            List<String> errors = new ArrayList<>();
            bindingResult.getFieldErrors().forEach(error -> errors.add(error.getCode()));
            page.addObject("error", errors);
            page.addObject("selectedSectionId", sectionId);
            return page;
        }

        validator.validate(classSetupForm, bindingResult);
        if (bindingResult.hasErrors()) {
            ModelAndView page = new ModelAndView("/courseConfiguration");
            List<String> errors = new ArrayList<>();
            bindingResult.getFieldErrors().forEach(error -> errors.add(error.getCode()));
            page.addObject("error", errors);
            page.addObject("selectedSectionId", sectionId);
            return page;
        } else {
            LOG.info("eid: " + canvasService.getEid() + " is pushing grades for course # " + canvasService.getCourseId() + " to Canvas");
            ModelAndView page = new ModelAndView("forward:/courseConfiguration/" + sectionId);

            Long courseId = Long.valueOf(canvasService.getCourseId());

            boolean isSimpleAttendance = classSetupForm.getSimpleAttendance();
            List<AttendanceSummaryModel> summaryForSections = isSimpleAttendance ?
                    reportService.getSimpleAttendanceSummaryReport(Long.parseLong(sectionId)) :
                    reportService.getAviationAttendanceSummaryReport(Long.parseLong(sectionId));

            AttendanceAssignment assignmentConfigurationFromSetup = generateAssignmentFromClassSetupForm(classSetupForm);
            try {
                assignmentSubmitter.submitCourseAttendances(isSimpleAttendance, summaryForSections, courseId, canvasService.getOauthToken(), assignmentConfigurationFromSetup);
                page.addObject("pushingSuccessful", true);
            }
            catch (AttendanceAssignmentException e){
                page.addObject("error", e.getMessage());
            }

            return page;
        }
    }

    private AttendanceAssignment generateAssignmentFromClassSetupForm(CourseConfigurationForm classSetupForm) {
        AttendanceAssignment assignmentConfigurationFromSetup = new AttendanceAssignment();
        assignmentConfigurationFromSetup.setAssignmentPoints(classSetupForm.getAssignmentPoints());
        assignmentConfigurationFromSetup.setGradingOn(true);
        assignmentConfigurationFromSetup.setPresentPoints(classSetupForm.getPresentPoints());
        assignmentConfigurationFromSetup.setTardyPoints(classSetupForm.getTardyPoints());
        assignmentConfigurationFromSetup.setExcusedPoints(classSetupForm.getExcusedPoints());
        assignmentConfigurationFromSetup.setAbsentPoints(classSetupForm.getAbsentPoints());
        assignmentConfigurationFromSetup.setAssignmentName(classSetupForm.getAssignmentName());
        return assignmentConfigurationFromSetup;
    }

    @RequestMapping(value = "/{sectionId}/delete", method = RequestMethod.POST)
    public ModelAndView deleteAttendanceAssignment(@PathVariable String sectionId) throws NoLtiSessionException {
        LOG.info("eid: " + canvasService.getEid() + " is turning off grading feature and deleting the assignment from Canvas for section: " + sectionId);
        ModelAndView page = new ModelAndView("forward:/courseConfiguration/" + sectionId);

        try {

            assignmentAssistant.deleteAssignmentInCanvas(canvasService.getCourseId().longValue(), canvasService.getOauthToken());
        } catch (Exception exception) {
            page.addObject("error", exception.getMessage());
            return page;
        }

        sectionService.resetAttendanceAssignmentsForCourse(canvasService.getCourseId());
        page.addObject("deleteSuccessful", true);

        return page;
    }

}
