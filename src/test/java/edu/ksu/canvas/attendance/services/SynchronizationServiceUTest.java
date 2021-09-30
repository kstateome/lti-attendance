package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import edu.ksu.canvas.attendance.repository.AttendanceCourseRepository;
import edu.ksu.canvas.attendance.repository.AttendanceSectionRepository;
import edu.ksu.canvas.attendance.repository.AttendanceStudentRepository;
import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Section;
import edu.ksu.canvas.model.User;
import edu.ksu.canvas.oauth.OauthToken;
import edu.ksu.canvas.oauth.OauthTokenRefresher;
import edu.ksu.canvas.oauth.RefreshableOauthToken;
import edu.ksu.canvas.attendance.repository.ConfigRepository;
import edu.ksu.lti.launch.exception.NoLtiSessionException;
import edu.ksu.lti.launch.model.LtiSession;
import edu.ksu.lti.launch.service.LtiSessionService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.io.IOException;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest(SynchronizationService.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class SynchronizationServiceUTest {

    private SynchronizationService synchronizationService;

    @Mock
    private AttendanceCourseRepository mockCourseRepository;

    @Mock
    private AttendanceStudentRepository mockStudentRepository;

    @Mock
    private AttendanceSectionRepository mockSectionRepository;

    @Mock
    private CanvasApiWrapperService mockCanvasService;

    @Mock
    private ConfigRepository mockConfigRepository;

    @Mock
    private LtiSessionService mockLtiSessionService;

    @Mock
    private OauthTokenRefresher mockOauthTokenRefresher;

    public static final String ARBITRARY_REFRESH_TOKEN = "sdalkkfjasldkjfalskdjfslkdjfalsdkjf";

    public static final String ARBITRARY_ACCESS_TOKEN = "1726~apC4CBtG4uZakngggggghsBuxwCkdrJZOu2jDstbizQyAJresn3BFKxIiUXPON0k";

    public static final String SYNC_STUDENTS_TO_DB = "synchronizeStudentsFromCanvasToDb";

    public static final String SYNC_COURSE_TO_DB = "synchronizeCourseFromCanvasToDb";

    public static final String SYNC_SECTIONS_TO_DB = "synchronizeSectionsFromCanvasToDb";

    @Before
    public void setup() {
        synchronizationService = new SynchronizationService();
        Whitebox.setInternalState(synchronizationService, mockCourseRepository);
        Whitebox.setInternalState(synchronizationService, mockStudentRepository);
        Whitebox.setInternalState(synchronizationService, mockSectionRepository);
        Whitebox.setInternalState(synchronizationService, mockCanvasService);
        Whitebox.setInternalState(synchronizationService, mockLtiSessionService);
        Whitebox.setInternalState(synchronizationService, mockConfigRepository);
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
        OauthToken oauthToken = new RefreshableOauthToken(mockOauthTokenRefresher, ARBITRARY_REFRESH_TOKEN, ARBITRARY_ACCESS_TOKEN);
        LtiSession ltiSession = new LtiSession();
        ltiSession.setOauthToken(oauthToken);
        Whitebox.setInternalState(neuteredSync, mockCourseRepository);
        Whitebox.setInternalState(neuteredSync, mockCanvasService);
        Whitebox.setInternalState(neuteredSync, mockLtiSessionService);
        Whitebox.setInternalState(neuteredSync, mockConfigRepository);
        SynchronizationService spy = spy(neuteredSync);
        when(mockCourseRepository.findByCanvasCourseId(canvasCourseId)).thenReturn(null);
        when(mockLtiSessionService.getLtiSession()).thenReturn(ltiSession);

        spy.synchronizeWhenCourseNotExistsInDB(canvasCourseId);

        verify(spy, times(1)).synchronize(canvasCourseId);
    }

    @Test
    public void synchronize_HappyPatchCallsInternalSynchornizationMethods() throws Exception {
        long canvasCourseId = 300L;
        SynchronizationService spy = spy(synchronizationService);
        OauthToken oauthToken = new RefreshableOauthToken(mockOauthTokenRefresher, ARBITRARY_REFRESH_TOKEN, ARBITRARY_ACCESS_TOKEN);

        LtiSession ltiSession = new LtiSession();
        ltiSession.setOauthToken(oauthToken);
        when(mockLtiSessionService.getLtiSession()).thenReturn(ltiSession);

        spy.synchronize(canvasCourseId);

        verifyPrivate(spy, times(1)).invoke("synchronizeCourseFromCanvasToDb", canvasCourseId);
        verifyPrivate(spy, times(1)).invoke("synchronizeSectionsFromCanvasToDb", any(List.class));
        verifyPrivate(spy, times(1)).invoke(SYNC_STUDENTS_TO_DB, anyLong(), any(Map.class), anyBoolean());
    }

    @Test
    public void synchronizeCourseFromCanvasToDb_NoExistingCourse() throws Exception {
        Long expectedCanvasCourseId = 500L;
        ArgumentCaptor<AttendanceCourse> capturedCourse = ArgumentCaptor.forClass(AttendanceCourse.class);
        AttendanceCourse expectedDbCourse = new AttendanceCourse();

        when(mockCourseRepository.save(any(AttendanceCourse.class))).thenReturn(expectedDbCourse);
        AttendanceCourse actualCourse = WhiteboxImpl.invokeMethod(synchronizationService, SYNC_COURSE_TO_DB, expectedCanvasCourseId);

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
        section.setCourseId(expectedCanvasCourseId);
        sections.add(section);
        AttendanceSection expectedDbSection = new AttendanceSection();
        ArgumentCaptor<AttendanceSection> capturedSection = ArgumentCaptor.forClass(AttendanceSection.class);

        when(mockSectionRepository.save(any(AttendanceSection.class))).thenReturn(expectedDbSection);
        List<AttendanceSection> actualSections = WhiteboxImpl.invokeMethod(synchronizationService, SYNC_SECTIONS_TO_DB, sections);

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
        section.setCourseId(expectedCanvasCourseId);
        sections.add(section);
        AttendanceSection expectedDbSection = new AttendanceSection();
        expectedDbSection.setName(previousSectionName);
        expectedDbSection.setCanvasSectionId(previousCanvasSectionId);
        expectedDbSection.setCanvasCourseId(previousCanvasCourseId);

        when(mockSectionRepository.findByCanvasSectionId(expectedCanvasSectionId)).thenReturn(expectedDbSection);
        when(mockSectionRepository.save(any(AttendanceSection.class))).thenReturn(expectedDbSection);
        List<AttendanceSection> actualSections = WhiteboxImpl.invokeMethod(synchronizationService, SYNC_SECTIONS_TO_DB, sections);

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
        String expectedName = "Smith, John";
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
        section.setCourseId(expectedCanvasCourseId);
        Map<Section, List<Enrollment>> canvasSectionMap = new HashMap<>();
        canvasSectionMap.put(section, enrollments);
        ArgumentCaptor<AttendanceStudent> capturedStudent = ArgumentCaptor.forClass(AttendanceStudent.class);
        AttendanceStudent expectedStudentSavedToDb = new AttendanceStudent();

        when(mockStudentRepository.save(any(AttendanceStudent.class))).thenReturn(expectedStudentSavedToDb);
        List<AttendanceStudent> actualStudents = WhiteboxImpl.invokeMethod(synchronizationService, SYNC_STUDENTS_TO_DB, expectedCanvasCourseId, canvasSectionMap, anyBoolean());

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
        String previousName = "Doe, Jane";
        AttendanceStudent expectedStudentInDb = new AttendanceStudent();
        expectedStudentInDb.setCanvasCourseId(previousCanvasCourseId);
        expectedStudentInDb.setCanvasSectionId(previousCanvasSectionId);
        expectedStudentInDb.setSisUserId(previousSisUserId);
        expectedStudentInDb.setName(previousName);
        List<AttendanceStudent> studentsInDbForCourse = new ArrayList<>();
        studentsInDbForCourse.add(expectedStudentInDb);

        Long expectedCanvasSectionId = 250L;
        Long expectedCanvasCourseId = 550L;
        String expectedSisUserId = "uniqueSisId";
        String expectedName = "Smith, John";
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
        section.setCourseId(expectedCanvasCourseId);
        Map<Section, List<Enrollment>> canvasSectionMap = new HashMap<>();
        canvasSectionMap.put(section, enrollments);

        when(mockStudentRepository.findByCanvasSectionIdOrderByNameAsc(expectedCanvasSectionId)).thenReturn(studentsInDbForCourse);
        when(mockStudentRepository.save(any(AttendanceStudent.class))).thenReturn(expectedStudentInDb);
        List<AttendanceStudent> actualStudents = WhiteboxImpl.invokeMethod(synchronizationService, SYNC_STUDENTS_TO_DB, expectedCanvasCourseId, canvasSectionMap, anyBoolean());

        verify(mockStudentRepository, atLeastOnce()).save(expectedStudentInDb);
        //assertThat(actualStudents.size(), is(equalTo(expectedStudentsSavedToDb)));
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
        section.setCourseId(expectedCanvasCourseId);
        Map<Section, List<Enrollment>> canvasSectionMap = new HashMap<>();
        canvasSectionMap.put(section, Collections.emptyList());
        ArgumentCaptor<AttendanceStudent> capturedStudent = ArgumentCaptor.forClass(AttendanceStudent.class);
        AttendanceStudent expectedStudentSavedToDb = new AttendanceStudent();

        when(mockStudentRepository.save(any(AttendanceStudent.class))).thenReturn(expectedStudentSavedToDb);
        when(mockStudentRepository.findByCanvasSectionIdOrderByNameAsc(anyLong())).thenReturn(Collections.singletonList(expectedStudentSavedToDb));
        AttendanceStudent droppedStudent = new AttendanceStudent();
        droppedStudent.setDeleted(true);
        when(mockStudentRepository.save(
                argThat(
                        Matchers.both(
                                Matchers.isA(AttendanceStudent.class)).
                                and(Matchers.hasProperty("deleted", Matchers.hasValue(true))))))
                .thenReturn(droppedStudent);
        List<AttendanceStudent> secondSetOfStudents = WhiteboxImpl.invokeMethod(synchronizationService, SYNC_STUDENTS_TO_DB, expectedCanvasCourseId, canvasSectionMap, anyBoolean());
        verify(mockStudentRepository, atLeastOnce()).save(capturedStudent.capture());
        assertEquals(droppedStudent, secondSetOfStudents.get(0));
        assertTrue("Dropped student should be marked as deleted", secondSetOfStudents.get(0).getDeleted());
    }

}
