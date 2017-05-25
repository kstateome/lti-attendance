package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import edu.ksu.canvas.attendance.enums.AttendanceType;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ksu.canvas.attendance.form.CourseConfigurationForm;
import edu.ksu.canvas.attendance.repository.AttendanceCourseRepository;


@Component
public class AttendanceCourseService {

    @Autowired
    private AttendanceCourseRepository attendanceCourseRepository;


    /**
     * @throws RuntimeException when courseForm is null
     */
    public void save(CourseConfigurationForm courseForm, long canvasCourseId) {
        Validate.notNull(courseForm, "courseForm must not be null");
        
        AttendanceCourse attendanceCourse = attendanceCourseRepository.findByCanvasCourseId(canvasCourseId);
        if (attendanceCourse == null) {
            attendanceCourse = new AttendanceCourse(canvasCourseId, courseForm.getTotalClassMinutes(), courseForm.getDefaultMinutesPerSession());
        } else {
            attendanceCourse.setShowNotesToStudents(courseForm.getShowNotesToStudents());
            attendanceCourse.setDefaultMinutesPerSession(courseForm.getDefaultMinutesPerSession());
            attendanceCourse.setTotalMinutes(courseForm.getTotalClassMinutes());
            if (courseForm.getSimpleAttendance() != null && courseForm.getSimpleAttendance()) {
                attendanceCourse.setAttendanceType(AttendanceType.SIMPLE);
            }
            else {
                attendanceCourse.setAttendanceType(AttendanceType.MINUTES);
            }
        }

        attendanceCourseRepository.save(attendanceCourse);
    }


    /**
     * @throws RuntimeException if course does not exist or if the courseForm is null
     */
    public void loadIntoForm(CourseConfigurationForm courseForm, long courseId) {
        Validate.notNull(courseForm, "courseForm must not be null");
        
        AttendanceCourse attendanceCourse = attendanceCourseRepository.findByCanvasCourseId(courseId);
        
        if(attendanceCourse == null) {
            RuntimeException e = new IllegalArgumentException("Cannot load data into courseForm for non-existent course");
            throw new ContextedRuntimeException(e).addContextValue("courseId", courseId);
        }

        courseForm.setTotalClassMinutes(attendanceCourse.getTotalMinutes());
        courseForm.setDefaultMinutesPerSession(attendanceCourse.getDefaultMinutesPerSession());
        courseForm.setSimpleAttendance(attendanceCourse.getAttendanceType().equals(AttendanceType.SIMPLE));
        courseForm.setShowNotesToStudents(attendanceCourse.getShowNotesToStudents());
    }

    public AttendanceCourse findByCanvasCourseId(Long courseId) {
        return attendanceCourseRepository.findByCanvasCourseId(courseId);
    }

}
