package edu.ksu.canvas.attendance.controller;

import edu.ksu.canvas.attendance.entity.Attendance;
import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import edu.ksu.canvas.attendance.form.MakeupForm;
import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.services.AttendanceStudentService;
import edu.ksu.canvas.attendance.services.CanvasApiWrapperService;
import edu.ksu.canvas.attendance.services.MakeupService;
import edu.ksu.canvas.attendance.services.ReportService;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.lti.LtiLaunchData;
import org.apache.commons.validator.routines.LongValidator;
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

    @Autowired
    private MakeupService makeupService;

    @Autowired
    private AttendanceStudentService studentService;

    @Autowired
    private ReportService reportService;

    @Autowired
    protected CanvasApiWrapperService canvasService;


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
            throw new IllegalArgumentException("Invalid student id");
        }

        AttendanceStudent student = studentService.getStudent(validatedStudentId);
        if(student == null) {
            throw new IllegalArgumentException("Student does not exist in database.");
        }

        MakeupForm makeupForm = makeupService.createMakeupForm(validatedStudentId, validatedSectionId, addEmptyEntry);

        ModelAndView page = new ModelAndView("studentSummary");

        List<AttendanceSummaryModel> summaryForSections = reportService.getAviationAttendanceSummaryReport(validatedSectionId);
        List<LtiLaunchData.InstitutionRole> institutionRoles = canvasService.getRoles();

        student.getAttendances().sort(Comparator.comparing(Attendance::getDateOfClass).reversed());

        summaryForSections.stream()
                .flatMap(summary -> summary.getEntries().stream())
                .filter(entry -> entry.getStudentId() == validatedStudentId)
                .findFirst()
                .ifPresent(entry -> page.addObject("attendanceSummaryEntry",
                        new AttendanceSummaryModel.Entry(entry.getCourseId(), entry.getSectionId(), entry.getStudentId(), entry.getStudentName(), student.getDeleted(), entry.getSumMinutesMadeup(), entry.getRemainingMinutesMadeup(), entry.getSumMinutesMissed(), entry.getPercentCourseMissed())));

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
