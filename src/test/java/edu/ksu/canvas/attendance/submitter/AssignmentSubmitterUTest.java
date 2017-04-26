package edu.ksu.canvas.attendance.submitter;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import edu.ksu.canvas.attendance.enums.AttendanceType;
import edu.ksu.canvas.attendance.form.CourseConfigurationForm;
import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.repository.AttendanceCourseRepository;
import edu.ksu.canvas.attendance.repository.AttendanceRepository;
import edu.ksu.canvas.attendance.services.AttendanceAssignmentService;
import edu.ksu.canvas.attendance.services.CanvasApiWrapperService;
import edu.ksu.canvas.interfaces.AssignmentReader;
import edu.ksu.canvas.interfaces.SubmissionWriter;
import edu.ksu.canvas.model.Progress;
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
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssignmentSubmitterUTest {

    private static final String CANVAS_URL = "canvas.example.edu";
    private static final Long SECTION_ID = 111111111111L;
    private static final Long SECTION_2_ID = 222222222222L;
    private static final Long COURSE_ID = 2121212121L;
    private static final String STUDENT_1_COMMENT = "student 1 comment";
    private static final String STUDENT_2_COMMENT = "student 2 comment";
    private static final String STUDENT_3_COMMENT = "student 3 comment";
    private static final String STUDENT_4_COMMENT = "student 4 comment";
    private static final String STUDENT_1_NAME = "STUDENT 1 NAME";
    private static final String STUDENT_2_NAME = "STUDENT 2 NAME";
    private static final String STUDENT_3_NAME = "STUDENT 3 NAME";
    private static final String STUDENT_4_NAME = "STUDENT 4 NAME";
    private static final Long STUDENT_1_ID = 55555555L;
    private static final Long STUDENT_2_ID = 22222222L;
    private static final Long STUDENT_3_ID = 55666555L;
    private static final Long STUDENT_4_ID = 22999222L;
    private static final String OAUTH_STRING = "sdfsdfSDFSDFsdfsdFSDFsdfSDFSDgfsdSFDFSDF";
    private static final long ASSIGNMENT_ID = 8484848484L;
    private static final long ASSIGNMENT_ID_2 = 77777778484L;
    private static final String ASSIGNMENT_NAME = "NAME OF THE ASSIGNMENT";
    private static final String ASSIGNMENT_NAME_2 = "NAME OF THE SECOND ASSIGNMENT";
    private static final Double ASSIGNMENT_POINTS = 100.0;
    private static final long CANVAS_ASSIGNMENT_ID = 514514514L;
    private static final int PROGRESS_ID = 19781971;

    private AssignmentSubmitter assignmentSubmitter;

    @Mock
    private CanvasApiFactory canvasApiFactory;

    @Mock
    private SubmissionWriter submissionWriter;

    @Mock
    private AssignmentReader assignmentReader;

    @Mock
    private AttendanceCourseRepository courseRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private AttendanceAssignmentService assignmentService;

    @Mock
    private CanvasApiWrapperService canvasApiWrapperService;

    @Mock
    private CanvasAssignmentAssistant canvasAssignmentAssistant;



    private NonRefreshableOauthToken oauthToken;
    private AttendanceSummaryModel attendanceSummaryModel1;
    private AttendanceSummaryModel attendanceSummaryModel2;
    private List<AttendanceSummaryModel> attendanceSummaryModelList;
    private AttendanceAssignment attendanceAssignment;
    private AttendanceAssignment attendanceAssignment2;
    private Optional<Assignment> assignmentOptional;
    private Assignment assignment;
    private AttendanceCourse course;
    private Map<Long, String> studentCommentsMap;
    private Map<Long, String> studentCommentsMap2;
    private Optional<Progress> progressOptional;
    private Progress progress;
    private CourseConfigurationForm courseConfigurationForm;
    private  Error error;
    private Error error2;

    @Before
    public void setup() {

        oauthToken = new NonRefreshableOauthToken(OAUTH_STRING);
        attendanceSummaryModel1 = new AttendanceSummaryModel(SECTION_ID);
        AttendanceSummaryModel.Entry entry1 = new AttendanceSummaryModel.Entry(COURSE_ID, SECTION_ID, STUDENT_1_ID, STUDENT_1_NAME, false, 0, 1, 0, 3);
        AttendanceSummaryModel.Entry entry2 = new AttendanceSummaryModel.Entry(COURSE_ID, SECTION_ID, STUDENT_2_ID, STUDENT_2_NAME, false, 0, 2, 0, 2);
        attendanceSummaryModel1.add(entry1);
        attendanceSummaryModel1.add(entry2);

        attendanceSummaryModel2 = new AttendanceSummaryModel(SECTION_2_ID);
        AttendanceSummaryModel.Entry entry3 = new AttendanceSummaryModel.Entry(COURSE_ID, SECTION_ID, STUDENT_3_ID, STUDENT_3_NAME, false, 0, 3, 0, 1);
        AttendanceSummaryModel.Entry entry4 = new AttendanceSummaryModel.Entry(COURSE_ID, SECTION_ID, STUDENT_4_ID, STUDENT_4_NAME, false, 0, 0, 0, 4);
        attendanceSummaryModel2.add(entry3);
        attendanceSummaryModel2.add(entry4);

        attendanceSummaryModelList = new ArrayList<>();
        attendanceSummaryModelList.add(attendanceSummaryModel1);
        attendanceSummaryModelList.add(attendanceSummaryModel2);

        courseConfigurationForm = new CourseConfigurationForm();
        courseConfigurationForm.setAbsentPoints(0.0);
        courseConfigurationForm.setAssignmentPoints(ASSIGNMENT_POINTS);
        courseConfigurationForm.setExcusedPoints(0.0);
        courseConfigurationForm.setPresentPoints(100.0);
        courseConfigurationForm.setTardyPoints(0.0);
        assignmentSubmitter = new AssignmentSubmitter(assignmentService, courseRepository, attendanceRepository, canvasApiWrapperService, COURSE_ID, oauthToken, courseConfigurationForm);

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

        attendanceAssignment2 = new AttendanceAssignment();
        attendanceAssignment2.setAssignmentId(ASSIGNMENT_ID_2);
        attendanceAssignment2.setAssignmentName(ASSIGNMENT_NAME_2);
        attendanceAssignment2.setCanvasAssignmentId(CANVAS_ASSIGNMENT_ID);
        attendanceAssignment2.setGradingOn(true);
        attendanceAssignment2.setAssignmentPoints(ASSIGNMENT_POINTS);
        attendanceAssignment2.setAbsentPoints(0.0);
        attendanceAssignment2.setExcusedPoints(0.0);
        attendanceAssignment2.setTardyPoints(0.0);
        attendanceAssignment2.setPresentPoints(100.0);

        assignment = new Assignment();
        assignment.setPointsPossible(ASSIGNMENT_POINTS);
        assignmentOptional = Optional.of(assignment);

        course = new AttendanceCourse();
        course.setCanvasCourseId(COURSE_ID);
        course.setAttendanceType(AttendanceType.SIMPLE);
        course.setShowNotesToStudents(true);

        studentCommentsMap = new HashMap<>();
        studentCommentsMap.put(STUDENT_1_ID, STUDENT_1_COMMENT);
        studentCommentsMap.put(STUDENT_2_ID, STUDENT_2_COMMENT);

        studentCommentsMap2 = new HashMap<>();
        studentCommentsMap2.put(STUDENT_3_ID, STUDENT_3_COMMENT);
        studentCommentsMap2.put(STUDENT_4_ID, STUDENT_4_COMMENT);

        progress = new Progress();
        progress.setId(PROGRESS_ID);
        progress.setUrl(CANVAS_URL);
        progress.setWorkflowState("queued");
        progressOptional = Optional.of(progress);

        error = new Error("Error");
        Whitebox.setInternalState(assignmentSubmitter, "canvasAssignmentAssistant", canvasAssignmentAssistant);

        error2 = new Error("Could not push grades of section: " + SECTION_ID);

    }

    @Test
    public void submitCourseAttendancesHappyPath() throws IOException {
        when(assignmentService.findBySectionId(SECTION_ID)).thenReturn(attendanceAssignment);
        when(assignmentService.findBySectionId(SECTION_2_ID)).thenReturn(attendanceAssignment2);
        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, CANVAS_ASSIGNMENT_ID+"")).thenReturn(assignmentOptional);
        when(courseRepository.findByCanvasCourseId(eq(COURSE_ID))).thenReturn(course);
        when(attendanceRepository.getAttendanceCommentsBySectionId(SECTION_ID)).thenReturn(studentCommentsMap);
        when(attendanceRepository.getAttendanceCommentsBySectionId(SECTION_2_ID)).thenReturn(studentCommentsMap2);
        when(submissionWriter.gradeMultipleSubmissionsBySection(any())).thenReturn(progressOptional);


        List<Error> errorList = assignmentSubmitter.submitCourseAttendances(true, attendanceSummaryModelList);
        Assert.assertTrue(errorList.isEmpty());
    }


    @Test
    public void submitCourseAttendancesAttendanceAssignmentValidationError() throws IOException {
        attendanceAssignment.setCanvasAssignmentId(null);

        when(assignmentService.findBySectionId(SECTION_ID)).thenReturn(attendanceAssignment);
        when(canvasAssignmentAssistant.createAssignmentInCanvas(COURSE_ID, attendanceAssignment, oauthToken)).thenReturn(error);

        List<Error> errorList = assignmentSubmitter.submitCourseAttendances(true, attendanceSummaryModelList);
        Assert.assertFalse(errorList.isEmpty());
        Assert.assertEquals("Expected to return the error", error, errorList.get(0));
    }

    @Test
    public void submitCourseAttendancesCanvasAssignmentValidationError() throws IOException {
        assignmentOptional.get().setPointsPossible(120.0);

        when(assignmentService.findBySectionId(SECTION_ID)).thenReturn(attendanceAssignment);
        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, CANVAS_ASSIGNMENT_ID+"")).thenReturn(assignmentOptional);
        when(canvasAssignmentAssistant.editAssignmentInCanvas(COURSE_ID, attendanceAssignment, oauthToken)).thenReturn(error);

        List<Error> errorList = assignmentSubmitter.submitCourseAttendances(true, attendanceSummaryModelList);
        Assert.assertFalse(errorList.isEmpty());
        Assert.assertEquals("Expected to return the error", error, errorList.get(0));
    }

    @Test
    public void submitCourseAttendancesUnableToPushGradesForSectionError() throws IOException {
        progressOptional.get().setWorkflowState("failed");

        when(assignmentService.findBySectionId(SECTION_ID)).thenReturn(attendanceAssignment);
        when(assignmentService.findBySectionId(SECTION_2_ID)).thenReturn(attendanceAssignment2);
        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, CANVAS_ASSIGNMENT_ID+"")).thenReturn(assignmentOptional);
        when(courseRepository.findByCanvasCourseId(eq(COURSE_ID))).thenReturn(course);
        when(attendanceRepository.getAttendanceCommentsBySectionId(SECTION_ID)).thenReturn(studentCommentsMap);
        when(attendanceRepository.getAttendanceCommentsBySectionId(SECTION_2_ID)).thenReturn(studentCommentsMap2);
        when(submissionWriter.gradeMultipleSubmissionsBySection(any())).thenReturn(progressOptional);

        List<Error> errorList = assignmentSubmitter.submitCourseAttendances(true, attendanceSummaryModelList);
        Assert.assertFalse(errorList.isEmpty());
        Assert.assertEquals("Expected to return the error", error2.getMessage(), errorList.get(0).getMessage());
    }



}
