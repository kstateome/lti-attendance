package edu.ksu.canvas.aviation.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.entity.lti.OauthToken;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.canvas.interfaces.EnrollmentsReader;
import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Section;
import edu.ksu.lti.LtiLaunch;
import edu.ksu.lti.model.LtiSession;


@RunWith(MockitoJUnitRunner.class)
public class CanvasApiWrapperServiceUTest {

    private CanvasApiWrapperService canvasService;

    @Mock
    private LtiLaunch mockLtiLaunch;

    @Mock
    private LtiSession mockLtiSession;

    @Mock
    private CanvasApiFactory mockCanvasApiFactory;


    @Before
    public void setup() throws NoLtiSessionException {
        canvasService = new CanvasApiWrapperService();
        Whitebox.setInternalState(canvasService, mockLtiLaunch);
        Whitebox.setInternalState(canvasService, mockCanvasApiFactory);
        
        when(mockLtiLaunch.getLtiSession()).thenReturn(mockLtiSession);
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
        OauthToken mockOAuthToken = mock(OauthToken.class);
        int expectedMapSize = 2;

        when(mockLtiSession.getCanvasOauthToken()).thenReturn(mockOAuthToken);
        when(mockCanvasApiFactory.getReader(eq(EnrollmentsReader.class), any(String.class))).thenReturn(mockEnrollmentReader);
        when(mockEnrollmentReader.getSectionEnrollments(eq(firstSectiondId), any(List.class))).thenReturn(firstSectionEnrollments);
        when(mockEnrollmentReader.getSectionEnrollments(eq(secondSectionId), any(List.class))).thenReturn(secondSectionEnrollments);
        Map<Section, List<Enrollment>> actualMap = WhiteboxImpl.invokeMethod(canvasService, "getEnrollmentsFromCanvas", sections);

        assertEquals(expectedMapSize, actualMap.keySet().size());
        assertThat(actualMap.keySet(), containsInAnyOrder(firstSection, secondSection));
        assertThat(actualMap.get(firstSection), containsInAnyOrder(firstEnrollmentOfFirstSection, secondEnrollmentOfFirstSection));
        assertThat(actualMap.get(secondSection), containsInAnyOrder(firstEnrollmentOfSecondSection));
    }


}