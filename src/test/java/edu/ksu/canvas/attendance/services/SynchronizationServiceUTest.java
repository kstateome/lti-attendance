package edu.ksu.canvas.attendance.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.ksu.canvas.attendance.entity.AviationSection;
import edu.ksu.canvas.attendance.entity.AviationStudent;
import edu.ksu.canvas.attendance.repository.AviationCourseRepository;
import edu.ksu.canvas.attendance.repository.AviationSectionRepository;
import edu.ksu.canvas.attendance.repository.AviationStudentRepository;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Section;
import edu.ksu.canvas.model.User;
import edu.ksu.lti.model.LtiSession;
import org.hamcrest.Matchers;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

import static org.powermock.api.mockito.PowerMockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.anyLong;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;


@RunWith(PowerMockRunner.class)
@PrepareForTest(SynchronizationService.class)
public class SynchronizationServiceUTest {

    private SynchronizationService synchronizationService;

    @Mock
    private AviationCourseRepository mockCourseRepository;

    @Mock
    private AviationStudentRepository mockStudentRepository;

    @Mock
    private AviationSectionRepository mockSectionRepository;

    @Mock
    private CanvasApiWrapperService mockCanvasService;


    @Before
    public void setup() {
        synchronizationService = new SynchronizationService();
        Whitebox.setInternalState(synchronizationService, mockCourseRepository);
        Whitebox.setInternalState(synchronizationService, mockStudentRepository);
        Whitebox.setInternalState(synchronizationService, mockSectionRepository);
        Whitebox.setInternalState(synchronizationService, mockCanvasService);
    }

    @Test
    public void synchronizeWhenCourseNotExistsInDB_CourseExists() throws IOException, NoLtiSessionException {
        long canvasCourseId = 500;
        AttendanceCourse existingCourse = new AttendanceCourse();

        SynchronizationService spy = spy(synchronizationService);
        when(mockCourseRepository.findByCanvasCourseId(canvasCourseId)).thenReturn(existingCourse);
        doThrow(new RuntimeException("This shouldn't happen... Test failed")).when(spy).synchronize(canvasCourseId);

        spy.synchronizeWhenCourseNotExistsInDB(canvasCourseId);
        //exception is thrown when tests fails
    }

    @Test
    public void synchronizeWhenCourseNotExistsInDB_CourseDoesntExists() throws IOException, NoLtiSessionException {
        long canvasCourseId = 500;
        SynchronizationService neuteredSync = new SynchronizationService() {

            @SuppressWarnings("unused")
            public void synchronize(LtiSession ltiSession, long canvasCourseId) throws IOException {
                //don't actually try to sync
            }
        };
        Whitebox.setInternalState(neuteredSync, mockCourseRepository);
        Whitebox.setInternalState(neuteredSync, mockCanvasService);

        SynchronizationService spy = spy(neuteredSync);
        when(mockCourseRepository.findByCanvasCourseId(canvasCourseId)).thenReturn(null);
        spy.synchronizeWhenCourseNotExistsInDB(canvasCourseId);

        verify(spy, times(1)).synchronize(canvasCourseId);
    }

    @Test
    public void synchronize_HappyPatchCallsInternalSynchornizationMethods() throws Exception {
        long canvasCourseId = 300L;
        SynchronizationService spy = spy(synchronizationService);

        spy.synchronize(canvasCourseId);

        verifyPrivate(spy, times(1)).invoke("synchronizeCourseFromCanvasToDb", canvasCourseId);
        verifyPrivate(spy, times(1)).invoke("synchronizeSectionsFromCanvasToDb", any(List.class));
        verifyPrivate(spy, times(1)).invoke("synchronizeStudentsFromCanvasToDb", any(Map.class));
    }

    @Test
    public void synchronizeCourseFromCanvasToDb_NoExistingCourse() throws Exception {
        Long expectedCanvasCourseId = 500L;
        ArgumentCaptor<AttendanceCourse> capturedCourse = ArgumentCaptor.forClass(AttendanceCourse.class);
        AttendanceCourse expectedDbCourse = new AttendanceCourse();

        when(mockCourseRepository.save(any(AttendanceCourse.class))).thenReturn(expectedDbCourse);
        AttendanceCourse actualCourse = WhiteboxImpl.invokeMethod(synchronizationService, "synchronizeCourseFromCanvasToDb", expectedCanvasCourseId);

        verify(mockCourseRepository, atLeastOnce()).save(capturedCourse.capture());
        assertEquals(expectedCanvasCourseId, capturedCourse.getValue().getCanvasCourseId());
        assertEquals(Integer.valueOf(SynchronizationService.DEFAULT_MINUTES_PER_CLASS), capturedCourse.getValue().getDefaultMinutesPerSession());
        assertEquals(Integer.valueOf(SynchronizationService.DEFAULT_TOTAL_CLASS_MINUTES), capturedCourse.getValue().getTotalMinutes());
        assertEquals(expectedDbCourse, actualCourse);
    }
    
