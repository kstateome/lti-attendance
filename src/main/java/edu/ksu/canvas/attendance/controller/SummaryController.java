package edu.ksu.canvas.attendance.controller;

import edu.ksu.canvas.attendance.entity.Attendance;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import edu.ksu.canvas.attendance.exception.MissingSisIdStudentException;
import edu.ksu.canvas.attendance.form.CourseConfigurationForm;
import edu.ksu.canvas.attendance.form.MakeupForm;
import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.services.*;
import edu.ksu.lti.launch.exception.NoLtiSessionException;
import edu.ksu.lti.launch.model.LtiLaunchData;
import org.apache.commons.validator.routines.LongValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


@Controller
@Scope("session")
@RequestMapping("/studentSummary")
public class SummaryController extends AttendanceBaseController {

    private static final Logger LOG = Logger.getLogger(SummaryController.class);

    @Autowired
    private MakeupService makeupService;

    @Autowired
    private AttendanceStudentService studentService;

    @Autowired
    private ReportService reportService;

    @Autowired
    protected CanvasApiWrapperService canvasService;

    @Autowired
    private AttendanceCourseService courseService;


    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    @RequestMapping("/{sectionId}/{studentId}")
    public ModelAndView studentSummary(@PathVariable String sectionId, @PathVariable String studentId) throws NoLtiSessionException {
        return studentSummary(sectionId, studentId, false);
    }

    private ModelAndView studentSummary(String sectionId, String studentId, boolean addEmptyEntry) throws NoLtiSessionException {
        Long validatedSectionId = LongValidator.getInstance().validate(sectionId);
        if(validatedSectionId == null) {
            throw new IllegalArgumentException("Invalid section id.");
        }

        Long validatedStudentId = LongValidator.getInstance().validate(studentId);
        if(validatedStudentId == null) {
            throw new MissingSisIdStudentException("Invalid student id");
        }

        AttendanceStudent student = studentService.getStudent(validatedStudentId);
        if(student == null) {
            throw new IllegalArgumentException("Student does not exist in database.");
        }

        MakeupForm makeupForm = makeupService.createMakeupForm(validatedStudentId, validatedSectionId, addEmptyEntry);

        //Checking if Attendance Summary is Simple or Aviation
        AttendanceSection selectedSection = getSelectedSection(validatedSectionId);
        CourseConfigurationForm courseConfigurationForm = new CourseConfigurationForm();
        boolean isSimpleAttendance = false;
        if (selectedSection != null){
            courseService.loadIntoForm(courseConfigurationForm, selectedSection.getCanvasCourseId());
            isSimpleAttendance = courseConfigurationForm.getSimpleAttendance();
        }

        ModelAndView page = isSimpleAttendance ?
                new ModelAndView("simpleStudentSummary") : new ModelAndView("studentSummary");

        List<AttendanceSummaryModel> summaryForSections = isSimpleAttendance ?
                reportService.getSimpleAttendanceSummaryReport(validatedSectionId) :
                reportService.getAviationAttendanceSummaryReport(validatedSectionId);
        List<LtiLaunchData.InstitutionRole> institutionRoles = canvasService.getRoles();

        student.getAttendances().sort(Comparator.comparing(Attendance::getDateOfClass).reversed());

        summaryForSections.stream()
                .flatMap(summary -> summary.getEntries().stream())
                .filter(entry -> entry.getStudentId() == validatedStudentId)
                .findFirst()
                .ifPresent(entry ->  page.addObject("attendanceSummaryEntry", courseConfigurationForm.getSimpleAttendance() ?
                            new AttendanceSummaryModel.Entry(entry.getCourseId(), entry.getSectionId(), entry.getStudentId(), entry.getStudentName(), student.getDeleted(), entry.getTotalClassesTardy(), entry.getTotalClassesMissed())
                          : new AttendanceSummaryModel.Entry(entry.getCourseId(), entry.getSectionId(), entry.getStudentId(), entry.getStudentName(), student.getDeleted(), entry.getSumMinutesMadeup(), entry.getRemainingMinutesMadeup(), entry.getSumMinutesMissed(), entry.getPercentCourseMissed())));

        institutionRoles.stream()
                .filter(institutionRole -> institutionRole.equals(LtiLaunchData.InstitutionRole.Learner))
                .findFirst()
                .ifPresent(role -> page.addObject("isStudent", true));


        page.addObject("sectionId", sectionId);
        page.addObject("student", student);
        page.addObject("summaryForm", makeupForm);

        return page;
    }

}
