package edu.ksu.canvas.attendance.controller;

import edu.ksu.canvas.attendance.entity.Attendance;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import edu.ksu.canvas.attendance.exception.MissingSisIdException;
import edu.ksu.canvas.attendance.form.CourseConfigurationForm;
import edu.ksu.canvas.attendance.form.MakeupForm;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import edu.ksu.canvas.attendance.util.DropDownOrganizer;
import java.util.ArrayList;


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
    private AttendanceCourseService courseService;


    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    @RequestMapping(value = "/{sectionId}/{studentId}", method = RequestMethod.GET)
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
        student.getAttendances().sort(Comparator.comparing(Attendance::getDateOfClass).reversed());

        MakeupForm makeupForm = makeupService.createMakeupForm(validatedStudentId, validatedSectionId, addEmptyEntry);

        //Checking if Attendance Summary is Simple or Aviation
        CourseConfigurationForm courseConfigurationForm = new CourseConfigurationForm();
        long selectedCourseId = student.getCanvasCourseId();

        courseService.loadIntoForm(courseConfigurationForm, selectedCourseId);
        List<AttendanceSection> sectionList = sectionService.getSectionByCanvasCourseId(selectedCourseId);
        courseConfigurationForm.setAllSections(sectionList);
        final boolean isSimpleAttendance = courseConfigurationForm.getSimpleAttendance();

        ModelAndView page = isSimpleAttendance ?
                new ModelAndView("simpleStudentSummary") : new ModelAndView("studentSummary");

        List<AttendanceStudent> studentAttendanceList = studentService.getStudentByCourseAndSisId(student.getSisUserId(), selectedCourseId);
        List<LtiLaunchData.InstitutionRole> institutionRoles = canvasService.getRoles();
        institutionRoles.stream()
                .filter(institutionRole -> institutionRole.equals(LtiLaunchData.InstitutionRole.Learner))
                .findFirst()
                .ifPresent(role -> page.addObject("isStudent", true));

        int totalTardy = 0, totalExcused = 0, totalMissed = 0;

        for (AttendanceStudent attendanceStudent: studentAttendanceList) {
            for (Attendance attendance: attendanceStudent.getAttendances()) {
                switch (attendance.getStatus()) {
                    case TARDY:
                        totalTardy++;
                        break;
                    case EXCUSED:
                        totalExcused++;
                        break;
                    case ABSENT:
                        totalMissed++;
                        break;
                    case PRESENT:
                        break;
                }
            }
        }
        page.addObject("sectionId", sectionId);
        page.addObject("student", student);
        page.addObject("summaryForm", makeupForm);
        page.addObject("totalTardy", totalTardy);
        page.addObject("totalExcused", totalExcused);
        page.addObject("totalMissed", totalMissed);
        page.addObject("sectionList", sectionList);
        page.addObject("studentList", studentAttendanceList);
        page.addObject("dropDownList", DropDownOrganizer.sortWithSelectedSectionFirst(sectionList, sectionId));

        return page;
    }

}
