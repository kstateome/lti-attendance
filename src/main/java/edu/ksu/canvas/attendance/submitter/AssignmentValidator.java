package edu.ksu.canvas.attendance.submitter;



import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.exception.AttendanceAssignmentException;
import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.services.CanvasApiWrapperService;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.oauth.OauthToken;
import edu.ksu.canvas.attendance.exception.AttendanceAssignmentException.Error;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class AssignmentValidator {

    private static final Logger LOG = Logger.getLogger(AssignmentValidator.class);

    public AttendanceAssignment validateAttendanceAssignment(Long courseId, AttendanceAssignment attendanceAssignment, CanvasApiWrapperService canvasApiWrapperService, OauthToken oauthToken) throws AttendanceAssignmentException {

        // If there is no linked canvas assignment, creates on in canvas
        if (attendanceAssignment.getCanvasAssignmentId() == null) {
            LOG.info("No Canvas assignment linked to section: " + attendanceAssignment.getAttendanceSection().getCanvasSectionId());
            attendanceAssignment.setStatus(AttendanceAssignment.Status.NOT_LINKED_TO_CANVAS);
            return attendanceAssignment;
        }

        // Looks for the linked assignment in canvas
        Optional<Assignment> assignmentOptional = lookForAssignmentInCanvas(courseId, attendanceAssignment, canvasApiWrapperService, oauthToken);
        // We will create a new assignment for the instructor based on what's left in the database
        if (!assignmentOptional.isPresent()) {
            LOG.info("No Canvas assignment linked to section: " + attendanceAssignment.getAttendanceSection().getCanvasSectionId());
            attendanceAssignment.setStatus(AttendanceAssignment.Status.NOT_LINKED_TO_CANVAS);
        }

        return attendanceAssignment;
    }

    public AttendanceAssignment validateCanvasAssignment(AttendanceAssignment assignmentConfigurationFormSetup, Long courseId, AttendanceAssignment attendanceAssignment,
                                         CanvasApiWrapperService canvasApiWrapperService, OauthToken oauthToken) throws AttendanceAssignmentException{

        //Look for changes between the course configuration in the form and in the DB
        if(!isAssignmentConfigurationSaved(assignmentConfigurationFormSetup, attendanceAssignment)) {
            LOG.info("Configuration form is different than saved assignment configuration for section: " + attendanceAssignment.getAttendanceSection().getCanvasSectionId());
            throw new AttendanceAssignmentException(Error.NOT_SAVED);

        }

        //Look for the canvas assignment in canvas
        Optional<Assignment> assignmentOptional = lookForAssignmentInCanvas(courseId,attendanceAssignment,canvasApiWrapperService,oauthToken);

        //Looks for discrepancy between the assignment in canvas and in database
        if (anyDiscrepancy(assignmentOptional.get(), assignmentConfigurationFormSetup)){
            LOG.debug("Discrepancy between Canvas and DB assignment. Point value of Canvas assignment is: " + assignmentOptional.get().getPointsPossible() +
                      " and point value of Database assignment is :" + attendanceAssignment.getAssignmentPoints());
            attendanceAssignment.setStatus(AttendanceAssignment.Status.CANVAS_AND_DB_DISCREPANCY);
        }
        return attendanceAssignment;
    }

    public Boolean anyDiscrepancy(Assignment canvasAssignment, AttendanceAssignment dbAssignment){
        if (canvasAssignment.getName() != dbAssignment.getAssignmentName()){
            return false;
        }
        return canvasAssignment.getPointsPossible() == dbAssignment.getAssignmentPoints();

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
     * Checks if the configuration setup have been saved before pushing. Throw AttendanceAssignmentException if not.
     */
    public AttendanceAssignment validateConfigurationSetupExistence(AttendanceSummaryModel model, AttendanceAssignment attendanceAssignment) throws AttendanceAssignmentException{

        if (attendanceAssignment == null || (attendanceAssignment.getAssignmentName() == null && attendanceAssignment.getAssignmentPoints() == null)) {
            LOG.info("There is no Attendance Assignment associated to section " + model.getSectionId());
            throw new AttendanceAssignmentException(Error.NOT_SAVED);
        }
        attendanceAssignment.setStatus(AttendanceAssignment.Status.UNKNOWN);
        return attendanceAssignment;
    }

    // Looks for the linked assignment in canvas
    private Optional<Assignment> lookForAssignmentInCanvas (Long courseId, AttendanceAssignment attendanceAssignment,
                                                           CanvasApiWrapperService canvasApiWrapperService, OauthToken oauthToken) throws AttendanceAssignmentException{
        Optional<Assignment> assignmentOptional;
        try {
            assignmentOptional = canvasApiWrapperService.getSingleAssignment(courseId, oauthToken, attendanceAssignment.getCanvasAssignmentId().toString());
        } catch (IOException e) {
            LOG.error("Error while getting assignment from canvas for section: " + attendanceAssignment.getAttendanceSection().getSectionId(), e);
            throw new AttendanceAssignmentException(Error.NO_CONNECTION);
        }
        return assignmentOptional;
    }
}
