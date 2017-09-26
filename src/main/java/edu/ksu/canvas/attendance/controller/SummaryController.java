package edu.ksu.canvas.attendance.controller;

import edu.ksu.canvas.attendance.entity.Attendance;
import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import edu.ksu.canvas.attendance.exception.MissingSisIdException;
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

    @Autowired
    private AttendanceAssignmentService assignmentService;


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
        if(validatedStudentId == null || validatedStudentId < 0) {
            throw new MissingSisIdException("Invalid student id", false);
        }

        AttendanceStudent student = studentService.getStudent(validatedStudentId);
        if(student == null) {
            throw new IllegalArgumentException("Student does not exist in database.");
        }

        MakeupForm makeupForm = makeupService.createMakeupForm(validatedStudentId, validatedSectionId, addEmptyEntry);

        //Checking if Attendance Summary is Simple or Aviation
        AttendanceSection selectedSection = getSelectedSection(validatedSectionId);
        AttendanceAssignment assignment = assignmentService.findBySection(selectedSection);
        CourseConfigurationForm courseConfigurationForm = new CourseConfigurationForm();
        if (selectedSection != null) {
            courseService.loadIntoForm(courseConfigurationForm, selectedSection.getCanvasCourseId());
        }
        final boolean isSimpleAttendance = courseConfigurationForm.getSimpleAttendance();

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
                .ifPresent(entry ->  addAssignmentSummaryToPage(page, entry, isSimpleAttendance, student, assignment));
        institutionRoles.stream()
                .filter(institutionRole -> institutionRole.equals(LtiLaunchData.InstitutionRole.Learner))
                .findFirst()
                .ifPresent(role -> page.addObject("isStudent", true));

        page.addObject("sectionId", sectionId);
        page.addObject("student", student);
        page.addObject("summaryForm", makeupForm);
        return page;
    }

    private void addAssignmentSummaryToPage(ModelAndView page, AttendanceSummaryModel.Entry entry, boolean isSimpleAttendance, AttendanceStudent student, AttendanceAssignment assignment) {
        page.addObject("attendanceSummaryEntry", isSimpleAttendance ?
            new AttendanceSummaryModel.Entry(entry.getCourseId(), entry.getSectionId(), entry.getStudentId(), entry.getSisUserId(), entry.getStudentName(), student.getDeleted(), entry.getTotalClassesTardy(), entry.getTotalClassesMissed(), entry.getTotalClassesExcused(), entry.getTotalClassesPresent())
            : new AttendanceSummaryModel.Entry(entry.getCourseId(), entry.getSectionId(), entry.getStudentId(), entry.getSisUserId(), entry.getStudentName(), student.getDeleted(), entry.getSumMinutesMadeup(), entry.getRemainingMinutesMadeup(), entry.getSumMinutesMissed(), entry.getPercentCourseMissed()));

        int totalPresentDays = entry.getTotalClassesPresent();
        int totalTardyDays  = entry.getTotalClassesTardy();
        int totalAbsentDays = entry.getTotalClassesMissed();
        int totalExcusedDays = entry.getTotalClassesExcused();
        int totalDays = totalPresentDays + totalTardyDays + totalAbsentDays + totalExcusedDays;

        page.addObject("totalPresentDays", totalPresentDays);
        page.addObject("totalTardyDays", totalTardyDays);
        page.addObject("totalAbsentDays", totalAbsentDays);
        page.addObject("totalExcusedDays", totalExcusedDays);
        page.addObject("totalDays", totalDays);

        if (assignment != null && totalDays != 0) {
            Long presentWeight = Long.valueOf(assignment.getPresentPoints());
            Long tardyWeight = Long.valueOf(assignment.getTardyPoints());
            Long absentWeight = Long.valueOf(assignment.getAbsentPoints());
            Long excusedWeight = Long.valueOf(assignment.getExcusedPoints());
            Long assignmentPoints = Long.valueOf(assignment.getAssignmentPoints());

            Long presentMultiplier = presentWeight / 100;
            Long tardyMultiplier = tardyWeight / 100;
            Long absentMultiplier = absentWeight / 100;
            Long excusedMultiplier = excusedWeight / 100;

            Long presentDaysTimesMultiplier = totalPresentDays * presentMultiplier;
            Long tardyDaysTimesMultiplier = totalTardyDays * tardyMultiplier;
            Long absentDaysTimesMultiplier = totalAbsentDays * absentMultiplier;
            Long excusedDaysTimesMultiplier = totalExcusedDays * excusedMultiplier;

            Long totalPresentPoints = presentDaysTimesMultiplier * assignmentPoints;
            Long totalTardyPoints = tardyDaysTimesMultiplier * assignmentPoints;
            Long totalAbsentPoints = absentDaysTimesMultiplier * assignmentPoints;
            Long totalExcusedPoints = excusedDaysTimesMultiplier * assignmentPoints;

            Long sumStudentsPoints = totalPresentPoints + totalTardyPoints + totalAbsentPoints + totalExcusedPoints;

            Long studentFinalGrade = sumStudentsPoints / totalDays;

            page.addObject("presentWeight", presentWeight);
            page.addObject("tardyWeight", tardyWeight);
            page.addObject("absentWeight", absentWeight);
            page.addObject("excusedWeight", excusedWeight);
            page.addObject("assignmentPoints", assignmentPoints);
            page.addObject("presentMultiplier", presentMultiplier);
            page.addObject("tardyMultiplier", tardyMultiplier);
            page.addObject("absentMultiplier", absentMultiplier);
            page.addObject("excusedMultiplier", excusedMultiplier);
            page.addObject("presentDaysTimesMultiplier", presentDaysTimesMultiplier);
            page.addObject("tardyDaysTimesMultiplier", tardyDaysTimesMultiplier);
            page.addObject("absentDaysTimesMultiplier", absentDaysTimesMultiplier);
            page.addObject("excusedDaysTimesMultiplier", excusedDaysTimesMultiplier);
            page.addObject("totalPresentPoints", totalPresentPoints);
            page.addObject("totalTardyPoints", totalTardyPoints);
            page.addObject("totalAbsentPoints", totalAbsentPoints);
            page.addObject("totalExcusedPoints", totalExcusedPoints);
            page.addObject("sumStudentsPoints", sumStudentsPoints);
            page.addObject("studentFinalGrade", studentFinalGrade);
        }
    }
}
