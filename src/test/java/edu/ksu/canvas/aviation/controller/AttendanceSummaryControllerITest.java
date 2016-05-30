package edu.ksu.canvas.aviation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ksu.canvas.aviation.config.TestDatabaseConfig;
import edu.ksu.canvas.aviation.config.TestSpringMVCConfig;
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
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.lti.LtiLaunch;
import edu.ksu.lti.model.LtiSession;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.text.SimpleDateFormat;

import javax.transaction.Transactional;

import static org.mockito.Mockito.*;


@Transactional
@ActiveProfiles("test")
@WebAppConfiguration
@ContextConfiguration(classes = {TestDatabaseConfig.class, TestSpringMVCConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class AttendanceSummaryControllerITest {

    
    private MockMvc mockMvc;
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private LtiLaunch mockLtiLaunch;
    
    private LtiSession mockLtiSession;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private AviationStudentRepository studentRepository;
    
    @Autowired
    private AviationCourseRepository courseRepository;
    
    @Autowired
    private AviationSectionRepository sectionRepository;
    
    
    @Before
    public void setUp() throws NoLtiSessionException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockLtiSession = mock(LtiSession.class);
        
        reset(mockLtiLaunch);
        when(mockLtiLaunch.getLtiSession()).thenReturn(mockLtiSession);
        when(mockLtiSession.getEid()).thenReturn("someEid");
    }
    
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
        existingStudent.setCanvasCourseId(existingCourse.getCanvasCourseId().intValue());
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
