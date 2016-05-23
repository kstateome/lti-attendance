package edu.ksu.canvas.aviation.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ksu.canvas.aviation.entity.AviationCourse;
import edu.ksu.canvas.aviation.form.CourseConfigurationForm;
import edu.ksu.canvas.aviation.repository.AviationCourseRepository;


@Component
public class AviationCourseService {

    @Autowired
    private AviationCourseRepository aviationCourseRepository;


    public void save(CourseConfigurationForm couseForm, String courseId) {

        Long canvasCourseId = Long.parseLong(courseId);
        AviationCourse aviationCourse = aviationCourseRepository.findByCanvasCourseId(canvasCourseId);
        if (aviationCourse == null) {
            aviationCourse = new AviationCourse(canvasCourseId, couseForm.getTotalClassMinutes(), couseForm.getDefaultMinutesPerSession());
        } else {
            aviationCourse.setDefaultMinutesPerSession(couseForm.getDefaultMinutesPerSession());
            aviationCourse.setTotalMinutes(couseForm.getTotalClassMinutes());
        }

        aviationCourseRepository.save(aviationCourse);
    }


    public void loadIntoForm(CourseConfigurationForm courseForm, Long courseId) {
        AviationCourse aviationCourse = aviationCourseRepository.findByCanvasCourseId(courseId);

        courseForm.setTotalClassMinutes(aviationCourse.getTotalMinutes());
        courseForm.setDefaultMinutesPerSession(aviationCourse.getDefaultMinutesPerSession());
    }

}
