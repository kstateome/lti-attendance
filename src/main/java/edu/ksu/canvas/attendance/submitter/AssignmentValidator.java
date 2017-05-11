package edu.ksu.canvas.attendance.submitter;


import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.services.CanvasApiWrapperService;
import edu.ksu.canvas.exception.ObjectNotFoundException;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.oauth.OauthToken;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class AssignmentValidator {

    private static final Logger LOG = Logger.getLogger(AssignmentValidator.class);

    public Error validateAttendanceAssignment(Long courseId, AttendanceAssignment attendanceAssignment, CanvasApiWrapperService canvasApiWrapperService, OauthToken oauthToken) {

        // If there is no linked canvas assignment, creates on in canvas
        if(attendanceAssignment.getCanvasAssignmentId() == null) {
            return new Error("NO CANVAS ASSIGNMENT LINKED");
        }

        // Looks for the linked assignment in canvas
        Optional<Assignment> assignmentOptional;
        try {
            assignmentOptional = canvasApiWrapperService.getSingleAssignment(courseId, oauthToken, attendanceAssignment.getCanvasAssignmentId().toString());
        } catch (IOException e) {
            LOG.error("Error while getting assignment from canvas for section: " + attendanceAssignment.getAttendanceSection().getSectionId(), e);
            return new Error("Could not connect to Canvas to get assignment");
        } catch (ObjectNotFoundException e) {
            LOG.debug("Assignment with ID: " + attendanceAssignment.getCanvasAssignmentId() + "not found in Canvas.");
            return new Error("NO CANVAS ASSIGNMENT LINKED");
        }

        if(!assignmentOptional.isPresent()) {
            return new Error("NO CANVAS ASSIGNMENT LINKED");
        }

        return null;
    }

    public Error validateCanvasAssignment(AttendanceAssignment assignmentConfigurationFromSetup, Long courseId, AttendanceAssignment attendanceAssignment, CanvasApiWrapperService canvasApiWrapperService, OauthToken oauthToken) {

        //Look for changes between the course configuration in the form and in the DB
        if(!isConfigurationInFormAndDBEquals(assignmentConfigurationFromSetup, attendanceAssignment)) {
            LOG.debug("Configuration form is different than saved assignment configuration for section: " + attendanceAssignment.getAttendanceSection().getSectionId());
            return new Error("Assignment configuration needs to be saved before pushing to Canvas");
        }

        //Look for the canvas assignment in canvas
        Optional<Assignment> assignmentOptional;
        try {
            assignmentOptional = canvasApiWrapperService.getSingleAssignment(courseId, oauthToken, attendanceAssignment.getCanvasAssignmentId().toString());
        } catch (IOException e) {
            LOG.error("Error while getting assignment from canvas for section: " + attendanceAssignment.getAttendanceSection().getSectionId(), e);
            return new Error("Could not connect to Canvas to get assignment");
        }

        if(!assignmentOptional.isPresent()) {
            return new Error("Assignment not found in Canvas");
        }

        //Looks for discrepancy between the assignment in canvas and in database
        if (assignmentOptional.get().getPointsPossible().doubleValue() != assignmentConfigurationFromSetup.getAssignmentPoints().doubleValue()) {
            LOG.debug("Discrepancy between Canvas and DB assignment. Point value of Canvas assignment is: " + assignmentOptional.get().getPointsPossible().doubleValue() +
                      " and point value of Database assignment is :" + assignmentConfigurationFromSetup.getAssignmentPoints().doubleValue());
            return new Error("DISCREPANCY BETWEEN CANVAS AND DATABASE");
        }

        return null;
    }

    /**
     * Returns true if configuration in the form and in the db is the same
     */
    private boolean isConfigurationInFormAndDBEquals(AttendanceAssignment assignmentConfigurationFromSetup, AttendanceAssignment attendanceAssignmentSaved) {
        return assignmentConfigurationFromSetup.getAssignmentPoints().doubleValue() == attendanceAssignmentSaved.getAssignmentPoints().doubleValue() && assignmentConfigurationFromSetup.getExcusedPoints().doubleValue() == attendanceAssignmentSaved.getExcusedPoints().doubleValue()
                && assignmentConfigurationFromSetup.getAbsentPoints().doubleValue() == attendanceAssignmentSaved.getAbsentPoints().doubleValue() && assignmentConfigurationFromSetup.getTardyPoints().doubleValue() == attendanceAssignmentSaved.getTardyPoints().doubleValue()
                && assignmentConfigurationFromSetup.getPresentPoints().doubleValue() == attendanceAssignmentSaved.getPresentPoints().doubleValue();
    }

}
