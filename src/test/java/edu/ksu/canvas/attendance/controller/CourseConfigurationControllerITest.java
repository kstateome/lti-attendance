package edu.ksu.canvas.attendance.controller;

import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.enums.AttendanceType;
import edu.ksu.canvas.attendance.repository.AttendanceAssignmentRepository;
import edu.ksu.canvas.attendance.repository.AttendanceCourseRepository;
import edu.ksu.canvas.attendance.repository.AttendanceSectionRepository;
import edu.ksu.canvas.attendance.services.CanvasApiWrapperService;
import edu.ksu.canvas.attendance.services.SynchronizationService;
import edu.ksu.lti.launch.exception.NoLtiSessionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringJUnit4ClassRunner.class)
public class CourseConfigurationControllerITest extends BaseControllerITest {

    private AttendanceCourse existingCourse;
    private AttendanceSection existingSection;

    @Autowired
    private AttendanceCourseRepository courseRepository;
    
    @Autowired
    private AttendanceSectionRepository sectionRepository;

    @Autowired
    private AttendanceAssignmentRepository assignmentRepository;
    
    @Autowired
    private CanvasApiWrapperService canvasService;


    @Before
    public void additionalSetup() throws NoLtiSessionException {
        existingCourse = new AttendanceCourse();
        existingCourse.setCanvasCourseId(2000L);
        existingCourse.setDefaultMinutesPerSession(10);
        existingCourse.setTotalMinutes(SynchronizationService.DEFAULT_TOTAL_CLASS_MINUTES);
        existingCourse.setAttendanceType(AttendanceType.SIMPLE);
        existingCourse = courseRepository.save(existingCourse);
        
        existingSection = new AttendanceSection();
        existingSection.setCanvasCourseId(existingCourse.getCanvasCourseId());
        existingSection.setCanvasSectionId(1000L);
        existingSection = sectionRepository.save(existingSection);

        when(canvasService.getCourseId()).thenReturn(existingCourse.getCanvasCourseId().intValue());
    }


    @Test
    public void classSetup_nonExistentSectionId() throws Exception {
        Long nonExistentSectionId = 2000L;
        
        mockMvc.perform(get("/courseConfiguration/"+nonExistentSectionId))
            .andExpect(status().isOk())
            .andExpect(view().name("forward:roster"));
    }

    @Test
    public void classSetup_badNumberForSectionId() throws Exception {
        String badSectionId = "hackerDelight";
        
        mockMvc.perform(get("/courseConfiguration/"+badSectionId))
            .andExpect(status().isOk())
            .andExpect(view().name("forward:roster"));
    }

    @Test
    public void classSetup_existingSectionId_HappyPath() throws Exception {
        mockMvc.perform(get("/courseConfiguration/"+existingSection.getCanvasSectionId()))
            .andExpect(status().isOk())
            .andExpect(view().name("courseConfiguration"))
            .andExpect(model().attribute("selectedSectionId", is(existingSection.getCanvasSectionId())))
            .andExpect(model().attribute("updateSuccessful", is(false)))
            .andExpect(model().attribute("courseConfigurationForm", notNullValue()));
    }
    
    @Test
    public void saveCourseConfiguration_HappyPath() throws Exception {
        Long irrelevantSectionId = 3000L;
        Integer expectedDefaultMinutesPerSession = 100;
        Integer expectedTotalClassMinutes = 1000;
        Boolean expectedSimpleAttendanceValue = true;

        mockMvc.perform(post("/courseConfiguration/"+irrelevantSectionId+"/save")
                .param("saveCourseConfiguration", "Save Course Configuration")
                .param("defaultMinutesPerSession", String.valueOf(expectedDefaultMinutesPerSession))
                .param("totalClassMinutes", String.valueOf(expectedTotalClassMinutes))
                .param("simpleAttendance", String.valueOf(expectedSimpleAttendanceValue))
                .param("gradingOn", String.valueOf(false)))
                .andExpect(status().isOk())
                .andExpect(view().name("forward:/courseConfiguration/" + irrelevantSectionId + "?updateSuccessful=true"));
        
        AttendanceCourse course = courseRepository.findByCourseId(existingCourse.getCourseId());
        assertEquals(expectedDefaultMinutesPerSession, course.getDefaultMinutesPerSession());
        assertEquals(expectedTotalClassMinutes, course.getTotalMinutes());
        assertEquals(expectedSimpleAttendanceValue, course.getAttendanceType().equals(AttendanceType.SIMPLE));
    }

