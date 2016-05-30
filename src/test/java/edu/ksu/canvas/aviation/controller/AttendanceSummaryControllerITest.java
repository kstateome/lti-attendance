package edu.ksu.canvas.aviation.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.ksu.canvas.aviation.entity.Attendance;
import edu.ksu.canvas.aviation.entity.AviationCourse;
import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.enums.Status;
import edu.ksu.canvas.aviation.repository.AttendanceRepository;
import edu.ksu.canvas.aviation.repository.AviationCourseRepository;
import edu.ksu.canvas.aviation.repository.AviationSectionRepository;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;
import edu.ksu.canvas.aviation.services.SynchronizationService;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.text.SimpleDateFormat;


@RunWith(SpringJUnit4ClassRunner.class)
public class AttendanceSummaryControllerITest extends BaseControllerITest {

    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private AviationStudentRepository studentRepository;
    
    @Autowired
    private AviationCourseRepository courseRepository;
    
    @Autowired
    private AviationSectionRepository sectionRepository;
    
    
    
    @Test
    public void attendanceSummary_nonExistantSectionId() throws Exception {
        Long nonExistantSectionId = 2000L;
        mockMvc.perform(get("/attendanceSummary/"+nonExistantSectionId))
            .andExpect(status().isOk())
            .andExpect(view().name("attendanceSummary"))
            .andExpect(model().attribute("selectedSectionId", is(nonExistantSectionId)))
            .andExpect(model().attribute("attendanceSummaryForSections", empty()))
            .andExpect(model().attribute("sectionList", empty()));
    }
    
    @Test
    public void attendanceSummary_badNumberForSectionId() throws Exception {
        String badSectionId = "hackerDelight";
        mockMvc.perform(get("/attendanceSummary/"+badSectionId))
            .andExpect(status().isOk())
            .andExpect(view().name("forward:roster"));
    }
    
    
    @Test
    public void attendanceSummary_existingSectionId_HappyPath() throws Exception {
        Long existingSectionId = 2000L;
        
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        
        AviationCourse existingCourse = new AviationCourse();
        existingCourse.setCanvasCourseId(2000L);
        existingCourse.setDefaultMinutesPerSession(10);
        existingCourse.setTotalMinutes(SynchronizationService.DEFAULT_TOTAL_CLASS_MINUTES);
        existingCourse = courseRepository.save(existingCourse);
        
        AviationSection existingSection = new AviationSection();
        existingSection.setCanvasCourseId(existingCourse.getCanvasCourseId());
        existingSection.setCanvasSectionId(existingSectionId);
        existingSection = sectionRepository.save(existingSection);
        
        AviationStudent existingStudent = new AviationStudent();
        existingStudent.setSisUserId("1001");
        existingStudent.setCanvasCourseId(existingCourse.getCanvasCourseId());
        existingStudent.setSectionId(existingSectionId);
        existingStudent.setName("Zoglmann, Kurt");
        existingStudent = studentRepository.save(existingStudent);
        
        Attendance existingAttendance = new Attendance();
        existingAttendance.setAviationStudent(existingStudent);
        existingAttendance.setDateOfClass(sdf.parse("5/21/2016"));
        existingAttendance.setMinutesMissed(5);
        existingAttendance.setStatus(Status.TARDY);
        existingAttendance = attendanceRepository.save(existingAttendance);
        
        
        mockMvc.perform(get("/attendanceSummary/"+existingSectionId))
        .andExpect(status().isOk())
        .andExpect(view().name("attendanceSummary"))
        .andExpect(model().attribute("selectedSectionId", is(existingSectionId)))
        .andExpect(model().attribute("attendanceSummaryForSections", hasSize(1)))
        .andExpect(model().attribute("attendanceSummaryForSections", hasItem(hasProperty("sectionId", is(existingSectionId)))))
        .andExpect(model().attribute("sectionList", hasSize(1)))
        .andExpect(model().attribute("sectionList", hasItem(hasProperty("canvasSectionId", is(existingSectionId)))));
    }
    
}
