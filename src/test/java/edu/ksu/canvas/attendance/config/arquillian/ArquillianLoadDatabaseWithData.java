package edu.ksu.canvas.attendance.config.arquillian;

import edu.ksu.canvas.attendance.entity.Attendance;
import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import edu.ksu.canvas.attendance.entity.AviationSection;
import edu.ksu.canvas.attendance.entity.AviationStudent;
import edu.ksu.canvas.attendance.enums.Status;
import edu.ksu.canvas.attendance.repository.AttendanceRepository;
import edu.ksu.canvas.attendance.repository.AviationCourseRepository;
import edu.ksu.canvas.attendance.repository.AviationSectionRepository;
import edu.ksu.canvas.attendance.repository.AviationStudentRepository;
import edu.ksu.canvas.attendance.services.SynchronizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;


@Component
@Profile("Arquillian")
public class ArquillianLoadDatabaseWithData implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private AviationCourseRepository courseRepository;
    
    @Autowired
    private AviationSectionRepository sectionRepository;
    
    @Autowired
    private AviationStudentRepository studentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;
    
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        AttendanceCourse existingCourse = new AttendanceCourse();
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
        existingStudent.setDeleted(false);
        existingStudent = studentRepository.save(existingStudent);

        Attendance existingAttendance = new Attendance();
        existingAttendance.setAviationStudent(studentRepository.findByStudentId(existingStudent.getStudentId()));
        existingAttendance.setStatus(Status.PRESENT);
        existingAttendance.setDateOfClass(new Date());
        existingStudent.setAttendances(Collections.singletonList(existingAttendance));
        studentRepository.save(existingStudent);
    }

}
