package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.EnrollmentReader;
import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Section;
import edu.ksu.canvas.requestOptions.GetEnrollmentOptions;
import edu.ksu.lti.launch.exception.NoLtiSessionException;
import edu.ksu.lti.launch.model.LtiSession;
import edu.ksu.canvas.oauth.OauthToken;
import edu.ksu.lti.launch.service.LtiSessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CanvasApiWrapperServiceUTest {

    private CanvasApiWrapperService canvasService;

    @Mock
    private LtiSessionService mockLtiSessionService;

    @Mock
    private LtiSession mockLtiSession;

    @Mock
    private CanvasApiFactory mockCanvasApiFactory;

    @Mock
    private CanvasApiWrapperService.EnrollmentOptionsFactory enrollmentOptionsFactory;


    @Before
    public void setup() throws NoLtiSessionException {
        canvasService = new CanvasApiWrapperService();
        Whitebox.setInternalState(canvasService, mockLtiSessionService);
        Whitebox.setInternalState(canvasService, mockCanvasApiFactory);
        canvasService.setEnrollmentOptionsFactory(enrollmentOptionsFactory);
        
        when(mockLtiSessionService.getLtiSession()).thenReturn(mockLtiSession);
    }


    @Test
    @SuppressWarnings("unchecked")
    public void getEnrollmentsFromCanvas_HappyPath() throws Exception {
        Section firstSection = new Section();
        Long firstSectiondId = 1L;
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


}
