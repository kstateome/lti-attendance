package edu.ksu.canvas.aviation.services;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.aviation.entity.AviationCourse;
import edu.ksu.canvas.aviation.repository.AviationCourseRepository;
import edu.ksu.canvas.aviation.repository.AviationSectionRepository;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;
import edu.ksu.lti.model.LtiSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class SynchronizationServiceUTest {

    private SynchronizationService synchronizationService;
    
    @Mock
    private AviationCourseRepository mockCourseRepository;
    
    @Mock
    private AviationStudentRepository mockStudentRepository;
    
    @Mock
    private AviationSectionRepository mockSectionRepository;
    
    @Mock
    private CanvasApiFactory mockCanvasApiFactory;
    
    @Mock
    private LtiSession mockLtiSession;
    
    
    @Before
    public void setup() {
        synchronizationService = new SynchronizationService();
        Whitebox.setInternalState(synchronizationService, mockCourseRepository);
        Whitebox.setInternalState(synchronizationService, mockStudentRepository);
        Whitebox.setInternalState(synchronizationService, mockSectionRepository);
        Whitebox.setInternalState(synchronizationService, mockCanvasApiFactory);
    }
    
    @Test(expected = NullPointerException.class)
    public void synchronizeWhenCourseNotExistsInDB_NullLtiSession() throws IOException {
        LtiSession nullLtiSession = null;
        long canvasCourseId = 500;
        
        synchronizationService.synchronize(nullLtiSession, canvasCourseId);
    }
    
    @Test
    public void synchronizeWhenCourseNotExistsInDB_CourseExists() throws IOException {
        long canvasCourseId = 500;
        AviationCourse existingCourse = new AviationCourse();
        
        SynchronizationService spy = spy(synchronizationService);
        when(mockCourseRepository.findByCanvasCourseId(canvasCourseId)).thenReturn(existingCourse);
        doThrow(new RuntimeException("This shouldn't happen... Test failed")).when(spy).synchronize(mockLtiSession, canvasCourseId);
        
        spy.synchronizeWhenCourseNotExistsInDB(mockLtiSession, canvasCourseId);
        //
    }
    
    @Test
    public void synchronizeWhenCourseNotExistsInDB_CourseDoesntExists() throws IOException {
        long canvasCourseId = 500;
        SynchronizationService neuteredSync = new SynchronizationService() {
            public void synchronize(LtiSession ltiSession, long canvasCourseId) throws IOException {
                //don't actually try to sync
            }
        };
        Whitebox.setInternalState(neuteredSync, mockCourseRepository);
        
        SynchronizationService spy = spy(neuteredSync);
        when(mockCourseRepository.findByCanvasCourseId(canvasCourseId)).thenReturn(null);
        spy.synchronizeWhenCourseNotExistsInDB(mockLtiSession, canvasCourseId);
        
        verify(spy, times(1)).synchronize(mockLtiSession, canvasCourseId);
    }
    
    @Test(expected = NullPointerException.class)
    public void synchronize_NullLtiSession() throws IOException {
        LtiSession nullLtiSession = null;
        long canvasCourseId = 500;
        
        synchronizationService.synchronize(nullLtiSession, canvasCourseId);
    }
    
    
    @Test
    public void synchronizeCourseFromCanvasToDb_NoExistingCourse() throws Exception {
        Long expectedCanvasCourseId = 500L;
        ArgumentCaptor<AviationCourse> capturedCourse = ArgumentCaptor.forClass(AviationCourse.class);
        AviationCourse expectedDbCourse = new AviationCourse();
        
        when(mockCourseRepository.save(any(AviationCourse.class))).thenReturn(expectedDbCourse);
        AviationCourse actualCourse = WhiteboxImpl.invokeMethod(synchronizationService, "synchronizeCourseFromCanvasToDb", expectedCanvasCourseId);
        
        verify(mockCourseRepository, atLeastOnce()).save(capturedCourse.capture());
        assertEquals(expectedCanvasCourseId, capturedCourse.getValue().getCanvasCourseId());
        assertEquals(Integer.valueOf(SynchronizationService.DEFAULT_MINUTES_PER_CLASS), capturedCourse.getValue().getDefaultMinutesPerSession());
        assertEquals(Integer.valueOf(SynchronizationService.DEFAULT_TOTAL_CLASS_MINUTES), capturedCourse.getValue().getTotalMinutes());
        assertEquals(expectedDbCourse, actualCourse);
    }
    
}
