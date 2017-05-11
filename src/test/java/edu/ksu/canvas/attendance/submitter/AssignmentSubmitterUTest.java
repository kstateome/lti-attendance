package edu.ksu.canvas.attendance.submitter;

import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.enums.AttendanceType;
import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.services.*;
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
    private static final Long SECTION_1_ID = 111111111111L;
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
    private static final Long CANVAS_STUDENT_ID_1 = 1111L;
    private static final Long CANVAS_STUDENT_ID_2 = 2222L;
    private static final Long CANVAS_STUDENT_ID_3 = 3333L;
    private static final Long CANVAS_STUDENT_ID_4 = 4444L;
    private static final String OAUTH_STRING = "sdfsdfSDFSDFsdfsdFSDFsdfSDFSDgfsdSFDFSDF";
    private static final long ASSIGNMENT_ID = 8484848484L;
    private static final long ASSIGNMENT_ID_2 = 77777778484L;
    private static final String ASSIGNMENT_NAME = "NAME OF THE ASSIGNMENT";
    private static final String ASSIGNMENT_NAME_2 = "NAME OF THE SECOND ASSIGNMENT";
    private static final Double ASSIGNMENT_POINTS = 100.0;
    private static final long CANVAS_ASSIGNMENT_ID = 514514514L;
    private static final int PROGRESS_ID = 19781971;
    private static final String SECTION_1_NAME = "SECTION NUMBER 1";
    private static final String SECTION_2_NAME = "SECTION NUMBER 2";

    private AssignmentSubmitter assignmentSubmitter;

    @Mock
    private AttendanceAssignmentService assignmentService;

    @Mock
    private AttendanceSectionService sectionService;

    @Mock
    private AttendanceCourseService attendanceCourseService;

    @Mock
    private AttendanceService attendanceService;

    @Mock
    private CanvasApiWrapperService canvasApiWrapperService;

    @Mock
    private AssignmentValidator assignmentValidator;

    @Mock
    private CanvasAssignmentAssistant canvasAssignmentAssistant;



    private NonRefreshableOauthToken oauthToken;
    private AttendanceSummaryModel attendanceSummaryModel1;
    private AttendanceSummaryModel attendanceSummaryModel2;
    private List<AttendanceSummaryModel> attendanceSummaryModelList;
    private AttendanceAssignment attendanceAssignment1;
    private AttendanceAssignment attendanceAssignment2;
    private Optional<Assignment> assignmentOptional;
    private Assignment assignment;
    private AttendanceCourse course;
    private Map<Long, String> studentCommentsMap1;
    private Map<Long, String> studentCommentsMap2;
    private Optional<Progress> progressOptional;
    private Progress progress;
    private AttendanceAssignment assignmentConfigurationFromSetup;
    private  Error error;
    private Error error2;
    private AttendanceSection section1;
    private AttendanceSection section2;

    @Before
    public void setup() {

        assignmentSubmitter = new AssignmentSubmitter();

        oauthToken = new NonRefreshableOauthToken(OAUTH_STRING);
        attendanceSummaryModel1 = new AttendanceSummaryModel(SECTION_1_ID);
        AttendanceSummaryModel.Entry entry1 = new AttendanceSummaryModel.Entry(COURSE_ID, SECTION_1_ID, STUDENT_1_ID, CANVAS_STUDENT_ID_1, STUDENT_1_NAME, false, 0, 1, 0, 3);
        AttendanceSummaryModel.Entry entry2 = new AttendanceSummaryModel.Entry(COURSE_ID, SECTION_1_ID, STUDENT_2_ID, CANVAS_STUDENT_ID_2, STUDENT_2_NAME, false, 0, 2, 0, 2);
        attendanceSummaryModel1.add(entry1);
        attendanceSummaryModel1.add(entry2);

        attendanceSummaryModel2 = new AttendanceSummaryModel(SECTION_2_ID);
        AttendanceSummaryModel.Entry entry3 = new AttendanceSummaryModel.Entry(COURSE_ID, SECTION_1_ID, STUDENT_3_ID, CANVAS_STUDENT_ID_3, STUDENT_3_NAME, false, 0, 3, 0, 1);
        AttendanceSummaryModel.Entry entry4 = new AttendanceSummaryModel.Entry(COURSE_ID, SECTION_1_ID, STUDENT_4_ID, CANVAS_STUDENT_ID_4, STUDENT_4_NAME, false, 0, 0, 0, 4);
        attendanceSummaryModel2.add(entry3);
        attendanceSummaryModel2.add(entry4);

        attendanceSummaryModelList = new ArrayList<>();
        attendanceSummaryModelList.add(attendanceSummaryModel1);
        attendanceSummaryModelList.add(attendanceSummaryModel2);

        assignmentConfigurationFromSetup = new AttendanceAssignment();
        assignmentConfigurationFromSetup.setAbsentPoints(0.0);
        assignmentConfigurationFromSetup.setAssignmentPoints(ASSIGNMENT_POINTS);
        assignmentConfigurationFromSetup.setExcusedPoints(0.0);
        assignmentConfigurationFromSetup.setPresentPoints(100.0);
        assignmentConfigurationFromSetup.setTardyPoints(0.0);

        attendanceAssignment1 = new AttendanceAssignment();
        attendanceAssignment1.setAssignmentId(ASSIGNMENT_ID);
        attendanceAssignment1.setAssignmentName(ASSIGNMENT_NAME);
        attendanceAssignment1.setCanvasAssignmentId(CANVAS_ASSIGNMENT_ID);
        attendanceAssignment1.setGradingOn(true);
        attendanceAssignment1.setAssignmentPoints(ASSIGNMENT_POINTS);
        attendanceAssignment1.setAbsentPoints(0.0);
        attendanceAssignment1.setExcusedPoints(0.0);
        attendanceAssignment1.setTardyPoints(0.0);
        attendanceAssignment1.setPresentPoints(100.0);

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

        section1 = new AttendanceSection();
        section1.setSectionId(SECTION_1_ID);
        section1.setCanvasCourseId(COURSE_ID);
        section1.setCanvasSectionId(SECTION_1_ID);
        section1.setName(SECTION_1_NAME);

        section2 = new AttendanceSection();
        section2.setSectionId(SECTION_2_ID);
        section2.setCanvasCourseId(COURSE_ID);
        section2.setCanvasSectionId(SECTION_2_ID);
        section2.setName(SECTION_2_NAME);

        studentCommentsMap1 = new HashMap<>();
        studentCommentsMap1.put(STUDENT_1_ID, STUDENT_1_COMMENT);
        studentCommentsMap1.put(STUDENT_2_ID, STUDENT_2_COMMENT);

        studentCommentsMap2 = new HashMap<>();
        studentCommentsMap2.put(STUDENT_3_ID, STUDENT_3_COMMENT);
        studentCommentsMap2.put(STUDENT_4_ID, STUDENT_4_COMMENT);

        progress = new Progress();
        progress.setId(PROGRESS_ID);
        progress.setUrl(CANVAS_URL);
        progress.setWorkflowState("queued");
        progressOptional = Optional.of(progress);

        error = new Error("NO CANVAS ASSIGNMENT LINKED");

        Whitebox.setInternalState(assignmentSubmitter, "canvasAssignmentAssistant", canvasAssignmentAssistant);
        Whitebox.setInternalState(assignmentSubmitter, "assignmentService", assignmentService);
        Whitebox.setInternalState(assignmentSubmitter, "sectionService", sectionService);
        Whitebox.setInternalState(assignmentSubmitter, "attendanceCourseService", attendanceCourseService);
        Whitebox.setInternalState(assignmentSubmitter, "attendanceService", attendanceService);
        Whitebox.setInternalState(assignmentSubmitter, "canvasApiWrapperService", canvasApiWrapperService);
        Whitebox.setInternalState(assignmentSubmitter, "assignmentValidator", assignmentValidator);


        error2 = new Error("Could not push grades of section: " + SECTION_1_ID);

    }

    @Test
    public void submitCourseAttendancesHappyPath() throws IOException {
        when(sectionService.getSectionInListById(COURSE_ID, SECTION_1_ID)).thenReturn(section1);
        when(sectionService.getSectionInListById(COURSE_ID, SECTION_2_ID)).thenReturn(section2);
        when(assignmentService.findBySection(section1)).thenReturn(attendanceAssignment1);
        when(assignmentService.findBySection(section2)).thenReturn(attendanceAssignment2);
        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, CANVAS_ASSIGNMENT_ID + "")).thenReturn(assignmentOptional);
        when(attendanceCourseService.findByCanvasCourseId(eq(COURSE_ID))).thenReturn(course);
        when(attendanceService.getAttendanceCommentsBySectionId(SECTION_1_ID)).thenReturn(studentCommentsMap1);
        when(attendanceService.getAttendanceCommentsBySectionId(SECTION_2_ID)).thenReturn(studentCommentsMap2);
        when(canvasApiWrapperService.gradeMultipleSubmissionsBySection(any(), any())).thenReturn(progressOptional);

        List<Error> errorList = assignmentSubmitter.submitCourseAttendances(true, attendanceSummaryModelList, COURSE_ID, oauthToken, assignmentConfigurationFromSetup);
        Assert.assertTrue(errorList.isEmpty());
    }

    @Test
    public void submitCourseAttendancesUnsavedConfigurationError() throws IOException {
        error = new Error("Please save configuration setup for the assignment before pushing grades to Canvas.");
        attendanceAssignment1.setAssignmentPoints(null);
        attendanceAssignment1.setAssignmentName(null);

        when(sectionService.getSectionInListById(COURSE_ID, SECTION_1_ID)).thenReturn(section1);
        when(assignmentService.findBySection(section1)).thenReturn(attendanceAssignment1);

        List<Error> errorList = assignmentSubmitter.submitCourseAttendances(true, attendanceSummaryModelList, COURSE_ID, oauthToken, assignmentConfigurationFromSetup);
        Assert.assertFalse(errorList.isEmpty());
        Assert.assertEquals("Expected to return the error", error.getMessage(), errorList.get(0).getMessage());
    }


    @Test
    public void submitCourseAttendancesAttendanceAssignmentValidationError() throws IOException {
        attendanceAssignment1.setCanvasAssignmentId(null);

        when(assignmentValidator.validateAttendanceAssignment(COURSE_ID, attendanceAssignment1, canvasApiWrapperService, oauthToken)).thenReturn(error);
        when(assignmentService.findBySection(any())).thenReturn(attendanceAssignment1);
        when(canvasAssignmentAssistant.createAssignmentInCanvas(COURSE_ID, attendanceAssignment1, oauthToken)).thenReturn(error);

        List<Error> errorList = assignmentSubmitter.submitCourseAttendances(true, attendanceSummaryModelList, COURSE_ID, oauthToken, assignmentConfigurationFromSetup);
        Assert.assertFalse(errorList.isEmpty());
        Assert.assertEquals("Expected to return the error", error.getMessage(), errorList.get(0).getMessage());
    }

    @Test
    public void submitCourseAttendancesCanvasAssignmentValidationError() throws IOException {
        error = new Error("Assignment configuration needs to be saved before pushing to Canvas");

        when(assignmentService.findBySection(any())).thenReturn(attendanceAssignment1);
        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, CANVAS_ASSIGNMENT_ID + "")).thenReturn(assignmentOptional);
        when(canvasAssignmentAssistant.editAssignmentInCanvas(COURSE_ID, attendanceAssignment1, oauthToken)).thenReturn(error);
        when(assignmentValidator.validateCanvasAssignment(assignmentConfigurationFromSetup, COURSE_ID, attendanceAssignment1, canvasApiWrapperService, oauthToken)).thenReturn(error);

        List<Error> errorList = assignmentSubmitter.submitCourseAttendances(true, attendanceSummaryModelList, COURSE_ID, oauthToken, assignmentConfigurationFromSetup);
        Assert.assertFalse(errorList.isEmpty());
        Assert.assertEquals("Expected to return the error", error.getMessage(), errorList.get(0).getMessage());
    }

    @Test
    public void submitCourseAttendancesUnableToPushGradesForSectionError() throws IOException {
        progressOptional.get().setWorkflowState("failed");

        when(assignmentService.findBySection(any())).thenReturn(attendanceAssignment1);
        when(canvasApiWrapperService.getSingleAssignment(COURSE_ID, oauthToken, CANVAS_ASSIGNMENT_ID+"")).thenReturn(assignmentOptional);
        when(attendanceCourseService.findByCanvasCourseId(eq(COURSE_ID))).thenReturn(course);
        when(attendanceService.getAttendanceCommentsBySectionId(SECTION_1_ID)).thenReturn(studentCommentsMap1);
        when(attendanceService.getAttendanceCommentsBySectionId(SECTION_2_ID)).thenReturn(studentCommentsMap2);
        when(canvasApiWrapperService.gradeMultipleSubmissionsBySection(any(), any())).thenReturn(progressOptional);

        List<Error> errorList = assignmentSubmitter.submitCourseAttendances(true, attendanceSummaryModelList, COURSE_ID, oauthToken, assignmentConfigurationFromSetup);
        Assert.assertFalse(errorList.isEmpty());
        Assert.assertEquals("Expected to return the error", error2.getMessage(), errorList.get(0).getMessage());
    }



}
