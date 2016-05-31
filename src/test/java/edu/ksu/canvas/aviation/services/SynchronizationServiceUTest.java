package edu.ksu.canvas.aviation.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.aviation.entity.AviationCourse;
import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.repository.AviationCourseRepository;
import edu.ksu.canvas.aviation.repository.AviationSectionRepository;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;
import edu.ksu.canvas.entity.lti.OauthToken;
import edu.ksu.canvas.interfaces.EnrollmentsReader;
import edu.ksu.canvas.interfaces.SectionReader;
import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Section;
import edu.ksu.canvas.model.User;
import edu.ksu.lti.model.LtiSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.spy;


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
        //exception is thrown when tests fails
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
    @SuppressWarnings("unchecked")
    public void synchronize_HappyPatchCallsInternalSynchornizationMethods() throws Exception {
        long canvasCourseId = 300L;
        EnrollmentsReader mockEnrollmentReader = mock(EnrollmentsReader.class);
        OauthToken mockOauthToken = mock(OauthToken.class);
        SectionReader mockSectionReader = mock(SectionReader.class);
        SynchronizationService spy = spy(synchronizationService);

        when(mockLtiSession.getCanvasOauthToken()).thenReturn(mockOauthToken);
        when(mockCanvasApiFactory.getReader(eq(SectionReader.class), any(String.class))).thenReturn(mockSectionReader);
        when(mockSectionReader.listCourseSections(any(Integer.class), any(List.class))).thenReturn(new ArrayList<>());
        when(mockCanvasApiFactory.getReader(eq(EnrollmentsReader.class), any(String.class))).thenReturn(mockEnrollmentReader);
        when(mockEnrollmentReader.getSectionEnrollments(any(Integer.class), any(List.class))).thenReturn(new ArrayList<>());
        spy.synchronize(mockLtiSession, canvasCourseId);

        verifyPrivate(spy, times(1)).invoke("synchronizeCourseFromCanvasToDb", canvasCourseId);
        verifyPrivate(spy, times(1)).invoke("synchronizeSectionsFromCanvasToDb", any(List.class));
        verifyPrivate(spy, times(1)).invoke("synchronizeStudentsFromCanvasToDb", any(Map.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getEnrollmentsFromCanvas_HappyPath() throws Exception {
        Section firstSection = new Section();
        int firstSectiondId = 1;
        firstSection.setId(firstSectiondId);
        Section secondSection = new Section();
        int secondSectionId = 2;
        secondSection.setId(2L);
        List<Section> sections = new ArrayList<>();
        sections.add(firstSection);
        sections.add(secondSection);
        Enrollment firstEnrollmentOfFirstSection = new Enrollment();
        firstEnrollmentOfFirstSection.setId(10L);
        Enrollment secondEnrollmentOfFirstSection = new Enrollment();
        secondEnrollmentOfFirstSection.setId(20L);
        Enrollment firstEnrollmentOfSecondSection = new Enrollment();
        firstEnrollmentOfSecondSection.setId(30L);
        List<Enrollment> firstSectionEnrollments = new ArrayList<>();
        firstSectionEnrollments.add(firstEnrollmentOfFirstSection);
        firstSectionEnrollments.add(secondEnrollmentOfFirstSection);
        List<Enrollment> secondSectionEnrollments = new ArrayList<>();
        secondSectionEnrollments.add(firstEnrollmentOfSecondSection);
        EnrollmentsReader mockEnrollmentReader = mock(EnrollmentsReader.class);
        int expectedMapSize = 2;

        when(mockEnrollmentReader.getSectionEnrollments(eq(firstSectiondId), any(List.class))).thenReturn(firstSectionEnrollments);
        when(mockEnrollmentReader.getSectionEnrollments(eq(secondSectionId), any(List.class))).thenReturn(secondSectionEnrollments);
        Map<Section, List<Enrollment>> actualMap = WhiteboxImpl.invokeMethod(synchronizationService, "getEnrollmentsFromCanvas", sections, mockEnrollmentReader);

        assertEquals(expectedMapSize, actualMap.keySet().size());
        assertThat(actualMap.keySet(), containsInAnyOrder(firstSection, secondSection));
        assertThat(actualMap.get(firstSection), containsInAnyOrder(firstEnrollmentOfFirstSection, secondEnrollmentOfFirstSection));
        assertThat(actualMap.get(secondSection), containsInAnyOrder(firstEnrollmentOfSecondSection));
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
    }

}
