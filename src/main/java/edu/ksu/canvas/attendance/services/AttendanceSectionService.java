package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.form.CourseConfigurationForm;
import edu.ksu.canvas.attendance.repository.AttendanceAssignmentRepository;
import edu.ksu.canvas.attendance.repository.AttendanceSectionRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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

    public List<AttendanceSection> getSectionByCanvasCourseId(long canvasCourseId) {
        return sectionRepository.findByCanvasCourseId(canvasCourseId);
    }

    public List<AttendanceSection> getSectionsByCourse(long canvasCourseId) {
        return sectionRepository.findByCanvasCourseId(canvasCourseId);
    }

    /**
     * @throws RuntimeException when courseForm or sections list are null or empty
     */
    public void save(CourseConfigurationForm courseForm, long canvasCourseId) {
        Validate.notNull(courseForm, "courseForm must not be null");

        List<AttendanceSection> sections = sectionRepository.findByCanvasCourseId(canvasCourseId);
        if(CollectionUtils.isEmpty(sections)) {
            RuntimeException e = new RuntimeException("Cannot load data into courseForm for non-existent sections for this course");
            throw new ContextedRuntimeException(e).addContextValue("courseId", canvasCourseId);
        }

        List<AttendanceAssignment> attendanceAssignments = new ArrayList<>();
        for(AttendanceSection section : sections) {
            AttendanceAssignment assignment = assignmentRepository.findByAttendanceSection(section);
            if(assignment == null) {
                assignment = new AttendanceAssignment();
                assignment.setAttendanceSection(section);
            }

            attendanceAssignments.add(assignment);
        }

        for(AttendanceAssignment assignment : attendanceAssignments) {
            assignment.setAssignmentName(courseForm.getAssignmentName());
            assignment.setGradingOn(courseForm.getGradingOn());
            assignment.setAssignmentPoints(courseForm.getAssignmentPoints());
            assignment.setPresentPoints(courseForm.getPresentPoints());
            assignment.setTardyPoints(courseForm.getTardyPoints());
            assignment.setExcusedPoints(courseForm.getExcusedPoints());
            assignment.setAbsentPoints(courseForm.getAbsentPoints());
            assignmentRepository.save(assignment);
        }
    }

    public void resetAttendanceAssignmentsForCourse(long canvasCourseId) {

        List<AttendanceSection> sections = sectionRepository.findByCanvasCourseId(canvasCourseId);
        if(CollectionUtils.isEmpty(sections)) {
            RuntimeException e = new RuntimeException("Cannot load data into courseForm for non-existent sections for this course");
            throw new ContextedRuntimeException(e).addContextValue("courseId", canvasCourseId);
        }

        List<AttendanceAssignment> attendanceAssignments = new ArrayList<>();
        for(AttendanceSection section : sections) {
            AttendanceAssignment assignment = assignmentRepository.findByAttendanceSection(section);
            if(assignment != null) {
                attendanceAssignments.add(assignment);
            }
        }

        for (AttendanceAssignment assignment: attendanceAssignments) {
            assignment.setGradingOn(false);
            assignment.setCanvasAssignmentId(null);
            assignment.setAssignmentName(null);
            assignment.setAssignmentPoints(null);
            assignment.setPresentPoints(null);
            assignment.setTardyPoints(null);
            assignment.setExcusedPoints(null);
            assignment.setAbsentPoints(null);
            assignmentRepository.save(assignment);
        }
    }


    /**
     * @throws RuntimeException if course does not exist or if the courseForm is null
     */
    public void loadIntoForm(CourseConfigurationForm courseForm, long courseId) {
        Validate.notNull(courseForm, "courseForm must not be null");

        List<AttendanceSection> sections = sectionRepository.findByCanvasCourseId(courseId);
        if(CollectionUtils.isEmpty(sections)){
            RuntimeException e = new RuntimeException("Cannot load data into courseForm for non-existent sections for this course");
            throw new ContextedRuntimeException(e).addContextValue("courseId", courseId);
        }

        AttendanceAssignment attendanceAssignment = assignmentRepository.findByAttendanceSection(sections.get(0));
        if(attendanceAssignment == null) {
            attendanceAssignment = new AttendanceAssignment();
        }

        courseForm.setAssignmentName(StringUtils.defaultIfEmpty(attendanceAssignment.getAssignmentName(), "Attendance"));
        courseForm.setAssignmentPoints(StringUtils.defaultIfEmpty(attendanceAssignment.getAssignmentPoints(), "100"));
        //default to full points for present or excused
        courseForm.setPresentPoints(StringUtils.defaultIfEmpty(attendanceAssignment.getPresentPoints(), courseForm.getAssignmentPoints()));
        courseForm.setExcusedPoints(StringUtils.defaultIfEmpty(attendanceAssignment.getExcusedPoints(), courseForm.getAssignmentPoints()));
        courseForm.setTardyPoints(StringUtils.defaultIfEmpty(attendanceAssignment.getTardyPoints(), "0"));
        courseForm.setAbsentPoints(StringUtils.defaultIfEmpty(attendanceAssignment.getAbsentPoints(), "0"));
        courseForm.setGradingOn(attendanceAssignment.getGradingOn());
    }

    public AttendanceSection getSectionInListById(Long canvasCourseId, Long sectionId) {
        List<AttendanceSection> sectionList = sectionRepository.findByCanvasCourseId(canvasCourseId);
        return sectionList.stream().filter(x -> x.getCanvasSectionId().equals(sectionId)).findFirst().orElse(null);

    }

}