    @Test
    public void synchronizeSectionsFromCanvasToDb_SectionDoesntExistInDB() throws Exception {
        String expectedSectionName = "CIS 200 B";
        Long expectedCanvasSectionId = 17000L;
        Long expectedCanvasCourseId = 550L;
        int expectedListSize = 1;
        List<Section> sections = new ArrayList<>();
        Section section = new Section();
        section.setName(expectedSectionName);
        section.setId(expectedCanvasSectionId);
        section.setCourseId(expectedCanvasCourseId.intValue());
        sections.add(section);
        AviationSection expectedDbSection = new AviationSection();
        ArgumentCaptor<AviationSection> capturedSection = ArgumentCaptor.forClass(AviationSection.class);

        when(mockSectionRepository.save(any(AviationSection.class))).thenReturn(expectedDbSection);
        List<AviationSection> actualSections = WhiteboxImpl.invokeMethod(synchronizationService, "synchronizeSectionsFromCanvasToDb", sections);

        verify(mockSectionRepository, atLeastOnce()).save(capturedSection.capture());
        assertThat(actualSections.size(), is(equalTo(expectedListSize)));
        assertSame(expectedDbSection, actualSections.get(0));
        assertEquals(expectedSectionName, capturedSection.getValue().getName());
        assertEquals(expectedCanvasSectionId, capturedSection.getValue().getCanvasSectionId());
        assertEquals(expectedCanvasCourseId, capturedSection.getValue().getCanvasCourseId());
    }

    @Test
    public void synchronizeSectionsFromCanvasToDb_SectionExistsInDB() throws Exception {
        String previousSectionName = "CIS 200 B";
        Long previousCanvasSectionId = 17000L;
        Long previousCanvasCourseId = 550L;
        String expectedSectionName = "CIS 400 B";
        Long expectedCanvasSectionId = 18000L;
        Long expectedCanvasCourseId = 560L;
        int expectedListSize = 1;
        List<Section> sections = new ArrayList<>();
        Section section = new Section();
        section.setName(expectedSectionName);
        section.setId(expectedCanvasSectionId);
        section.setCourseId(expectedCanvasCourseId.intValue());
        sections.add(section);
        AviationSection expectedDbSection = new AviationSection();
        expectedDbSection.setName(previousSectionName);
        expectedDbSection.setCanvasSectionId(previousCanvasSectionId);
        expectedDbSection.setCanvasCourseId(previousCanvasCourseId);

        when(mockSectionRepository.findByCanvasSectionId(expectedCanvasSectionId)).thenReturn(expectedDbSection);
        when(mockSectionRepository.save(any(AviationSection.class))).thenReturn(expectedDbSection);
        List<AviationSection> actualSections = WhiteboxImpl.invokeMethod(synchronizationService, "synchronizeSectionsFromCanvasToDb", sections);

        verify(mockSectionRepository, atLeastOnce()).save(expectedDbSection);
        assertThat(actualSections.size(), is(equalTo(expectedListSize)));
        assertSame(expectedDbSection, actualSections.get(0));
        assertEquals(expectedSectionName, expectedDbSection.getName());
        assertEquals(expectedCanvasSectionId, expectedDbSection.getCanvasSectionId());
        assertEquals(expectedCanvasCourseId, expectedDbSection.getCanvasCourseId());
    }

