package edu.ksu.canvas.attendance.submitter;


import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import edu.ksu.canvas.attendance.form.CourseConfigurationForm;
import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.repository.AttendanceCourseRepository;
import edu.ksu.canvas.attendance.repository.AttendanceRepository;
import edu.ksu.canvas.attendance.services.AttendanceAssignmentService;
import edu.ksu.canvas.attendance.services.CanvasApiWrapperService;
import edu.ksu.canvas.model.Progress;
import edu.ksu.canvas.oauth.OauthToken;
import edu.ksu.canvas.requestOptions.MultpleSubmissionsOptions;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class AssignmentSubmitter {
    private static final Logger LOG = Logger.getLogger(AssignmentSubmitter.class);

    private AttendanceAssignmentService assignmentService;
    //TODO: DONT USE REPOSITORIES AND SERVICES, USE THEIR SERVICES (CALL IN SERVICES IF NEEDED)
    private AttendanceCourseRepository courseRepository;
    private AttendanceRepository attendanceRepository;
    private OauthToken adminOauthToken;
    private CanvasApiWrapperService canvasApiWrapper;
    private Long courseId;
    private AssignmentValidator assignmentValidator;
    private CanvasAssignmentAssistant canvasAssignmentAssistant;
    //TODO: CHANGE FOR THE MODEL WHEN CHANGED
    private CourseConfigurationForm courseConfigurationForm;



    public AssignmentSubmitter(AttendanceAssignmentService assignmentService, AttendanceCourseRepository courseRepository,
                               AttendanceRepository attendanceRepository, CanvasApiWrapperService canvasApiWrapper,
                               Long courseId, OauthToken adminOauthToken, CourseConfigurationForm courseConfigurationForm) {
        this.assignmentService = assignmentService;
        this.courseRepository = courseRepository;
        this.attendanceRepository = attendanceRepository;
        this.canvasApiWrapper = canvasApiWrapper;
        this.courseId = courseId;
        this.adminOauthToken = adminOauthToken;
        this.courseConfigurationForm = courseConfigurationForm;
        this.assignmentValidator = new AssignmentValidator();
        this.canvasAssignmentAssistant = new CanvasAssignmentAssistant();
    }

    /**
     * This function takes the summary for sections and one by one validates the attendance assignment and
     * the canvas assignment. Then pushes all the grades and comments to the canvas assignment associated.
     * @param isSimpleAttendance
     * @param summaryForSections
     * @return List<Error> this errors will be displayed to the user.
     */
    public List<Error> submitCourseAttendances(boolean isSimpleAttendance, List<AttendanceSummaryModel> summaryForSections) {
        List<Error> errorList = new ArrayList<>();

        for (AttendanceSummaryModel model : summaryForSections) {

            //TODO: Add form validation asking to save for the first time

            AttendanceAssignment attendanceAssignment = assignmentService.findBySectionId(model.getSectionId());

            Error validationError = assignmentValidator.validateAttendanceAssignment(courseId, attendanceAssignment, canvasApiWrapper, adminOauthToken);
            if (handleAttendanceAssignmentValidationError(errorList, attendanceAssignment, validationError))
                return errorList;

            Error canvasAssignmentValidationError = assignmentValidator.validateCanvasAssignment(courseConfigurationForm, courseId, attendanceAssignment, canvasApiWrapper, adminOauthToken);
            if (handleCanvasAssignmentValidationError(errorList, attendanceAssignment, canvasAssignmentValidationError))
                return errorList;


            submitSectionAttendances(isSimpleAttendance, errorList, model, attendanceAssignment);
        }

        return errorList;
    }

    /**
     * Handles the push grades and/or comments to canvas for just one section.
     */
    private void submitSectionAttendances(boolean isSimpleAttendance, List<Error> errorList, AttendanceSummaryModel model, AttendanceAssignment attendanceAssignment) {
        Map<Integer, MultpleSubmissionsOptions.StudentSubmissionOption> studentMap;
        MultpleSubmissionsOptions submissionOptions;
        studentMap = new HashMap<>();
        submissionOptions = new MultpleSubmissionsOptions(((Long) model.getSectionId()).toString(), attendanceAssignment.getAssignmentId().intValue(), null);

        AttendanceCourse course = courseRepository.findByCanvasCourseId(courseId);

        Map<Long, String> studentCommentsMap = new HashMap<>();
        if (course.getShowNotesToStudents()) {
            studentCommentsMap = attendanceRepository.getAttendanceCommentsBySectionId(model.getSectionId());
        }

        for (AttendanceSummaryModel.Entry entry : model.getEntries()) {

            studentMap.put(((Long) entry.getStudentId()).intValue(),
                    generateStudentSubmissionOptions(isSimpleAttendance, submissionOptions, attendanceAssignment, course, studentCommentsMap, entry));
        }

        Optional<Progress> returnedProgress = null;
        submissionOptions.setStudentSubmissionOptionMap(studentMap);
        try {
            returnedProgress = canvasApiWrapper.gradeMultipleSubmissionsBySection(adminOauthToken, submissionOptions);
        } catch (IOException e) {
            LOG.error("Error while pushing the grades of section: " + model.getSectionId(), e);
            errorList.add(new Error("Could not push grades of section: " + model.getSectionId()));
        }

        if (returnedProgress == null || !isValidProgress(returnedProgress)) {
            LOG.error("Error object returned while pushing the grades of section: " + model.getSectionId());
            errorList.add(new Error("Could not push grades of section: " + model.getSectionId()));
        }
    }

    /**
     * If in validation is determined discrepancy in the data between canvas and teh assignment in the data base,
     * canvas will be aligned to the data in the db.
     */
    private boolean handleCanvasAssignmentValidationError(List<Error> errorList, AttendanceAssignment attendanceAssignment, Error canvasAssignmentValidationError) {
        if(canvasAssignmentValidationError != null) {
            if(canvasAssignmentValidationError.getMessage().equals("DISCREPANCY BETWEEN CANVAS AND DATABASE")) {
                Error canvasAssignmentError = canvasAssignmentAssistant.editAssignmentInCanvas(courseId, attendanceAssignment, adminOauthToken);
                if (canvasAssignmentError != null) {
                    errorList.add(canvasAssignmentError);
                    return true;
                }
            } else {
                errorList.add(canvasAssignmentValidationError);
                return true;
            }
        }
        return false;
    }

    /**
     * If in validation is determined that there is not canvas assignment associated to the attendance assignment
     * then a new canvas assignment will be created and associate to the attendance assignment.
     */
    private boolean handleAttendanceAssignmentValidationError(List<Error> errorList, AttendanceAssignment attendanceAssignment, Error validationError) {
        if (validationError != null) {
            if (validationError.getMessage().equals("NO CANVAS ASSIGNMENT LINKED")) {
                Error canvasAssignmentError = canvasAssignmentAssistant.createAssignmentInCanvas(courseId, attendanceAssignment, adminOauthToken);
                if (canvasAssignmentError != null) {
                    errorList.add(canvasAssignmentError);
                    return true;
                }
            } else {
                errorList.add(validationError);
                return true;
            }
        }
        return false;
    }

    /**
     * Generates the parameters and data needed for push grades or comments to canvas
     */
    private MultpleSubmissionsOptions.StudentSubmissionOption generateStudentSubmissionOptions(boolean isSimpleAttendance, MultpleSubmissionsOptions submissionOptions,
                                                                                               AttendanceAssignment attendanceAssignment, AttendanceCourse course, Map<Long, String> studentCommentsMap, AttendanceSummaryModel.Entry entry) {

        Double grade = calculateStudentGrade(attendanceAssignment, entry, course, isSimpleAttendance);
        String comments = getCommentHeader(entry, course, isSimpleAttendance);

        if (course.getShowNotesToStudents()) {
            comments = comments.concat(studentCommentsMap.get(entry.getStudentId()));
        }

        return submissionOptions.createStudentSubmissionOption(comments, grade.toString(), null, null, null, null);
    }

    private boolean isValidProgress(Optional<Progress> returnedProgress) {
        return (returnedProgress.isPresent() && !returnedProgress.get().getWorkflowState().equals("failed"));
    }

    /**
     * Generates the header part of the comments, which is an explanation of the grades.
     */
    private String getCommentHeader(AttendanceSummaryModel.Entry entry, AttendanceCourse course, boolean isSimpleAttendance) {
        if (isSimpleAttendance) {
            return "Total number of classes: " + getTotalSimpleClasses(entry) + "\nNumber of classes present: " + entry.getTotalClassesPresent()
                    + "\nNumber of classes tardy: " + entry.getTotalClassesTardy() + "\nNumber of classes absent: " + entry.getTotalClassesMissed()
                    + "\nNumber of classes excused: " + entry.getTotalClassesExcused() + "\n";
        } else {
            return "Total number of minutes: " + course.getTotalMinutes() + "\nTotal minutes misseds: " + entry.getSumMinutesMissed()
                    + "\nNumber of minutes made up: " + entry.getSumMinutesMadeup() + "\nNumber of minutes remaining to be made up: "
                    + entry.getRemainingMinutesMadeup() + "\n";
        }
    }

    /**
     * Calculates the grade of one student.
     */
    private double calculateStudentGrade(AttendanceAssignment attendanceAssignment, AttendanceSummaryModel.Entry entry, AttendanceCourse course, boolean isSimpleAttendance) {
        if (isSimpleAttendance) {
            int totalClasses = getTotalSimpleClasses(entry);
            return ((entry.getTotalClassesPresent() * (attendanceAssignment.getPresentPoints() / 100) +
                    entry.getTotalClassesTardy() * (attendanceAssignment.getTardyPoints() / 100) +
                    entry.getTotalClassesExcused() * (attendanceAssignment.getExcusedPoints() / 100) +
                    entry.getTotalClassesMissed() * (attendanceAssignment.getAbsentPoints() / 100)) / totalClasses) * attendanceAssignment.getAssignmentPoints();
        } else {
            return ((course.getTotalMinutes() - entry.getRemainingMinutesMadeup()) / course.getTotalMinutes()) * attendanceAssignment.getAssignmentPoints();
        }
    }

    private int getTotalSimpleClasses(AttendanceSummaryModel.Entry entry) {
        return entry.getTotalClassesExcused() + entry.getTotalClassesMissed() + entry.getTotalClassesTardy() + entry.getTotalClassesPresent();
    }

}