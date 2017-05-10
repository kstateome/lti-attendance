package edu.ksu.canvas.attendance.submitter;


import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import edu.ksu.canvas.attendance.form.CourseConfigurationForm;
import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.services.*;
import edu.ksu.canvas.model.Progress;
import edu.ksu.canvas.oauth.OauthToken;
import edu.ksu.canvas.requestOptions.MultipleSubmissionsOptions;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@Scope(value="session")
public class AssignmentSubmitter {
    private static final Logger LOG = Logger.getLogger(AssignmentSubmitter.class);

    @Autowired
    private AttendanceAssignmentService assignmentService;

    @Autowired
    private AttendanceSectionService sectionService;

    @Autowired
    private AttendanceCourseService attendanceCourseService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private CanvasApiWrapperService canvasApiWrapperService;

    @Autowired
    private AssignmentValidator assignmentValidator;

    @Autowired
    private CanvasAssignmentAssistant canvasAssignmentAssistant;


    /**
     * This function takes the summary for sections and one by one validates the attendance assignment and
     * the canvas assignment. Then pushes all the grades and comments to the canvas assignment associated.
     * @param isSimpleAttendance
     * @param summaryForSections
     * @return List<Error> this errors will be displayed to the user.
     */
    public List<Error> submitCourseAttendances(boolean isSimpleAttendance, List<AttendanceSummaryModel> summaryForSections, Long courseId, OauthToken oauthToken, CourseConfigurationForm courseConfigurationForm) {
        List<Error> errorList = new ArrayList<>();

        for (AttendanceSummaryModel model : summaryForSections) {

            //Handles errors by section before handling the pushing of said section
            AttendanceAssignment attendanceAssignment = assignmentService.findBySection(sectionService.getSectionInListById(courseId, model.getSectionId()));
            if (checkConfigurationSetup(errorList, model, attendanceAssignment)) {
                return errorList;
            }

            Error validationError = assignmentValidator.validateAttendanceAssignment(courseId, attendanceAssignment, canvasApiWrapperService, oauthToken);
            if (handleAttendanceAssignmentValidationError(errorList, attendanceAssignment, validationError, courseId, oauthToken)) {
                return errorList;
            }

            Error canvasAssignmentValidationError = assignmentValidator.validateCanvasAssignment(courseConfigurationForm, courseId, attendanceAssignment, canvasApiWrapperService, oauthToken);
            if (handleCanvasAssignmentValidationError(errorList, attendanceAssignment, canvasAssignmentValidationError, courseId, oauthToken)) {
                return errorList;
            }

            submitSectionAttendances(isSimpleAttendance, errorList, model, attendanceAssignment, courseId, oauthToken);
        }

        return errorList;
    }

    /**
     * Handles the push grades and/or comments to canvas for just one section.
     */
    private void submitSectionAttendances(boolean isSimpleAttendance, List<Error> errorList, AttendanceSummaryModel model, AttendanceAssignment attendanceAssignment, Long courseId, OauthToken oauthToken) {
        Map<String, MultipleSubmissionsOptions.StudentSubmissionOption> studentMap;
        MultipleSubmissionsOptions submissionOptions;
        studentMap = new HashMap<>();
        submissionOptions = new MultipleSubmissionsOptions(((Long) model.getSectionId()).toString(), attendanceAssignment.getCanvasAssignmentId().intValue(), null);

        AttendanceCourse course = attendanceCourseService.findByCanvasCourseId(courseId);

        Map<Long, String> studentCommentsMap = new HashMap<>();
        if (course.getShowNotesToStudents()) {
            studentCommentsMap = attendanceService.getAttendanceCommentsBySectionId(model.getSectionId());
        }

        //Generates the map with the information to be submitted to Canvas
        for (AttendanceSummaryModel.Entry entry : model.getEntries()) {
            studentMap.put(Long.toString(entry.getCanvasStudentId()),
                    generateStudentSubmissionOptions(isSimpleAttendance, submissionOptions, attendanceAssignment, course, studentCommentsMap, entry));
        }

        //Pushing the information to Canvas
        Optional<Progress> returnedProgress = Optional.empty();
        submissionOptions.setStudentSubmissionOptionMap(studentMap);
        try {
            returnedProgress = canvasApiWrapperService.gradeMultipleSubmissionsBySection(oauthToken, submissionOptions);
        } catch (IOException e) {
            LOG.error("Error while pushing the grades of section: " + model.getSectionId(), e);
            errorList.add(new Error("Could not push grades of section: " + model.getSectionId()));
        }

        if (!isValidProgress(returnedProgress)) {
            LOG.debug("Error object returned while pushing the grades of section: " + model.getSectionId());
            errorList.add(new Error("Could not push grades of section: " + model.getSectionId()));
        }
    }

