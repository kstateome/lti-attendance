package edu.ksu.canvas.attendance.submitter;

import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.form.CourseConfigurationForm;
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
    private CourseConfigurationForm courseConfigurationForm;
    private  Error error;
    private AttendanceSection attendanceSection;

    @Before
    public void setup() {

        assignmentValidator = new AssignmentValidator();

        oauthToken = new NonRefreshableOauthToken(OAUTH_STRING);

        courseConfigurationForm = new CourseConfigurationForm();
        courseConfigurationForm.setAbsentPoints(0.0);
        courseConfigurationForm.setAssignmentPoints(ASSIGNMENT_POINTS);
        courseConfigurationForm.setExcusedPoints(0.0);
        courseConfigurationForm.setPresentPoints(100.0);
        courseConfigurationForm.setTardyPoints(0.0);

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

        assignment = new Assignment();
        assignment.setPointsPossible(ASSIGNMENT_POINTS);
        assignmentOptional = Optional.of(assignment);

    }

    @Test
    public void validateAttendanceAssignmentHappyPath() throws IOException {
        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, CANVAS_ASSIGNMENT_ID+"")).thenReturn(assignmentOptional);

        Error validationError = assignmentValidator.validateAttendanceAssignment(COURSE_ID, attendanceAssignment, canvasApiWrapperService, oauthToken);
        Assert.assertNull(validationError);
    }

    @Test
    public void validateCanvasAssignmentHappyPath() throws IOException {
        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, CANVAS_ASSIGNMENT_ID+"")).thenReturn(assignmentOptional);

        Error validationError = assignmentValidator.validateCanvasAssignment(courseConfigurationForm, COURSE_ID, attendanceAssignment, canvasApiWrapperService, oauthToken);
        Assert.assertNull(validationError);
    }

    @Test
    public void validateAttendanceAssignmentNullAssignmentIdValidationError() throws IOException {
        error = new Error("NO CANVAS ASSIGNMENT LINKED");

        attendanceAssignment.setCanvasAssignmentId(null);
        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, CANVAS_ASSIGNMENT_ID + "")).thenReturn(assignmentOptional);

        Error validationError = assignmentValidator.validateAttendanceAssignment(COURSE_ID, attendanceAssignment, canvasApiWrapperService, oauthToken);

        Assert.assertNotNull(validationError);
        Assert.assertEquals("Expected to return the error", error.getMessage(), validationError.getMessage());
    }

    @Test
    public void validateAttendanceAssignmentEmptyOptionalIdValidationError() throws IOException {
        error = new Error("NO CANVAS ASSIGNMENT LINKED");

        attendanceAssignment.setCanvasAssignmentId(null);
        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, CANVAS_ASSIGNMENT_ID + "")).thenReturn(assignmentOptional);

        Error validationError = assignmentValidator.validateAttendanceAssignment(COURSE_ID, attendanceAssignment, canvasApiWrapperService, oauthToken);

        Assert.assertNotNull(validationError);
        Assert.assertEquals("Expected to return the error", error.getMessage(), validationError.getMessage());
    }

    @Test
    public void validateCanvasAssignmentDBMismatchValidationError() throws IOException {
        courseConfigurationForm.setAssignmentPoints(120.0);
        error = new Error("Assignment configuration needs to be saved before pushing to Canvas");

        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, CANVAS_ASSIGNMENT_ID + "")).thenReturn(assignmentOptional);

        Error validationError = assignmentValidator.validateCanvasAssignment(courseConfigurationForm, COURSE_ID, attendanceAssignment, canvasApiWrapperService, oauthToken);

        Assert.assertNotNull(validationError);
        Assert.assertEquals("Expected to return the error", error.getMessage(), validationError.getMessage());
    }

    @Test
    public void validateCanvasAssignmentEmptyAssignmentValidationError() throws IOException {
        assignmentOptional.get().setPointsPossible(12.0);
        error = new Error("DISCREPANCY BETWEEN CANVAS AND DATABASE");

        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, CANVAS_ASSIGNMENT_ID + "")).thenReturn(assignmentOptional);

        Error validationError = assignmentValidator.validateCanvasAssignment(courseConfigurationForm, COURSE_ID, attendanceAssignment, canvasApiWrapperService, oauthToken);

        Assert.assertNotNull(validationError);
        Assert.assertEquals("Expected to return the error", error.getMessage(), validationError.getMessage());
    }

}
