package edu.ksu.canvas.aviation.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import edu.ksu.canvas.aviation.entity.AviationCourse;
import edu.ksu.canvas.aviation.form.CourseConfigurationForm;
import edu.ksu.canvas.aviation.repository.AviationCourseRepository;

import org.apache.commons.lang3.exception.ContextedRuntimeException;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class AviationCourseServiceUTest {

    private AviationCourseService courseService;
    
    @Mock
    private AviationCourseRepository mockCourseRepository;
    
    
    @Before
    public void setup() {
        courseService = new AviationCourseService();
        Whitebox.setInternalState(courseService, mockCourseRepository);
    }
    
    @Test
    public void loadIntoForm_HappyPath() {
        int expectedTotalMinutes = 500;
        int expectedDefaultMinutesPerSession = 45;
        long canvasCourseId = 1001;
        AviationCourse course = new AviationCourse();
        course.setTotalMinutes(expectedTotalMinutes);
        course.setDefaultMinutesPerSession(expectedDefaultMinutesPerSession);
        CourseConfigurationForm courseForm = new CourseConfigurationForm();
        
        when(mockCourseRepository.findByCanvasCourseId(canvasCourseId)).thenReturn(course);
        courseService.loadIntoForm(courseForm, canvasCourseId);
        
        assertEquals(expectedDefaultMinutesPerSession, courseForm.getDefaultMinutesPerSession());
        assertEquals(expectedTotalMinutes, courseForm.getTotalClassMinutes());
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
    public void save_NewCourseHasFormData() {
        long nonExistantCanvasCourseId = -1;
        Integer expectedTotalClassMinutes = 500;
        Integer expectedDefaultMinutesPerSession = 45;
        CourseConfigurationForm courseForm = new CourseConfigurationForm();
        courseForm.setDefaultMinutesPerSession(expectedDefaultMinutesPerSession);
        courseForm.setTotalClassMinutes(expectedTotalClassMinutes);
        ArgumentCaptor<AviationCourse> capturedAviationCourse = ArgumentCaptor.forClass(AviationCourse.class);
 
        when(mockCourseRepository.findByCanvasCourseId(nonExistantCanvasCourseId)).thenReturn(null);
        courseService.save(courseForm, nonExistantCanvasCourseId);
        
        verify(mockCourseRepository, atLeastOnce()).save(capturedAviationCourse.capture());
        assertEquals("expectedTotalClassMinutes", capturedAviationCourse.getValue().getTotalMinutes(), expectedTotalClassMinutes);
        assertEquals("expectedDefaultMinutesPerSession", capturedAviationCourse.getValue().getDefaultMinutesPerSession(), expectedDefaultMinutesPerSession);
    }
    
    @Test
    public void save_ExistingCourseHasFormData() {
        long nonExistantCanvasCourseId = -1;
        Integer expectedTotalClassMinutes = 500;
        Integer expectedDefaultMinutesPerSession = 45;
        CourseConfigurationForm courseForm = new CourseConfigurationForm();
        courseForm.setDefaultMinutesPerSession(expectedDefaultMinutesPerSession);
        courseForm.setTotalClassMinutes(expectedTotalClassMinutes);
        AviationCourse existingCourse = new AviationCourse();
 
        when(mockCourseRepository.findByCanvasCourseId(nonExistantCanvasCourseId)).thenReturn(existingCourse);
        courseService.save(courseForm, nonExistantCanvasCourseId);
        
        verify(mockCourseRepository, atLeastOnce()).save(existingCourse);
        assertEquals("expectedTotalClassMinutes", existingCourse.getTotalMinutes(), expectedTotalClassMinutes);
        assertEquals("expectedDefaultMinutesPerSession", existingCourse.getDefaultMinutesPerSession(), expectedDefaultMinutesPerSession);
    }
    
    @Test(expected = NullPointerException.class)
    public void save_NullForm() {
        CourseConfigurationForm courseForm = null;
        long irrlevantCanvasCourseId = -1;
        
        courseService.save(courseForm, irrlevantCanvasCourseId);
    }
    
}