    @Test
    public void saveCourseConfiguration_PushingParameters_HappyPath() throws Exception {
        Long irrelevantSectionId = 3000L;
        Integer expectedDefaultMinutesPerSession = 100;
        Integer expectedTotalClassMinutes = 1000;
        Boolean expectedSimpleAttendanceValue = true;
        String expectedAssignmentName = "Assignment Name";
        String expectedAssignmentPoints = "120";
        String expectedPresentPoints = "100";
        String expectedTardyPoints = "0";
        String expectedExcusedPoints = "0";
        String expectedAbsentPoints = "0";

        mockMvc.perform(post("/courseConfiguration/"+irrelevantSectionId+"/save")
                .param("saveCourseConfiguration", "Save Course Configuration")
                .param("defaultMinutesPerSession", String.valueOf(expectedDefaultMinutesPerSession))
                .param("totalClassMinutes", String.valueOf(expectedTotalClassMinutes))
                .param("simpleAttendance", String.valueOf(expectedSimpleAttendanceValue))
                .param("gradingOn", String.valueOf(true))
                .param("assignmentName", expectedAssignmentName)
                .param("assignmentPoints", expectedAssignmentPoints)
                .param("presentPoints", expectedPresentPoints)
                .param("tardyPoints", expectedTardyPoints)
                .param("excusedPoints", expectedExcusedPoints)
                .param("absentPoints", expectedAbsentPoints) )
                .andExpect(status().isOk())
                .andExpect(view().name("forward:/courseConfiguration/" + irrelevantSectionId + "?updateSuccessful=true"));

        AttendanceCourse course = courseRepository.findByCourseId(existingCourse.getCourseId());
        assertEquals(expectedDefaultMinutesPerSession, course.getDefaultMinutesPerSession());
        assertEquals(expectedTotalClassMinutes, course.getTotalMinutes());
        assertEquals(expectedSimpleAttendanceValue, course.getAttendanceType().equals(AttendanceType.SIMPLE));

        AttendanceSection section = sectionRepository.findByCanvasSectionId(1000L);
        AttendanceAssignment attendanceAssignment = assignmentRepository.findByAttendanceSection(section);
        assertEquals(true, attendanceAssignment.getGradingOn());
        assertEquals(expectedAssignmentName, attendanceAssignment.getAssignmentName());
        assertEquals(expectedAssignmentPoints, attendanceAssignment.getAssignmentPoints());
        assertEquals(expectedPresentPoints, attendanceAssignment.getPresentPoints());
        assertEquals(expectedTardyPoints, attendanceAssignment.getTardyPoints());
        assertEquals(expectedExcusedPoints, attendanceAssignment.getExcusedPoints());
        assertEquals(expectedAbsentPoints, attendanceAssignment.getAbsentPoints());
    }

    @Test
    public void saveCourseConfiguration_MinuteBasedAttendance() throws Exception {
        Long irrelevantSectionId = 3000L;
        Integer expectedDefaultMinutesPerSession = 100;
        Integer expectedTotalClassMinutes = 1000;
        Boolean expectedSimpleAttendanceValue = true;

        mockMvc.perform(post("/courseConfiguration/" + irrelevantSectionId + "/save")
                .param("saveCourseConfiguration", "Save Course Configuration")
                .param("defaultMinutesPerSession", String.valueOf(expectedDefaultMinutesPerSession))
                .param("gradingOn", "false")
                .param("totalClassMinutes", String.valueOf(expectedTotalClassMinutes)))
                .andExpect(status().isOk())
                .andExpect(view().name("forward:/courseConfiguration/"+irrelevantSectionId+"?updateSuccessful=true"));

        AttendanceCourse course = courseRepository.findByCourseId(existingCourse.getCourseId());
        assertEquals(expectedDefaultMinutesPerSession, course.getDefaultMinutesPerSession());
        assertEquals(expectedTotalClassMinutes, course.getTotalMinutes());
        assertEquals(expectedSimpleAttendanceValue, course.getAttendanceType().equals(AttendanceType.MINUTES));
    }
    
    @Test
    public void saveCourseConfiguration_BadFormData() throws Exception {
        Long irrelevantSectionId = 3000L;
        Integer invalidDefaultMinutesPerSession = -1;
        Integer invalidTotalClassMinutes = -1;
        
        String postPageURL = "/courseConfiguration/"+irrelevantSectionId+"/save";
        mockMvc.perform(post(postPageURL)
                .param("saveCourseConfiguration", "Save Course Configuration")
                .param("defaultMinutesPerSession", String.valueOf(invalidDefaultMinutesPerSession))
                .param("gradingOn", "false")
                .param("totalClassMinutes", String.valueOf(invalidTotalClassMinutes)))
                .andExpect(status().isOk())
                .andExpect(view().name("/courseConfiguration"))
                .andExpect(model().attribute("selectedSectionId", is(String.valueOf(irrelevantSectionId))))
                .andExpect(model().attribute("error", notNullValue()));
    }
    
    @Test
    public void synchronizeWithCanvas_HappyPath() throws Exception {
        Long irrelevantSectionId = 3000L;
        
        mockMvc.perform(post("/courseConfiguration/"+irrelevantSectionId+"/save")
                .param("synchronizeWithCanvas", "Synchronize With Canvas"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("synchronizationSuccessful", is(true)))
                .andExpect(view().name("forward:/courseConfiguration/" + irrelevantSectionId));
    }

    @Test
    public void deleteAttendanceAssignment_HappyPath() throws Exception {
        Long irrelevantSectionId = 3000L;

        mockMvc.perform(post("/courseConfiguration/"+irrelevantSectionId+"/save")
                .param("deleteAssignment", "Yes"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("deleteSuccessful", is(true)))
                .andExpect(view().name("forward:/courseConfiguration/" + irrelevantSectionId));
    }

}
