package edu.ksu.canvas.attendance.submitter;


import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.exception.CanvasOutOfSyncException;
import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.services.CanvasApiWrapperService;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.oauth.OauthToken;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class AssignmentValidator {

    private static final Logger LOG = Logger.getLogger(AssignmentValidator.class);

    public void validateAttendanceAssignment(Long courseId, AttendanceAssignment attendanceAssignment, CanvasApiWrapperService canvasApiWrapperService, OauthToken oauthToken) throws IOException, CanvasOutOfSyncException {

        // If there is no linked canvas assignment, creates on in canvas
        if(attendanceAssignment.getCanvasAssignmentId() == null) {
            LOG.info("No Canvas assignment linked to section: " + attendanceAssignment.getAttendanceSection().getCanvasSectionId());
            throw new CanvasOutOfSyncException("NO CANVAS ASSIGNMENT LINKED");
        }

        // Looks for the linked assignment in canvas
        Optional<Assignment> assignmentOptional;
        try {
            assignmentOptional = canvasApiWrapperService.getSingleAssignment(courseId, oauthToken, attendanceAssignment.getCanvasAssignmentId().toString());
        } catch (IOException e) {
            LOG.error("Error while getting assignment from canvas for section: " + attendanceAssignment.getAttendanceSection().getCanvasSectionId(), e);
            throw new IOException("Could not connect to Canvas to get assignment");
        }

        if(!assignmentOptional.isPresent()) {
            LOG.info("No Canvas assignment linked to section: " + attendanceAssignment.getAttendanceSection().getCanvasSectionId());
            throw new CanvasOutOfSyncException("NO CANVAS ASSIGNMENT LINKED");
        }
    }

    public void validateCanvasAssignment(AttendanceAssignment assignmentConfigurationFromSetup, Long courseId, AttendanceAssignment attendanceAssignment,
                                         CanvasApiWrapperService canvasApiWrapperService, OauthToken oauthToken) throws IOException, CanvasOutOfSyncException {

        //Look for changes between the course configuration in the form and in the DB
        if(!isAssignmentConfigurationSaved(assignmentConfigurationFromSetup, attendanceAssignment)) {
            LOG.info("Configuration form is different than saved assignment configuration for section: " + attendanceAssignment.getAttendanceSection().getCanvasSectionId());
            throw new CanvasOutOfSyncException("Assignment configuration needs to be saved before pushing to Canvas");
        }

        //Look for the canvas assignment in canvas
        Optional<Assignment> assignmentOptional;
        try {
            assignmentOptional = canvasApiWrapperService.getSingleAssignment(courseId, oauthToken, attendanceAssignment.getCanvasAssignmentId().toString());
        } catch (IOException e) {
            LOG.error("Error while getting assignment from canvas for section: " + attendanceAssignment.getAttendanceSection().getSectionId(), e);
            throw new IOException("Could not connect to Canvas to get assignment");
        }

        if(!assignmentOptional.isPresent()) {
            LOG.error("Assignment not found in Canvas. Empty optional returned from api call.");
            throw new IOException("Assignment not found in Canvas");
        }

        //Looks for discrepancy between the assignment in canvas and in database
        if (assignmentOptional.get().getPointsPossible().doubleValue() != assignmentConfigurationFromSetup.getAssignmentPoints().doubleValue()) {
            LOG.debug("Discrepancy between Canvas and DB assignment. Point value of Canvas assignment is: " + assignmentOptional.get().getPointsPossible().doubleValue() +
                      " and point value of Database assignment is :" + assignmentConfigurationFromSetup.getAssignmentPoints().doubleValue());
            throw new CanvasOutOfSyncException("DISCREPANCY BETWEEN CANVAS AND DATABASE");
        }
    }

    /**
     * Returns true if configuration in the form and in the db is the same
     */
    private boolean isAssignmentConfigurationSaved(AttendanceAssignment assignmentConfigurationFromSetup, AttendanceAssignment attendanceAssignmentSaved) {
        return assignmentConfigurationFromSetup.getAssignmentPoints().doubleValue() == attendanceAssignmentSaved.getAssignmentPoints().doubleValue()
                && assignmentConfigurationFromSetup.getExcusedPoints().doubleValue() == attendanceAssignmentSaved.getExcusedPoints().doubleValue()
                && assignmentConfigurationFromSetup.getAbsentPoints().doubleValue() == attendanceAssignmentSaved.getAbsentPoints().doubleValue()
                && assignmentConfigurationFromSetup.getTardyPoints().doubleValue() == attendanceAssignmentSaved.getTardyPoints().doubleValue()
                && assignmentConfigurationFromSetup.getPresentPoints().doubleValue() == attendanceAssignmentSaved.getPresentPoints().doubleValue();
    }

    /**
     * Checks if the configuration setup have been saved before pushing. Throw CanvasOutOfSyncException if not.
     */
    public void validateConfigurationSetupExistence(AttendanceSummaryModel model, AttendanceAssignment attendanceAssignment) throws CanvasOutOfSyncException {
        if (attendanceAssignment == null || (attendanceAssignment.getAssignmentName() == null && attendanceAssignment.getAssignmentPoints() == null)) {
            LOG.info("There is no Attendance Assignment associated to section " + model.getSectionId());
            throw new CanvasOutOfSyncException("Please save configuration setup for the assignment before pushing grades to Canvas.");
        }
    }

}
