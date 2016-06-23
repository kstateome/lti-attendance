package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.attendance.enums.AttendanceType;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ksu.canvas.attendance.entity.AviationCourse;
import edu.ksu.canvas.attendance.form.CourseConfigurationForm;
import edu.ksu.canvas.attendance.repository.AviationCourseRepository;


@Component
public class AviationCourseService {

    @Autowired
    private AviationCourseRepository aviationCourseRepository;


    /**
     * @throws RuntimeException when courseForm is null
     */
    public void save(CourseConfigurationForm courseForm, long canvasCourseId) {
        Validate.notNull(courseForm, "courseForm must not be null");
        
        AviationCourse aviationCourse = aviationCourseRepository.findByCanvasCourseId(canvasCourseId);
        if (aviationCourse == null) {
            aviationCourse = new AviationCourse(canvasCourseId, courseForm.getTotalClassMinutes(), courseForm.getDefaultMinutesPerSession());
        } else {
            aviationCourse.setDefaultMinutesPerSession(courseForm.getDefaultMinutesPerSession());
            aviationCourse.setTotalMinutes(courseForm.getTotalClassMinutes());
            if (courseForm.getSimpleAttendance() != null && courseForm.getSimpleAttendance()) {
                aviationCourse.setAttendanceType(AttendanceType.SIMPLE);
            }
            else {
                aviationCourse.setAttendanceType(AttendanceType.MINUTES);
            }
        }

        aviationCourseRepository.save(aviationCourse);
    }


    /**
     * @throws RuntimeException if course does not exist or if the courseForm is null
     */
    public void loadIntoForm(CourseConfigurationForm courseForm, long courseId) {
        Validate.notNull(courseForm, "courseForm must not be null");
        
        AviationCourse aviationCourse = aviationCourseRepository.findByCanvasCourseId(courseId);
        
        if(aviationCourse == null) {
            RuntimeException e = new IllegalArgumentException("Cannot load data into courseForm for non-existant course");
            throw new ContextedRuntimeException(e).addContextValue("courseId", courseId);
        }

        courseForm.setTotalClassMinutes(aviationCourse.getTotalMinutes());
        courseForm.setDefaultMinutesPerSession(aviationCourse.getDefaultMinutesPerSession());
        courseForm.setSimpleAttendance(aviationCourse.getAttendanceType().equals(AttendanceType.SIMPLE));
    }

}
