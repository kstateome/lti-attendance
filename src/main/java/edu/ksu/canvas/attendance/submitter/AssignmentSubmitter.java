package edu.ksu.canvas.attendance.submitter;


import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import edu.ksu.canvas.attendance.exception.AttendanceAssignmentException;
import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.repository.AttendanceStudentRepository;
import edu.ksu.canvas.attendance.services.*;
import edu.ksu.canvas.model.Progress;
import edu.ksu.canvas.oauth.OauthToken;
import edu.ksu.canvas.requestOptions.MultipleSubmissionsOptions;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.*;

@Component
@Scope(value="session")
public class AssignmentSubmitter {
    private static final Logger LOG = LogManager.getLogger(AssignmentSubmitter.class);

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

    @Autowired
    private AttendanceStudentService studentService;

    @Autowired
    private AttendanceStudentRepository studentRepository;

    /**
     * This function takes the summary for sections and one by one validates the attendance assignment and
     * the canvas assignment. Then pushes all the grades and comments to the canvas assignment associated.
     * @param isSimpleAttendance
     * @param summaryForSections
     */
    public void submitCourseAttendances(boolean isSimpleAttendance, List<AttendanceSummaryModel> summaryForSections, Long courseId,
                                        OauthToken oauthToken, AttendanceAssignment assignmentConfigurationFromSetup) throws AttendanceAssignmentException{

        AttendanceAssignment attendanceAssignment = assignmentService.findBySection(sectionService.getFirstSectionOfCourse(courseId));

        gradePushingValidation(courseId, oauthToken, assignmentConfigurationFromSetup, attendanceAssignment);

        List<AttendanceStudent> allStudents = studentRepository.findByCanvasCourseId(courseId);

        List<AttendanceStudent> studentsToGrade = new ArrayList<>();

        Set<String> idList = new HashSet<>();

        for (AttendanceStudent student: allStudents){
            idList.add(student.getSisUserId());
        }

        for (String id: idList){
            List<AttendanceStudent> attendanceStudentList = studentService.getStudentByCourseAndSisId(id, courseId);
            attendanceStudentList.stream().filter(x -> !x.getDeleted())
                .findFirst()
                .ifPresent(studentsToGrade::add);
        }

        submitSectionAttendances(isSimpleAttendance, summaryForSections, studentsToGrade, attendanceAssignment, courseId, oauthToken);
    }

    /**
     * If in validation is determined discrepancy in the data between canvas and teh assignment in the data base,
     * canvas will be aligned to the data in the db. If in validation is determined that there is not canvas assignment
     * associated to the attendance assignment then a new canvas assignment will be created and associate to the attendance assignment.
     */
    private void gradePushingValidation(Long courseId, OauthToken oauthToken, AttendanceAssignment assignmentConfigurationFromSetup,
                                        AttendanceAssignment attendanceAssignment) throws AttendanceAssignmentException{

        AttendanceAssignment validatingAssignment = assignmentValidator.validateConfigurationSetupExistence(attendanceAssignment);

        if (validatingAssignment.getStatus() == AttendanceAssignment.Status.UNKNOWN){
            validatingAssignment = assignmentValidator.validateAttendanceAssignment(courseId, validatingAssignment, canvasApiWrapperService, oauthToken);
        }
        if (validatingAssignment.getStatus() == AttendanceAssignment.Status.UNKNOWN){
            validatingAssignment = assignmentValidator.validateCanvasAssignment(assignmentConfigurationFromSetup, courseId, validatingAssignment, canvasApiWrapperService, oauthToken);
        }
        if (validatingAssignment.getStatus() == AttendanceAssignment.Status.CANVAS_AND_DB_DISCREPANCY){
            canvasAssignmentAssistant.editAssignmentInCanvas(courseId, validatingAssignment, oauthToken);
        }
        else if (validatingAssignment.getStatus() == AttendanceAssignment.Status.NOT_LINKED_TO_CANVAS){
            canvasAssignmentAssistant.createAssignmentInCanvas(courseId, validatingAssignment, oauthToken);
        }
        validatingAssignment.setStatus(AttendanceAssignment.Status.OKAY);

    }

    /**
     * Handles the push grades and/or comments to canvas for just one section.
     */
    private void submitSectionAttendances(boolean isSimpleAttendance, List<AttendanceSummaryModel> summaryForSections, List<AttendanceStudent> students, AttendanceAssignment attendanceAssignment, Long courseId, OauthToken oauthToken) throws AttendanceAssignmentException {

        Map<String, MultipleSubmissionsOptions.StudentSubmissionOption> studentMap;
        MultipleSubmissionsOptions submissionOptions = new MultipleSubmissionsOptions(courseId.toString(), attendanceAssignment.getCanvasAssignmentId(), null);
        studentMap = new HashMap<>();

        for (AttendanceStudent student: students){

            AttendanceCourse course = attendanceCourseService.findByCanvasCourseId(courseId);

            Map<Long, String> studentCommentsMap = new HashMap<>();
            if (course.getShowNotesToStudents()) {
                studentCommentsMap = attendanceService.getAttendanceCommentsBySectionId(student.getCanvasSectionId());
            }

            //Generates the map with the information to be submitted to Canvas
            studentMap.put("sis_user_id:" + student.getSisUserId(), generateStudentSubmissionOptions(isSimpleAttendance, submissionOptions, attendanceAssignment, course, studentCommentsMap, student, summaryForSections));
        }

        //Pushing the information to Canvas
        Optional<Progress> returnedProgress = Optional.empty();
        submissionOptions.setStudentSubmissionOptionMap(studentMap);

        try {
            returnedProgress = canvasApiWrapperService.gradeMultipleSubmissionsByCourse(oauthToken, submissionOptions);
        } catch (IOException e) {
            LOG.error("Error while pushing the grades of course: " + courseId, e);
        }
        if (!isValidProgress(returnedProgress)) {
            LOG.error("Error object returned while pushing the grades of course: " + courseId);
        }
    }

