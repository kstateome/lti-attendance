package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.AssignmentOverrideWriter;
import edu.ksu.canvas.interfaces.EnrollmentReader;
import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Section;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.model.assignment.AssignmentOverride;
import edu.ksu.canvas.requestOptions.GetEnrollmentOptions;
import edu.ksu.lti.launch.exception.NoLtiSessionException;
import edu.ksu.lti.launch.model.LtiSession;
import edu.ksu.canvas.oauth.OauthToken;
import edu.ksu.lti.launch.service.LtiSessionService;
import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CanvasApiWrapperServiceUTest {

    private static final Logger LOG = Logger.getLogger(CanvasApiWrapperService.class);


    private CanvasApiWrapperService canvasService;

    @Mock
    private LtiSessionService mockLtiSessionService;

    @Mock
    private LtiSession mockLtiSession;

    @Mock
    private CanvasApiFactory mockCanvasApiFactory;

    @Mock
    private CanvasApiWrapperService.EnrollmentOptionsFactory enrollmentOptionsFactory;

    @Mock
    private OauthToken mockOauthToken;


    @Before
    public void setup() throws NoLtiSessionException {
        canvasService = new CanvasApiWrapperService();
        Whitebox.setInternalState(canvasService, mockLtiSessionService);
        Whitebox.setInternalState(canvasService, mockCanvasApiFactory);
        canvasService.setEnrollmentOptionsFactory(enrollmentOptionsFactory);
        
        when(mockLtiSessionService.getLtiSession()).thenReturn(mockLtiSession);
        when(mockLtiSession.getOauthToken()).thenReturn(mockOauthToken);
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
        EnrollmentReader mockEnrollmentReader = mock(EnrollmentReader.class);
        OauthToken mockOAuthToken = mock(OauthToken.class);
        int expectedMapSize = 2;
        GetEnrollmentOptions enrollmentOptions1 = new GetEnrollmentOptions(Long.toString(firstSectiondId));
        enrollmentOptions1.type(Collections.singletonList(GetEnrollmentOptions.EnrollmentType.STUDENT));
        GetEnrollmentOptions enrollmentOptions2 = new GetEnrollmentOptions(Long.toString(secondSectionId));
        enrollmentOptions2.type(Collections.singletonList(GetEnrollmentOptions.EnrollmentType.STUDENT));

        when(mockLtiSession.getOauthToken()).thenReturn(mockOAuthToken);
        when(mockCanvasApiFactory.getReader(eq(EnrollmentReader.class), any(OauthToken.class))).thenReturn(mockEnrollmentReader);
        when(enrollmentOptionsFactory.buildEnrollmentOptions(eq(firstSection))).thenReturn(enrollmentOptions1);
        when(enrollmentOptionsFactory.buildEnrollmentOptions(eq(secondSection))).thenReturn(enrollmentOptions2);
        when(mockEnrollmentReader.getSectionEnrollments(enrollmentOptions1)).thenReturn(firstSectionEnrollments);
        when(mockEnrollmentReader.getSectionEnrollments(enrollmentOptions2)).thenReturn(secondSectionEnrollments);
        Map<Section, List<Enrollment>> actualMap = WhiteboxImpl.invokeMethod(canvasService, "getEnrollmentsFromCanvas", sections, mockLtiSession.getOauthToken());

        assertEquals(expectedMapSize, actualMap.keySet().size());
        assertThat(actualMap.keySet(), containsInAnyOrder(firstSection, secondSection));
        assertThat(actualMap.get(firstSection), containsInAnyOrder(firstEnrollmentOfFirstSection, secondEnrollmentOfFirstSection));
        assertThat(actualMap.get(secondSection), containsInAnyOrder(firstEnrollmentOfSecondSection));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createAssignmentOverride_ReturnCorrectOverride() throws Exception{
        Integer correctSectionId = 1010101;
        Integer correctAssignmentId = 10101010;
        Integer incorrectSectionId = 1000;
        Integer incorrectAssignmentId = 2000;
        String courseId = "irrelevantCourseId";
        AssignmentOverrideWriter mockWriter = mock(AssignmentOverrideWriter.class);

        AssignmentOverride incorrectOverride = new AssignmentOverride();
        incorrectOverride.setCourseSectionId(incorrectSectionId);
        incorrectOverride.setAssignmentId(incorrectAssignmentId);
        Optional<AssignmentOverride> incorrectOptional = Optional.of(incorrectOverride);

        AssignmentOverride correctOverride = new AssignmentOverride();
        correctOverride.setCourseSectionId(correctSectionId);
        correctOverride.setAssignmentId(correctAssignmentId);

        when(mockCanvasApiFactory.getWriter(eq(AssignmentOverrideWriter.class), any(OauthToken.class))).thenReturn(mockWriter);
        when(mockWriter.createAssignmentOverride(eq(courseId), any(AssignmentOverride.class))).thenReturn(incorrectOptional);
        AssignmentOverride returnOverride = canvasService.createAssignmentOverride(mockOauthToken, incorrectSectionId, incorrectAssignmentId, courseId);

        assertNotEquals(returnOverride.getAssignmentId(), correctOverride.getAssignmentId());
        assertNotEquals(returnOverride.getCourseSectionId(), correctOverride.getCourseSectionId());
    }


}