    /**
     * If in validation is determined discrepancy in the data between canvas and teh assignment in the data base,
     * canvas will be aligned to the data in the db.
     */
    private boolean handleCanvasAssignmentValidationError(List<Error> errorList, AttendanceAssignment attendanceAssignment, Error canvasAssignmentValidationError, Long courseId, OauthToken oauthToken) {
        if(canvasAssignmentValidationError != null) {
            if(canvasAssignmentValidationError.getMessage().equals("DISCREPANCY BETWEEN CANVAS AND DATABASE")) {
                Error canvasAssignmentError = canvasAssignmentAssistant.editAssignmentInCanvas(courseId, attendanceAssignment, oauthToken);
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
     * Checks if the configuration setup have been saved before pushing. Returns error if not.
     */
    private boolean checkConfigurationSetup(List<Error> errorList, AttendanceSummaryModel model, AttendanceAssignment attendanceAssignment) {
        if (attendanceAssignment == null || (attendanceAssignment.getAssignmentName() == null && attendanceAssignment.getAssignmentPoints() == null)) {
            LOG.info("There is no Attendance Assignment associated to section " + model.getSectionId());
            errorList.add(new Error("Please save configuration setup for the assignment before pushing grades to Canvas."));
            return true;
        }
        return false;
    }


    /**
     * If in validation is determined that there is not canvas assignment associated to the attendance assignment
     * then a new canvas assignment will be created and associate to the attendance assignment.
     */
    private boolean handleAttendanceAssignmentValidationError(List<Error> errorList, AttendanceAssignment attendanceAssignment, Error validationError, Long courseId, OauthToken oauthToken) {
        if (validationError != null) {
            if (validationError.getMessage().equals("NO CANVAS ASSIGNMENT LINKED")) {
                Error canvasAssignmentError = canvasAssignmentAssistant.createAssignmentInCanvas(courseId, attendanceAssignment, oauthToken);
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
    private MultipleSubmissionsOptions.StudentSubmissionOption generateStudentSubmissionOptions(boolean isSimpleAttendance, MultipleSubmissionsOptions submissionOptions,
                                                                                               AttendanceAssignment attendanceAssignment, AttendanceCourse course, Map<Long, String> studentCommentsMap, AttendanceSummaryModel.Entry entry) {

        Double grade = calculateStudentGrade(attendanceAssignment, entry, isSimpleAttendance);
        StringBuilder comments = new StringBuilder(getCommentHeader(entry, course, isSimpleAttendance));

        if (course.getShowNotesToStudents() && studentCommentsMap.keySet().contains(entry.getStudentId())) {
            comments.append("\nAdditional Comments: \n");
            comments.append(studentCommentsMap.get(entry.getStudentId()));
        }

        return submissionOptions.createStudentSubmissionOption(comments.toString(), grade.toString(), null, null, null, null);
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
            return "Total number of minutes: " + course.getTotalMinutes() + "\nTotal minutes missed: " + entry.getSumMinutesMissed()
                    + "\nNumber of minutes made up: " + entry.getSumMinutesMadeup() + "\nNumber of minutes remaining to be made up: "
                    + entry.getRemainingMinutesMadeup() + "\n";
        }
    }

    /**
     * Calculates the grade of one student.
     */
    private double calculateStudentGrade(AttendanceAssignment attendanceAssignment, AttendanceSummaryModel.Entry entry, boolean isSimpleAttendance) {
        if (isSimpleAttendance) {
            int totalClasses = getTotalSimpleClasses(entry);
            totalClasses = totalClasses == 0? 1 : totalClasses;
            return ((entry.getTotalClassesPresent() * (attendanceAssignment.getPresentPoints() / 100) +
                    entry.getTotalClassesTardy() * (attendanceAssignment.getTardyPoints() / 100) +
                    entry.getTotalClassesExcused() * (attendanceAssignment.getExcusedPoints() / 100) +
                    entry.getTotalClassesMissed() * (attendanceAssignment.getAbsentPoints() / 100)) / totalClasses) * attendanceAssignment.getAssignmentPoints();
        } else {

            return ((100 - entry.getPercentCourseMissed()) / 100) * attendanceAssignment.getAssignmentPoints();
        }
    }

    private int getTotalSimpleClasses(AttendanceSummaryModel.Entry entry) {
        return entry.getTotalClassesExcused() + entry.getTotalClassesMissed() + entry.getTotalClassesTardy() + entry.getTotalClassesPresent();
    }

}