    /**
     * Generates the parameters and data needed for push grades or comments to canvas
     */
    private MultipleSubmissionsOptions.StudentSubmissionOption generateStudentSubmissionOptions(boolean isSimpleAttendance, MultipleSubmissionsOptions submissionOptions,
                                                                                               AttendanceAssignment attendanceAssignment, AttendanceCourse course, Map<Long, String> studentCommentsMap, AttendanceStudent student,
                                                                                               List<AttendanceSummaryModel> summaryForSections) {

        Double grade = calculateStudentGrade(attendanceAssignment, summaryForSections, isSimpleAttendance, student);

        StringBuilder comments = new StringBuilder();

        for(AttendanceSummaryModel model: summaryForSections){
            for (AttendanceSummaryModel.Entry entry: model.getEntries()){
                if (entry.getSisUserId().equals(student.getSisUserId())){
                    comments.append(getCommentHeader(entry, course, isSimpleAttendance));
                }
            }
        }
        return submissionOptions.createStudentSubmissionOption(comments.toString(), grade.toString(), null, null, null, null);
    }

    private boolean isValidProgress(Optional<Progress> returnedProgress) {
        return returnedProgress.isPresent() && !"failed".equals(returnedProgress.get().getWorkflowState());
    }

    /**
     * Generates the header part of the comments, which is an explanation of the grades.
     */
    private String getCommentHeader(AttendanceSummaryModel.Entry entry, AttendanceCourse course, boolean isSimpleAttendance) {
        String sectionName = sectionService.getSection(entry.getSectionId()).getName();
        StringBuilder commentBuilder = new StringBuilder();

        if (isSimpleAttendance) {
            commentBuilder.append("Section Name: ").append(sectionName)
                .append("\nTotal number of classes: ").append(getTotalSimpleClasses(entry))
                .append("\nNumber of classes present: ").append(entry.getTotalClassesPresent())
                .append("\nNumber of classes tardy: ").append(entry.getTotalClassesTardy())
                .append("\nNumber of classes absent: ").append(entry.getTotalClassesMissed())
                .append("\nNumber of classes excused: ").append(entry.getTotalClassesExcused())
                .append("\n");
        } else {
            commentBuilder.append("Total number of minutes: ").append(course.getTotalMinutes())
                .append("\nTotal minutes missed: ").append(entry.getSumMinutesMissed())
                .append("\nNumber of minutes made up: ").append(entry.getSumMinutesMadeup())
                .append("\nNumber of minutes remaining to be made up: ").append(entry.getRemainingMinutesMadeup())
                .append("\n");
        }
        commentBuilder.append(entry.getSisUserId());
        commentBuilder.append("\n\nTo see a date-by-date breakdown of your attendance, " +
            "please navigate to the \"K-State Attendance\" tab in the navigation menu of this Canvas course.");

        return commentBuilder.toString();
    }

    /**
     * Calculates the grade of one student.
     */
    private double calculateStudentGrade(AttendanceAssignment attendanceAssignment, List<AttendanceSummaryModel> summaryForSections, boolean isSimpleAttendance, AttendanceStudent student) {

        if (isSimpleAttendance) {

            int totalClasses = 0;
            double presentPoints = calculation(attendanceAssignment.getPresentPoints());
            double tardyPoints = calculation(attendanceAssignment.getTardyPoints());
            double excusedPoints = calculation(attendanceAssignment.getExcusedPoints());
            double absentPoints = calculation(attendanceAssignment.getAbsentPoints());
            double totalClassesPresent = 0.0, totalClassesTardy = 0.0, totalClassesExcused = 0.0, totalClassesMissed = 0.0;

            for (AttendanceSummaryModel model : summaryForSections) {
                for (AttendanceSummaryModel.Entry entry : model.getEntries()) {
                    if (entry.getSisUserId() != null && entry.getSisUserId().equals(student.getSisUserId())) {
                        totalClasses += getTotalSimpleClasses(entry);
                        totalClassesPresent += entry.getTotalClassesPresent();
                        totalClassesTardy += entry.getTotalClassesTardy();
                        totalClassesExcused += entry.getTotalClassesExcused();
                        totalClassesMissed += entry.getTotalClassesMissed();
                    }
                }
            }
            if (totalClasses == 0){
                return 0.0;
            }
            else {
                return ((((totalClassesPresent * presentPoints) +
                    (totalClassesTardy * tardyPoints) +
                    (totalClassesExcused * excusedPoints) +
                    (totalClassesMissed * absentPoints)) / totalClasses) * Double.parseDouble(attendanceAssignment.getAssignmentPoints()));
            }
        } else {

            for (AttendanceSummaryModel model : summaryForSections) {
                for (AttendanceSummaryModel.Entry entry : model.getEntries()) {
                    if (entry.getSisUserId() != null && entry.getSisUserId().equals(student.getSisUserId())) {
                        return ((100 - entry.getPercentCourseMissed()) / 100) * Double.parseDouble(attendanceAssignment.getAssignmentPoints());
                    }
                }
            }
        }
        return 0.0;
    }
    private double calculation(String value) {
        return Double.parseDouble(value)/100;
    }

    private int getTotalSimpleClasses(AttendanceSummaryModel.Entry entry) {
        return entry.getTotalClassesExcused() + entry.getTotalClassesMissed() + entry.getTotalClassesTardy() + entry.getTotalClassesPresent();
    }

}