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
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


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
        List<AttendanceStudent> studentList = new ArrayList<>();
        List<AttendanceSection> sectionList = new ArrayList<>();
        if (selectedSection != null) {
            sectionList.addAll(sectionService.getSectionByCanvasCourseId(selectedSection.getCanvasCourseId()));
            studentList.addAll(studentService.getStudentByCourseAndSisId(student.getSisUserId(), selectedSection.getCanvasCourseId()));
            courseService.loadIntoForm(courseConfigurationForm, selectedSection.getCanvasCourseId());
        }

        final boolean isSimpleAttendance = courseConfigurationForm.getSimpleAttendance();

        ModelAndView page = isSimpleAttendance ?
                new ModelAndView("simpleStudentSummary") : new ModelAndView("studentSummary");

        List<AttendanceSummaryModel> summaryForSections = isSimpleAttendance ?
                reportService.getSimpleAttendanceSummaryReport(validatedSectionId) :
                reportService.getAviationAttendanceSummaryReport(validatedSectionId);

        student.getAttendances().sort(Comparator.comparing(Attendance::getDateOfClass).reversed());

        List<AttendanceSummaryModel.Entry> entries = new ArrayList<>();

        if (student.getSisUserId() != null) {
            entries = summaryForSections.stream()
                    .flatMap(model -> model.getEntries().stream())
                    .filter(entry -> student.getSisUserId().equals(entry.getSisUserId()))
                    .collect(Collectors.toList());
        }

        int totalPresentDays = 0, totalTardyDays  = 0, totalAbsentDays = 0, totalExcusedDays = 0;

        for (AttendanceSummaryModel.Entry entry : entries) {
            totalPresentDays += entry.getTotalClassesPresent();
            totalAbsentDays += entry.getTotalClassesMissed();
            totalTardyDays += entry.getTotalClassesTardy();
            totalExcusedDays += entry.getTotalClassesExcused();
        }

        int totalDays = totalPresentDays + totalTardyDays + totalAbsentDays + totalExcusedDays;
        
        addAssignmentSummaryToPage(page, totalPresentDays, totalTardyDays, totalAbsentDays, totalExcusedDays, totalDays, assignment);

        List<LtiLaunchData.InstitutionRole> institutionRoles = canvasService.getRoles();
        institutionRoles.stream()
                .filter(institutionRole -> institutionRole.equals(LtiLaunchData.InstitutionRole.Learner))
                .findFirst()
                .ifPresent(role -> page.addObject("isStudent", true));

        page.addObject("totalPresentDays", totalPresentDays);
        page.addObject("totalTardyDays", totalTardyDays);
        page.addObject("totalAbsentDays", totalAbsentDays);
        page.addObject("totalExcusedDays", totalExcusedDays);
        page.addObject("totalDays", totalDays);
        page.addObject("student", student);
        page.addObject("sectionId", sectionId);
        page.addObject("studentList", studentList);
        page.addObject("sectionList", sectionList);
        page.addObject("summaryForm", makeupForm);
        page.addObject("entryList", entries);
        return page;
    }

    private void addAssignmentSummaryToPage(ModelAndView page, int totalPresentDays, int totalTardyDays, int totalAbsentDays, int totalExcusedDays, int totalDays, AttendanceAssignment assignment) {

        if (assignment != null && totalDays != 0 && !StringUtils.isBlank(assignment.getPresentPoints())) {
            double presentWeight = Long.valueOf(assignment.getPresentPoints());
            double tardyWeight = Long.valueOf(assignment.getTardyPoints());
            double absentWeight = Long.valueOf(assignment.getAbsentPoints());
            double excusedWeight = Long.valueOf(assignment.getExcusedPoints());
            double assignmentPoints = Long.valueOf(assignment.getAssignmentPoints());

            double presentMultiplier = presentWeight / 100;
            double tardyMultiplier = tardyWeight / 100;
            double absentMultiplier = absentWeight / 100;
            double excusedMultiplier = excusedWeight / 100;

            double presentDaysTimesMultiplier = totalPresentDays * presentMultiplier;
            double tardyDaysTimesMultiplier = totalTardyDays * tardyMultiplier;
            double absentDaysTimesMultiplier = totalAbsentDays * absentMultiplier;
            double excusedDaysTimesMultiplier = totalExcusedDays * excusedMultiplier;

            double totalPresentPoints =  Math.round(presentDaysTimesMultiplier * assignmentPoints);
            double totalTardyPoints = Math.round(tardyDaysTimesMultiplier * assignmentPoints);
            double totalAbsentPoints = Math.round(absentDaysTimesMultiplier * assignmentPoints);
            double totalExcusedPoints = Math.round(excusedDaysTimesMultiplier * assignmentPoints);

            double sumStudentsPoints = totalPresentPoints + totalTardyPoints + totalAbsentPoints + totalExcusedPoints;

            double studentFinalGrade = Math.round(sumStudentsPoints / totalDays);

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