    @Test
    public void synchronizeStudentsFromCanvasToDb_NewStudent() throws Exception {
        Long expectedCanvasSectionId = 200L;
        Long expectedCanvasCourseId = 500L;
        String expectedSisUserId = "uniqueSisId";
        String expectedName = "Zoglmann, Kurt";
        Integer expectedStudentsSavedToDb = 1;
        Boolean expectedDeleted = Boolean.FALSE;

        User user = new User();
        user.setSisUserId(expectedSisUserId);
        user.setSortableName(expectedName);
        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        List<Enrollment> enrollments = new ArrayList<>();
        enrollments.add(enrollment);
        Section section = new Section();
        section.setId(expectedCanvasSectionId);
        section.setCourseId(expectedCanvasCourseId.intValue());
        Map<Section, List<Enrollment>> canvasSectionMap = new HashMap<>();
        canvasSectionMap.put(section, enrollments);
        ArgumentCaptor<AviationStudent> capturedStudent = ArgumentCaptor.forClass(AviationStudent.class);
        AviationStudent expectedStudentSavedToDb = new AviationStudent();

        when(mockStudentRepository.save(any(AviationStudent.class))).thenReturn(expectedStudentSavedToDb);
        List<AviationStudent> actualStudents = WhiteboxImpl.invokeMethod(synchronizationService, "synchronizeStudentsFromCanvasToDb", canvasSectionMap);

        verify(mockStudentRepository, atLeastOnce()).save(capturedStudent.capture());
        assertThat(actualStudents.size(), is(equalTo(expectedStudentsSavedToDb)));
        assertSame(expectedStudentSavedToDb, actualStudents.get(0));
        assertEquals(expectedCanvasSectionId, capturedStudent.getValue().getCanvasSectionId());
        assertEquals(expectedCanvasCourseId, capturedStudent.getValue().getCanvasCourseId());
        assertEquals(expectedSisUserId, capturedStudent.getValue().getSisUserId());
        assertEquals(expectedName, capturedStudent.getValue().getName());
        assertEquals(expectedDeleted, capturedStudent.getValue().getDeleted());
    }

    @Test
    public void synchronizeStudentsFromCanvasToDb_UpdateExistingStudentInCourse() throws Exception {
        Long previousCanvasSectionId = 350L;
        Long previousCanvasCourseId = 350L;
        String previousSisUserId = "uniqueSisId";
        String previousName = "Zoglmann, Chris";
        AviationStudent expectedStudentInDb = new AviationStudent();
        expectedStudentInDb.setCanvasCourseId(previousCanvasCourseId);
        expectedStudentInDb.setCanvasSectionId(previousCanvasSectionId);
        expectedStudentInDb.setSisUserId(previousSisUserId);
        expectedStudentInDb.setName(previousName);
        List<AviationStudent> studentsInDbForCourse = new ArrayList<>();
        studentsInDbForCourse.add(expectedStudentInDb);

        Long expectedCanvasSectionId = 250L;
        Long expectedCanvasCourseId = 550L;
        String expectedSisUserId = "uniqueSisId";
        String expectedName = "Zoglmann, Kurt";
        Integer expectedStudentsSavedToDb = 1;
        Boolean expectedDeleted = Boolean.FALSE;

        User user = new User();
        user.setSisUserId(expectedSisUserId);
        user.setSortableName(expectedName);
        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        List<Enrollment> enrollments = new ArrayList<>();
        enrollments.add(enrollment);
        Section section = new Section();
        section.setId(expectedCanvasSectionId);
        section.setCourseId(expectedCanvasCourseId.intValue());
        Map<Section, List<Enrollment>> canvasSectionMap = new HashMap<>();
        canvasSectionMap.put(section, enrollments);

        when(mockStudentRepository.findByCanvasCourseId(expectedCanvasCourseId)).thenReturn(studentsInDbForCourse);
        when(mockStudentRepository.save(any(AviationStudent.class))).thenReturn(expectedStudentInDb);
        List<AviationStudent> actualStudents = WhiteboxImpl.invokeMethod(synchronizationService, "synchronizeStudentsFromCanvasToDb", canvasSectionMap);

        verify(mockStudentRepository, atLeastOnce()).save(expectedStudentInDb);
        assertThat(actualStudents.size(), is(equalTo(expectedStudentsSavedToDb)));
        assertSame(expectedStudentInDb, actualStudents.get(0));
        assertEquals(expectedCanvasSectionId, expectedStudentInDb.getCanvasSectionId());
        assertEquals(expectedCanvasCourseId, expectedStudentInDb.getCanvasCourseId());
        assertEquals(expectedSisUserId, expectedStudentInDb.getSisUserId());
        assertEquals(expectedName, expectedStudentInDb.getName());
        assertEquals(expectedDeleted, expectedStudentInDb.getDeleted());
    }

