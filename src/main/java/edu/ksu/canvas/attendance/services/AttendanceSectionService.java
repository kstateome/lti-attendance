package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.form.CourseConfigurationForm;
import edu.ksu.canvas.attendance.repository.AttendanceAssignmentRepository;
import edu.ksu.canvas.attendance.repository.AttendanceSectionRepository;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class AttendanceSectionService {

    @Autowired
    private AttendanceSectionRepository sectionRepository;

    @Autowired
    private AttendanceAssignmentRepository assignmentRepository;



    public AttendanceSection getSection(long canvasSectionId) {
        return sectionRepository.findByCanvasSectionId(canvasSectionId);
    }

    public AttendanceSection getFirstSectionOfCourse(long canvasCourseId) {
        List<AttendanceSection> sections = sectionRepository.findByCanvasCourseId(canvasCourseId);
        return sections.isEmpty() ? null : sections.get(0);
    }


    public List<AttendanceSection> getSectionsByCourse(long canvasCourseId) {
        return sectionRepository.findByCanvasCourseId(canvasCourseId);
    }

    /**
     * @throws RuntimeException when courseForm is null
     */
    public void save(CourseConfigurationForm courseForm, long canvasCourseId) {
        Validate.notNull(courseForm, "courseForm must not be null");

        List<AttendanceSection> sections = sectionRepository.findByCanvasCourseId(canvasCourseId);
        if(sections == null || sections.isEmpty()) {
            RuntimeException e = new IllegalArgumentException("Cannot load data into courseForm for non-existant course");
            throw new ContextedRuntimeException(e).addContextValue("courseId", canvasCourseId);
        }

        List<AttendanceAssignment> attendanceAssignments = new ArrayList<>();
        for(AttendanceSection section : sections) {
            AttendanceAssignment assignment = assignmentRepository.findBySectionId(section.getSectionId());
            if(assignment == null) {
                assignment = new AttendanceAssignment();
                assignment.setSectionId(section.getSectionId());
            }
            attendanceAssignments.add(assignment);
        }

        for(AttendanceAssignment assignment : attendanceAssignments) {
            assignment.setAssignmentName(courseForm.getAssignmentName());
            assignment.setGradingOn(courseForm.getGradingOn());
            assignment.setPresentPoints(courseForm.getPresentPoints());
            assignment.setTardyPoints(courseForm.getTardyPoints());
            assignment.setExcusedPoints(courseForm.getExcusedPoints());
            assignment.setAbsentPoints(courseForm.getAbsentPoints());
            assignmentRepository.save(assignment);
        }
    }


    /**
     * @throws RuntimeException if course does not exist or if the courseForm is null
     */
    public void loadIntoForm(CourseConfigurationForm courseForm, long courseId) {
        Validate.notNull(courseForm, "courseForm must not be null");

        List<AttendanceSection> sections = sectionRepository.findByCanvasCourseId(courseId);

        if(sections == null || sections.isEmpty()) {
            RuntimeException e = new IllegalArgumentException("Cannot load data into courseForm for non-existant course");
            throw new ContextedRuntimeException(e).addContextValue("courseId", courseId);
        }

        AttendanceAssignment attendanceAssignment = assignmentRepository.findBySectionId(sections.get(0).getSectionId());
        if(attendanceAssignment == null) {
            attendanceAssignment = new AttendanceAssignment();
        }

        courseForm.setAssignmentPoints(attendanceAssignment.getAssignmentPoints());
        courseForm.setPresentPoints(attendanceAssignment.getPresentPoints());
        courseForm.setExcusedPoints(attendanceAssignment.getExcusedPoints());
        courseForm.setAbsentPoints(attendanceAssignment.getAbsentPoints());
        courseForm.setGradingOn(attendanceAssignment.getGradingOn());
        courseForm.setAssignmentName(attendanceAssignment.getAssignmentName());
    }


}
