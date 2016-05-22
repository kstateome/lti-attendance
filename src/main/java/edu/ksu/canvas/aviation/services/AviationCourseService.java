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
    
    
    public void saveCourseMinutes(CourseConfigurationForm classSetupForm, String courseId) {

        Long canvasCourseId = Long.parseLong(courseId);
        AviationCourse aviationCourse = aviationCourseRepository.findByCanvasCourseId(canvasCourseId);
        if(aviationCourse == null){
            aviationCourse = new AviationCourse(canvasCourseId, classSetupForm.getTotalClassMinutes(), classSetupForm.getDefaultMinutesPerSession());
        }
        else{
            aviationCourse.setDefaultMinutesPerSession(classSetupForm.getDefaultMinutesPerSession());
            aviationCourse.setTotalMinutes(classSetupForm.getTotalClassMinutes());
        }

        aviationCourseRepository.save(aviationCourse);
    }
    
    
    public void loadCourseInfoIntoForm(CourseConfigurationForm courseConfigurationForm, Long courseId) {
        AviationCourse aviationCourse = aviationCourseRepository.findByCanvasCourseId(courseId);
        
        courseConfigurationForm.setTotalClassMinutes(aviationCourse.getTotalMinutes());
        courseConfigurationForm.setDefaultMinutesPerSession(aviationCourse.getDefaultMinutesPerSession());
    }
    
}
