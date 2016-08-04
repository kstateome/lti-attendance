package edu.ksu.canvas.attendance.controller;

import edu.ksu.canvas.attendance.entity.Attendance;
import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import edu.ksu.canvas.attendance.enums.AttendanceType;
import edu.ksu.canvas.attendance.enums.Status;
import edu.ksu.canvas.attendance.repository.AttendanceRepository;
import edu.ksu.canvas.attendance.repository.AttendanceCourseRepository;
import edu.ksu.canvas.attendance.repository.AttendanceSectionRepository;
import edu.ksu.canvas.attendance.repository.AttendanceStudentRepository;
import edu.ksu.canvas.attendance.services.SynchronizationService;
import edu.ksu.canvas.interfaces.CourseReader;
import edu.ksu.canvas.model.Course;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringJUnit4ClassRunner.class)
public class AttendanceSummaryControllerITest extends BaseControllerITest {

    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private AttendanceStudentRepository studentRepository;
    
    @Autowired
    private AttendanceCourseRepository courseRepository;
    
    @Autowired
    private AttendanceSectionRepository sectionRepository;
    
    
    @Test
    @SuppressWarnings("unchecked")
    public void attendanceSummary_nonExistantSectionId() throws Exception {
        Long nonExistantSectionId = 2000L;

        CourseReader mockCourseReader = mock(CourseReader.class);
        when(mockCourseReader.getSingleCourse(anyString(), anyList())).thenReturn(Optional.<Course>empty());

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
        
        AttendanceCourse existingCourse = new AttendanceCourse();
        existingCourse.setAttendanceType(AttendanceType.MINUTES);
        existingCourse.setCanvasCourseId(4000L);
        existingCourse.setDefaultMinutesPerSession(10);
        existingCourse.setTotalMinutes(SynchronizationService.DEFAULT_TOTAL_CLASS_MINUTES);
        existingCourse = courseRepository.save(existingCourse);
        
        AttendanceSection existingSection = new AttendanceSection();
        existingSection.setCanvasCourseId(existingCourse.getCanvasCourseId());
        existingSection.setCanvasSectionId(existingSectionId);
        existingSection = sectionRepository.save(existingSection);
        
        AttendanceStudent existingStudent = new AttendanceStudent();
        existingStudent.setSisUserId("1001");
        existingStudent.setCanvasCourseId(existingCourse.getCanvasCourseId());
        existingStudent.setCanvasSectionId(existingSectionId);
        existingStudent.setName("Zoglmann, Kurt");
        existingStudent.setDeleted(false);
        existingStudent = studentRepository.save(existingStudent);
        
        Attendance existingAttendance = new Attendance();
        existingAttendance.setAttendanceStudent(existingStudent);
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

    @Test
    public void SimpleAttendanceSummary_existingSectionId_HappyPath() throws Exception {
        Long existingSectionId = 2000L;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        AttendanceCourse existingCourse = new AttendanceCourse();
        existingCourse.setAttendanceType(AttendanceType.SIMPLE);
        existingCourse.setCanvasCourseId(4000L);
        existingCourse.setDefaultMinutesPerSession(10);
        existingCourse.setTotalMinutes(SynchronizationService.DEFAULT_TOTAL_CLASS_MINUTES);
        existingCourse = courseRepository.save(existingCourse);

        AttendanceSection existingSection = new AttendanceSection();
        existingSection.setCanvasCourseId(existingCourse.getCanvasCourseId());
        existingSection.setCanvasSectionId(existingSectionId);
        existingSection = sectionRepository.save(existingSection);

        AttendanceStudent existingStudent = new AttendanceStudent();
        existingStudent.setSisUserId("1001");
        existingStudent.setCanvasCourseId(existingCourse.getCanvasCourseId());
        existingStudent.setCanvasSectionId(existingSectionId);
        existingStudent.setName("Zoglmann, Kurt");
        existingStudent.setDeleted(false);
        existingStudent = studentRepository.save(existingStudent);

        Attendance existingAttendance = new Attendance();
        existingAttendance.setAttendanceStudent(existingStudent);
        existingAttendance.setDateOfClass(sdf.parse("5/21/2016"));
        existingAttendance.setMinutesMissed(5);
        existingAttendance.setStatus(Status.TARDY);
        existingAttendance = attendanceRepository.save(existingAttendance);


        mockMvc.perform(get("/attendanceSummary/"+existingSectionId))
                .andExpect(status().isOk())
                .andExpect(view().name("simpleAttendanceSummary"))
                .andExpect(model().attribute("selectedSectionId", is(existingSectionId)))
                .andExpect(model().attribute("attendanceSummaryForSections", hasSize(1)))
                .andExpect(model().attribute("attendanceSummaryForSections", hasItem(hasProperty("sectionId", is(existingSectionId)))))
                .andExpect(model().attribute("sectionList", hasSize(1)))
                .andExpect(model().attribute("sectionList", hasItem(hasProperty("canvasSectionId", is(existingSectionId)))));
    }
    
}
