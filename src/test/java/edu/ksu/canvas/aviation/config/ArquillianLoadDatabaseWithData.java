package edu.ksu.canvas.aviation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import edu.ksu.canvas.aviation.entity.AviationCourse;
import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.repository.AviationCourseRepository;
import edu.ksu.canvas.aviation.repository.AviationSectionRepository;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;
import edu.ksu.canvas.aviation.services.SynchronizationService;


@Component
@Profile("Arquillian")
public class ArquillianLoadDatabaseWithData implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private AviationCourseRepository courseRepository;
    
    @Autowired
    private AviationSectionRepository sectionRepository;
    
    @Autowired
    private AviationStudentRepository studentRepository;
    
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        AviationCourse existingCourse = new AviationCourse();
        existingCourse.setCanvasCourseId(ArquillianSpringMVCConfig.COURSE_ID_EXISTING);
        existingCourse.setDefaultMinutesPerSession(10);
        existingCourse.setTotalMinutes(SynchronizationService.DEFAULT_TOTAL_CLASS_MINUTES);
        existingCourse = courseRepository.save(existingCourse);
        
        AviationSection existingSection = new AviationSection();
        existingSection.setCanvasCourseId(existingCourse.getCanvasCourseId());
        existingSection.setCanvasSectionId(1000L);
        existingSection = sectionRepository.save(existingSection);
        
        AviationStudent existingStudent = new AviationStudent();
        existingStudent.setCanvasCourseId(existingCourse.getCanvasCourseId());
        existingStudent.setName("Zoglmann, Brian");
        existingStudent.setCanvasSectionId(existingSection.getCanvasSectionId());
        existingStudent.setSisUserId("SisId");
        existingStudent = studentRepository.save(existingStudent);
    }

}
