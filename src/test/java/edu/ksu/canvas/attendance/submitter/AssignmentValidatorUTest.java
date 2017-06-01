package edu.ksu.canvas.attendance.submitter;

import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.exception.AttendanceAssignmentException;
import edu.ksu.canvas.attendance.services.CanvasApiWrapperService;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.oauth.NonRefreshableOauthToken;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssignmentValidatorUTest {

    private static final Long COURSE_ID = 2121212121L;
    private static final String OAUTH_STRING = "sdfsdfSDFSDFsdfsdFSDFsdfSDFSDgfsdSFDFSDF";
    private static final long ASSIGNMENT_ID = 8484848484L;
    private static final String ASSIGNMENT_NAME = "NAME OF THE ASSIGNMENT";
    private static final Double ASSIGNMENT_POINTS = 100.0;
    private static final long CANVAS_ASSIGNMENT_ID = 514514514L;
    private static final String SECTION_NAME = "SECTION NAME";
    private static final long CANVAS_COURSE_ID = 11111L;
    private static final long SECTION_ID = 121212L;
    private static final long CANVAS_SECTION_ID = 212121L;

    private AssignmentValidator assignmentValidator;

    @Mock
    private CanvasApiWrapperService canvasApiWrapperService;

    private NonRefreshableOauthToken oauthToken;
    private AttendanceAssignment attendanceAssignment;
    private Optional<Assignment> assignmentOptional;
    private Assignment assignment;
    private AttendanceAssignment assignmentConfigurationFromSetup;
    private AttendanceSection attendanceSection;

    @Before
    public void setup() {

        assignmentValidator = new AssignmentValidator();

        oauthToken = new NonRefreshableOauthToken(OAUTH_STRING);

        assignmentConfigurationFromSetup = new AttendanceAssignment();
        assignmentConfigurationFromSetup.setAssignmentName(ASSIGNMENT_NAME);
        assignmentConfigurationFromSetup.setAbsentPoints(0.0);
        assignmentConfigurationFromSetup.setAssignmentPoints(ASSIGNMENT_POINTS);
        assignmentConfigurationFromSetup.setExcusedPoints(0.0);
        assignmentConfigurationFromSetup.setPresentPoints(100.0);
        assignmentConfigurationFromSetup.setTardyPoints(0.0);

        attendanceSection = new AttendanceSection();
        attendanceSection.setName(SECTION_NAME);
        attendanceSection.setCanvasCourseId(CANVAS_COURSE_ID);
        attendanceSection.setSectionId(SECTION_ID);
        attendanceSection.setCanvasSectionId(CANVAS_SECTION_ID);

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
        attendanceAssignment.setAttendanceSection(attendanceSection);
        attendanceAssignment.setStatus(AttendanceAssignment.Status.UNKNOWN);


        assignment = new Assignment();
        assignment.setPointsPossible(ASSIGNMENT_POINTS);
        assignmentOptional = Optional.of(assignment);

    }

    @Test
    public void validateAttendanceAssignmentHappyPath() throws IOException, AttendanceAssignmentException {
        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, Long.toString(CANVAS_ASSIGNMENT_ID))).thenReturn(assignmentOptional);
        assignmentValidator.validateAttendanceAssignment(COURSE_ID, attendanceAssignment, canvasApiWrapperService, oauthToken);
        Assert.assertNotEquals(AttendanceAssignment.Status.NOT_LINKED_TO_CANVAS, attendanceAssignment.getStatus());
        Assert.assertNotEquals(AttendanceAssignment.Status.CANVAS_AND_DB_DISCREPANCY, attendanceAssignment.getStatus());
    }

    @Test
    public void validateCanvasAssignmentHappyPath() throws IOException, AttendanceAssignmentException {
        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, Long.toString(CANVAS_ASSIGNMENT_ID))).thenReturn(assignmentOptional);

        assignmentValidator.validateCanvasAssignment(assignmentConfigurationFromSetup, COURSE_ID, attendanceAssignment, canvasApiWrapperService, oauthToken);
        Assert.assertNotEquals(AttendanceAssignment.Status.NOT_LINKED_TO_CANVAS, attendanceAssignment.getStatus());
        Assert.assertNotEquals(AttendanceAssignment.Status.CANVAS_AND_DB_DISCREPANCY, attendanceAssignment.getStatus());
    }

    @Test
    public void validateAttendanceAssignmentEmptyOptionalIdValidationError() throws Exception {
        assignmentOptional = Optional.empty();
        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, Long.toString(CANVAS_ASSIGNMENT_ID))).thenReturn(assignmentOptional);

        assignmentValidator.validateAttendanceAssignment(COURSE_ID, attendanceAssignment, canvasApiWrapperService, oauthToken);
        Assert.assertEquals(AttendanceAssignment.Status.NOT_LINKED_TO_CANVAS, attendanceAssignment.getStatus());
    }


    @Test
    public void validateAttendanceAssignmentNullCanvasIDError() throws Exception {
        attendanceAssignment.setCanvasAssignmentId(null);
        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, Long.toString(CANVAS_ASSIGNMENT_ID))).thenReturn(assignmentOptional);

        assignmentValidator.validateAttendanceAssignment(COURSE_ID, attendanceAssignment, canvasApiWrapperService, oauthToken);
        Assert.assertEquals(AttendanceAssignment.Status.NOT_LINKED_TO_CANVAS, attendanceAssignment.getStatus());
    }


    @Test
    public void validateCanvasAssignmentDBMismatchValidationError() throws Exception {
        assignmentConfigurationFromSetup.setAssignmentPoints(120.0);

        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, Long.toString(CANVAS_ASSIGNMENT_ID))).thenReturn(assignmentOptional);

        try {
            assignmentValidator.validateCanvasAssignment(assignmentConfigurationFromSetup, COURSE_ID, attendanceAssignment, canvasApiWrapperService, oauthToken);
            Assert.fail("Expected AttendanceAssignmentException.");
        } catch (AttendanceAssignmentException exception) {
            Assert.assertEquals(AttendanceAssignmentException.Error.NOT_SAVED, exception.error);
        }
    }

    @Test
    public void validateCanvasAssignmentEmptyAssignmentValidationError() throws Exception {
        assignmentOptional = Optional.empty();

        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, Long.toString(CANVAS_ASSIGNMENT_ID))).thenReturn(assignmentOptional);

        assignmentValidator.validateAttendanceAssignment(COURSE_ID, attendanceAssignment, canvasApiWrapperService, oauthToken);
        Assert.assertEquals(AttendanceAssignment.Status.NOT_LINKED_TO_CANVAS, attendanceAssignment.getStatus());
    }

}