    @Test
    public void synchronizeStudentsFromCanvasToDb_UpdateDeletedStudentInCourse_StayDeleted() throws Exception {
        Long previousCanvasSectionId = 350L;
        Long previousCanvasCourseId = 350L;
        String previousSisUserId = "uniqueSisId";
        String previousName = "Zoglmann, Chris";
        AviationStudent expectedStudentInDb = new AviationStudent();
        expectedStudentInDb.setCanvasCourseId(previousCanvasCourseId);
        expectedStudentInDb.setCanvasSectionId(previousCanvasSectionId);
        expectedStudentInDb.setSisUserId(previousSisUserId);
        expectedStudentInDb.setName(previousName);
        expectedStudentInDb.setDeleted(Boolean.TRUE);
        List<AviationStudent> studentsInDbForCourse = new ArrayList<>();
        studentsInDbForCourse.add(expectedStudentInDb);

        Long expectedCanvasSectionId = 250L;
        Long expectedCanvasCourseId = 550L;
        String expectedSisUserId = "uniqueSisId";
        String expectedName = "Zoglmann, Kurt";
        Integer expectedStudentsSavedToDb = 1;
        Boolean expectedDeleted = Boolean.TRUE;

        User user = new User();
        user.setSisUserId(expectedSisUserId);
        user.setSortableName(expectedName);
        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        List<Enrollment> enrollments = new ArrayList<>();
        enrollments.add(enrollment);
        Section section = new Section();
        section.setId(expectedCanvasSectionId);
        section.setCourseId(expectedCanvasCourseId.intValue());
        Map<Section, List<Enrollment>> canvasSectionMap = new HashMap<>();
        canvasSectionMap.put(section, enrollments);

        when(mockStudentRepository.findByCanvasCourseId(expectedCanvasCourseId)).thenReturn(studentsInDbForCourse);
        when(mockStudentRepository.save(any(AviationStudent.class))).thenReturn(expectedStudentInDb);
        List<AviationStudent> actualStudents = WhiteboxImpl.invokeMethod(synchronizationService, "synchronizeStudentsFromCanvasToDb", canvasSectionMap);

        verify(mockStudentRepository, atLeastOnce()).save(expectedStudentInDb);
        assertThat(actualStudents.size(), is(equalTo(expectedStudentsSavedToDb)));
        assertSame(expectedStudentInDb, actualStudents.get(0));
        assertEquals(expectedCanvasSectionId, expectedStudentInDb.getCanvasSectionId());
        assertEquals(expectedCanvasCourseId, expectedStudentInDb.getCanvasCourseId());
        assertEquals(expectedSisUserId, expectedStudentInDb.getSisUserId());
        assertEquals(expectedName, expectedStudentInDb.getName());
        assertEquals(expectedDeleted, expectedStudentInDb.getDeleted());
    }

    @Test
    public void synchronizeStudentsFromCanvasToDb_DroppedStudent() throws Exception {
        Long expectedCanvasSectionId = 200L;
        Long expectedCanvasCourseId = 500L;

        Section section = new Section();
        section.setId(expectedCanvasSectionId);
        section.setCourseId(expectedCanvasCourseId.intValue());
        Map<Section, List<Enrollment>> canvasSectionMap = new HashMap<>();
        canvasSectionMap.put(section, Collections.emptyList());
        ArgumentCaptor<AviationStudent> capturedStudent = ArgumentCaptor.forClass(AviationStudent.class);
        AviationStudent expectedStudentSavedToDb = new AviationStudent();

        when(mockStudentRepository.save(any(AviationStudent.class))).thenReturn(expectedStudentSavedToDb);
        when(mockStudentRepository.findByCanvasCourseId(anyLong())).thenReturn(Collections.singletonList(expectedStudentSavedToDb));
        AviationStudent droppedStudent = new AviationStudent();
        droppedStudent.setDeleted(true);
        when(mockStudentRepository.save(
                argThat(
                        Matchers.both(
                                Matchers.isA(AviationStudent.class)).
                                and(Matchers.hasProperty("deleted", Matchers.hasValue(true))))))
                .thenReturn(droppedStudent);
        List<AviationStudent> secondSetOfStudents = WhiteboxImpl.invokeMethod(synchronizationService, "synchronizeStudentsFromCanvasToDb", canvasSectionMap);
        verify(mockStudentRepository, atLeastOnce()).save(capturedStudent.capture());
        assertEquals(droppedStudent, secondSetOfStudents.get(0));
        assertTrue("Dropped student should be marked as deleted", secondSetOfStudents.get(0).getDeleted());
    }

}
