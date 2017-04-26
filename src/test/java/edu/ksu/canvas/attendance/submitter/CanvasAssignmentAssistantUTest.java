package edu.ksu.canvas.attendance.submitter;

import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.repository.AttendanceAssignmentRepository;
import edu.ksu.canvas.attendance.services.AttendanceAssignmentService;
import edu.ksu.canvas.attendance.services.AttendanceSectionService;
import edu.ksu.canvas.attendance.services.CanvasApiWrapperService;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.oauth.NonRefreshableOauthToken;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CanvasAssignmentAssistantUTest {

    private static final Long COURSE_ID = 2121212121L;
    private static final String OAUTH_STRING = "sdfsdfSDFSDFsdfsdFSDFsdfSDFSDgfsdSFDFSDF";
    private static final long ASSIGNMENT_ID = 8484848484L;
    private static final String ASSIGNMENT_NAME = "NAME OF THE ASSIGNMENT";
    private static final Double ASSIGNMENT_POINTS = 100.0;
    private static final long CANVAS_ASSIGNMENT_ID = 514514514L;
    private static final Long SECTION_ID = 111111111111L;
    private static final String SECTION_NAME = "SECTION NAME";
    private static final String ASSIGNMENT_DESCRIPTION = "This result of this assignment is based on attendances of each student. Detailed and individual information in the grading comments.";

    private CanvasAssignmentAssistant canvasAssignmentAssistant;

    @Mock
    private AttendanceAssignmentService assignmentService;

    @Mock
    private AttendanceSectionService attendanceSectionService;

    @Mock
    private AttendanceAssignmentRepository assignmentRepository;

    @Mock
    private CanvasApiWrapperService canvasApiWrapperService;

    private NonRefreshableOauthToken adminOauthToken;
    private AttendanceAssignment attendanceAssignment;
    private Optional<Assignment> assignmentOptional;
    private Assignment assignment;
    private Error error;
    private AttendanceSection attendanceSection;
    private List<AttendanceSection> sectionList;

    @Before
    public void setup() {

        canvasAssignmentAssistant = new CanvasAssignmentAssistant();

        adminOauthToken = new NonRefreshableOauthToken(OAUTH_STRING);

        attendanceAssignment = new AttendanceAssignment();
        attendanceAssignment.setAssignmentId(ASSIGNMENT_ID);
        attendanceAssignment.setAssignmentName(ASSIGNMENT_NAME);
        attendanceAssignment.setCanvasAssignmentId(CANVAS_ASSIGNMENT_ID);
        attendanceAssignment.setGradingOn(true);
        attendanceAssignment.setAssignmentPoints(ASSIGNMENT_POINTS);
        attendanceAssignment.setAbsentPoints(0.0);
        attendanceAssignment.setExcusedPoints(0.0);
        attendanceAssignment.setTardyPoints(0.0);
        attendanceAssignment.setPresentPoints(100.0);

        assignment = new Assignment();
        assignment.setId(ASSIGNMENT_ID+"");
        assignment.setName(attendanceAssignment.getAssignmentName());
        assignment.setPointsPossible(Double.valueOf(attendanceAssignment.getAssignmentPoints()));
        assignment.setCourseId(COURSE_ID + "");
        assignment.setDescription(ASSIGNMENT_DESCRIPTION);
        assignment.setPublished(true);
        assignment.setUnpublishable(false);
        assignmentOptional = Optional.of(assignment);

        attendanceSection = new AttendanceSection();
        attendanceSection.setCanvasCourseId(COURSE_ID);
        attendanceSection.setSectionId(SECTION_ID);
        attendanceSection.setName(SECTION_NAME);
        attendanceSection.setCanvasSectionId(SECTION_ID);
        sectionList = new ArrayList<>();
        sectionList.add(attendanceSection);

        Whitebox.setInternalState(canvasAssignmentAssistant, "assignmentService", assignmentService);
        Whitebox.setInternalState(canvasAssignmentAssistant, "attendanceSectionService", attendanceSectionService);
        Whitebox.setInternalState(canvasAssignmentAssistant, "assignmentRepository", assignmentRepository);
        Whitebox.setInternalState(canvasAssignmentAssistant, "canvasApiWrapper", canvasApiWrapperService);
    }

    @Test
    public void createAssignmentInCanvasHappyPath() throws IOException {
        when(canvasApiWrapperService.createAssignment(any(), any(), any())).thenReturn(assignmentOptional);
        when(attendanceSectionService.getSectionsByCourse(COURSE_ID)).thenReturn(sectionList);
        when(assignmentService.findBySectionId(SECTION_ID)).thenReturn(attendanceAssignment);
        when(assignmentRepository.save(attendanceAssignment)).thenReturn(attendanceAssignment);

        Error createError = canvasAssignmentAssistant.createAssignmentInCanvas(COURSE_ID, attendanceAssignment, adminOauthToken);
        Assert.assertNull(createError);
    }

    @Test
    public void editAssignmentInCanvasHappyPath() throws IOException {
        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, adminOauthToken, CANVAS_ASSIGNMENT_ID+"")).thenReturn(assignmentOptional);
        when(assignmentService.findBySectionId(SECTION_ID)).thenReturn(attendanceAssignment);

        Error editError = canvasAssignmentAssistant.editAssignmentInCanvas(COURSE_ID, attendanceAssignment, adminOauthToken);
        Assert.assertNull(editError);
    }
       

}
