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
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

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

    private NonRefreshableOauthToken oauthToken;
    private AttendanceAssignment attendanceAssignment;
    private Optional<Assignment> assignmentOptional;
    private Assignment assignment;
    private AttendanceSection attendanceSection;
    private List<AttendanceSection> sectionList;
    private Error error;

    @Before
    public void setup() {

        canvasAssignmentAssistant = new CanvasAssignmentAssistant();

        oauthToken = new NonRefreshableOauthToken(OAUTH_STRING);

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
        attendanceAssignment.setAttendanceSection(attendanceSection);

        Whitebox.setInternalState(canvasAssignmentAssistant, "assignmentService", assignmentService);
        Whitebox.setInternalState(canvasAssignmentAssistant, "attendanceSectionService", attendanceSectionService);
        Whitebox.setInternalState(canvasAssignmentAssistant, "assignmentRepository", assignmentRepository);
        Whitebox.setInternalState(canvasAssignmentAssistant, "canvasApiWrapperService", canvasApiWrapperService);
    }

    @Test
    public void createAssignmentInCanvasHappyPath() throws IOException {
        when(canvasApiWrapperService.createAssignment(any(), any(), any())).thenReturn(assignmentOptional);
        when(attendanceSectionService.getSectionsByCourse(COURSE_ID)).thenReturn(sectionList);
        when(assignmentService.findBySection(any())).thenReturn(attendanceAssignment);
        when(assignmentRepository.save(attendanceAssignment)).thenReturn(attendanceAssignment);

        Error createError = canvasAssignmentAssistant.createAssignmentInCanvas(COURSE_ID, attendanceAssignment, oauthToken);
        Assert.assertNull(createError);
    }

    @Test
    public void createAssignmentInCanvasNullReturnError() throws IOException {
        error = new Error("Error while creating canvas assignment for section: " + SECTION_NAME);
        assignmentOptional = Optional.empty();

        when(canvasApiWrapperService.createAssignment(any(), any(), any())).thenReturn(assignmentOptional);

        Error createError = canvasAssignmentAssistant.createAssignmentInCanvas(COURSE_ID, attendanceAssignment, oauthToken);
        Assert.assertNotNull(createError);
        Assert.assertEquals("Expected return error from creation", error.getMessage(), createError.getMessage());
    }

    @Test
    public void editAssignmentInCanvasHappyPath() throws IOException {
        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, CANVAS_ASSIGNMENT_ID+"")).thenReturn(assignmentOptional);

        Error editError = canvasAssignmentAssistant.editAssignmentInCanvas(COURSE_ID, attendanceAssignment, oauthToken);
        Assert.assertNull(editError);
    }

    @Test
    public void editAssignmentInCanvasCanvasAssignmentNotFound() throws IOException {
        error = new Error("Assignment not found in Canvas");
        assignmentOptional = Optional.empty();

        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, CANVAS_ASSIGNMENT_ID+"")).thenReturn(assignmentOptional);

        Error editError = canvasAssignmentAssistant.editAssignmentInCanvas(COURSE_ID, attendanceAssignment, oauthToken);
        Assert.assertNotNull(editError);
        Assert.assertEquals("Expected return error by not finding the canvas assignment.", error.getMessage(), editError.getMessage());
    }

    @Test
    public void deleteAssignmentInCanvasHappyPath() throws IOException {
        when(attendanceSectionService.getSectionByCanvasCourseId(COURSE_ID)).thenReturn(sectionList);
        when(assignmentRepository.findByAttendanceSection(sectionList.get(0))).thenReturn(attendanceAssignment);

        Error createError = canvasAssignmentAssistant.deleteAssignmentInCanvas(COURSE_ID, oauthToken);
        Assert.assertNull(createError);
    }

    @Test
    public void deleteAssignmentInCanvasAssignmentNotFoundError() throws IOException {
        error = new Error("Attendance assignment not found for section: " + SECTION_NAME);
        attendanceAssignment = null;

        when(attendanceSectionService.getSectionByCanvasCourseId(COURSE_ID)).thenReturn(sectionList);
        when(assignmentRepository.findByAttendanceSection(sectionList.get(0))).thenReturn(attendanceAssignment);

        Error createError = canvasAssignmentAssistant.deleteAssignmentInCanvas(COURSE_ID, oauthToken);
        Assert.assertNotNull(createError);
        Assert.assertEquals("Expected error not found attendance assignment.", error.getMessage(), createError.getMessage());
    }


}
