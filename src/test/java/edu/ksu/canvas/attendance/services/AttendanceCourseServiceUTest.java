package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import edu.ksu.canvas.attendance.enums.AttendanceType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.ksu.canvas.attendance.form.CourseConfigurationForm;
import edu.ksu.canvas.attendance.repository.AttendanceCourseRepository;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class AttendanceCourseServiceUTest {

    private AttendanceCourseService courseService;
    
    @Mock
    private AttendanceCourseRepository mockCourseRepository;
    
    
    @Before
    public void setup() {
        courseService = new AttendanceCourseService();
        ReflectionTestUtils.setField(courseService, "attendanceCourseRepository", mockCourseRepository);
    }
    
    @Test
    public void loadIntoForm_HappyPath() {
        int expectedTotalMinutes = 500;
        int expectedDefaultMinutesPerSession = 45;
        long canvasCourseId = 1001;
        AttendanceCourse course = new AttendanceCourse();
        course.setTotalMinutes(expectedTotalMinutes);
        course.setDefaultMinutesPerSession(expectedDefaultMinutesPerSession);
        CourseConfigurationForm courseForm = new CourseConfigurationForm();

        when(mockCourseRepository.findByCanvasCourseId(canvasCourseId)).thenReturn(course);
        courseService.loadIntoForm(courseForm, canvasCourseId);
        
        assertEquals(expectedDefaultMinutesPerSession, courseForm.getDefaultMinutesPerSession());
        assertEquals(expectedTotalMinutes, courseForm.getTotalClassMinutes());
        assertTrue(courseForm.getSimpleAttendance());
    }
    
    @Test(expected = ContextedRuntimeException.class)
    public void loadIntoForm_CourseDoesntExist() {
        long nonExistantCanvasCourseId = -1;
        CourseConfigurationForm courseForm = new CourseConfigurationForm();
        
        when(mockCourseRepository.findByCanvasCourseId(nonExistantCanvasCourseId)).thenReturn(null);
        courseService.loadIntoForm(courseForm, nonExistantCanvasCourseId);
    }
    
    @Test(expected = NullPointerException.class)
    public void loadIntoForm_NullForm() {
        CourseConfigurationForm courseForm = null;
        long irrlevantCanvasCourseId = -1;
        
        courseService.loadIntoForm(courseForm, irrlevantCanvasCourseId);
    }
    
    @Test
    public void save_NewCourse() {
        long nonExistantCanvasCourseId = -1;
        Integer expectedTotalClassMinutes = 500;
        Integer expectedDefaultMinutesPerSession = 45;
        AttendanceType expectedDefaultType = AttendanceType.SIMPLE;
        CourseConfigurationForm courseForm = new CourseConfigurationForm();
        courseForm.setDefaultMinutesPerSession(expectedDefaultMinutesPerSession);
        courseForm.setTotalClassMinutes(expectedTotalClassMinutes);
        ArgumentCaptor<AttendanceCourse> capturedAttendanceCourse = ArgumentCaptor.forClass(AttendanceCourse.class);
 
        when(mockCourseRepository.findByCanvasCourseId(nonExistantCanvasCourseId)).thenReturn(null);
        courseService.save(courseForm, nonExistantCanvasCourseId);
        
        verify(mockCourseRepository, atLeastOnce()).save(capturedAttendanceCourse.capture());
        assertEquals("expectedTotalClassMinutes", capturedAttendanceCourse.getValue().getTotalMinutes(), expectedTotalClassMinutes);
        assertEquals("expectedDefaultMinutesPerSession", capturedAttendanceCourse.getValue().getDefaultMinutesPerSession(), expectedDefaultMinutesPerSession);
        assertEquals("expectedDefaultAttendanceType", capturedAttendanceCourse.getValue().getAttendanceType(), expectedDefaultType);
    }
    
    @Test
    public void save_ExistingCourseMinutesAttendance() {
        long nonExistantCanvasCourseId = -1;
        Integer expectedTotalClassMinutes = 500;
        Integer expectedDefaultMinutesPerSession = 45;
        AttendanceType expectedAttendanceType = AttendanceType.MINUTES;
        CourseConfigurationForm courseForm = new CourseConfigurationForm();
        courseForm.setDefaultMinutesPerSession(expectedDefaultMinutesPerSession);
        courseForm.setTotalClassMinutes(expectedTotalClassMinutes);
        AttendanceCourse existingCourse = new AttendanceCourse();
 
        when(mockCourseRepository.findByCanvasCourseId(nonExistantCanvasCourseId)).thenReturn(existingCourse);
        courseService.save(courseForm, nonExistantCanvasCourseId);
        
        verify(mockCourseRepository, atLeastOnce()).save(existingCourse);
        assertEquals("expectedTotalClassMinutes", expectedTotalClassMinutes, existingCourse.getTotalMinutes());
        assertEquals("expectedDefaultMinutesPerSession", expectedDefaultMinutesPerSession, existingCourse.getDefaultMinutesPerSession());
        assertEquals("expectedAttendanceType", expectedAttendanceType, existingCourse.getAttendanceType());

    }

    @Test
    public void save_ExistingCourseSimpleAttendance() {
        long nonExistantCanvasCourseId = -1;
        Integer expectedTotalClassMinutes = 500;
        Integer expectedDefaultMinutesPerSession = 45;
        AttendanceType expectedAttendanceType = AttendanceType.SIMPLE;
        CourseConfigurationForm courseForm = new CourseConfigurationForm();
        courseForm.setDefaultMinutesPerSession(expectedDefaultMinutesPerSession);
        courseForm.setTotalClassMinutes(expectedTotalClassMinutes);
        courseForm.setSimpleAttendance(true);
        AttendanceCourse existingCourse = new AttendanceCourse();

        when(mockCourseRepository.findByCanvasCourseId(nonExistantCanvasCourseId)).thenReturn(existingCourse);
        courseService.save(courseForm, nonExistantCanvasCourseId);

        verify(mockCourseRepository, atLeastOnce()).save(existingCourse);
        assertEquals("expectedTotalClassMinutes", expectedTotalClassMinutes, existingCourse.getTotalMinutes());
        assertEquals("expectedDefaultMinutesPerSession", expectedDefaultMinutesPerSession, existingCourse.getDefaultMinutesPerSession());
        assertEquals("expectedAttendanceType", expectedAttendanceType, existingCourse.getAttendanceType());

    }
    
    @Test(expected = NullPointerException.class)
    public void save_NullForm() {
        CourseConfigurationForm courseForm = null;
        long irrlevantCanvasCourseId = -1;
        
        courseService.save(courseForm, irrlevantCanvasCourseId);
    }
    
}
