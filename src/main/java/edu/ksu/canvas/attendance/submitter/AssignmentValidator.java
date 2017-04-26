package edu.ksu.canvas.attendance.submitter;


import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.form.CourseConfigurationForm;
import edu.ksu.canvas.attendance.services.CanvasApiWrapperService;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.oauth.OauthToken;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

public class AssignmentValidator {

    private static final Logger LOG = Logger.getLogger(AssignmentValidator.class);

    public Error validateAttendanceAssignment(Long courseId, AttendanceAssignment attendanceAssignment, CanvasApiWrapperService canvasApiWrapperService, OauthToken adminOauthToken) {

        // If there is no linked canvas assignment, creates on in canvas
        if(attendanceAssignment.getCanvasAssignmentId() == null) {
            return new Error("NO CANVAS ASSIGNMENT LINKED");
        }

        // Looks for the linked assignment in canvas
        Optional<Assignment> assignmentOptional;
        try {
            assignmentOptional = canvasApiWrapperService.getSingleAssignment(courseId, adminOauthToken, attendanceAssignment.getCanvasAssignmentId().toString());
        } catch (IOException e) {
            LOG.error("Error while getting assignment from canvas for section: " + attendanceAssignment.getSectionId(), e);
            return new Error("Could not connect to Canvas to get assignment");
        }

        if(!assignmentOptional.isPresent()) {
            return new Error("NO CANVAS ASSIGNMENT LINKED");
        }

        return null;
    }

    public Error validateCanvasAssignment(CourseConfigurationForm courseConfigurationForm, Long courseId, AttendanceAssignment attendanceAssignment, CanvasApiWrapperService canvasApiWrapperService, OauthToken adminOauthToken) {

        if(!compareFormToDB(courseConfigurationForm, attendanceAssignment)) {
            LOG.error("Configuration form is different than saved assignment configuration for section: " + attendanceAssignment.getSectionId());
            return new Error("Assignment configuration needs to be saved before pushing to Canvas");
        }

        Optional<Assignment> assignmentOptional;
        try {
            assignmentOptional = canvasApiWrapperService.getSingleAssignment(courseId, adminOauthToken, attendanceAssignment.getCanvasAssignmentId().toString());
        } catch (IOException e) {
            LOG.error("Error while getting assignment from canvas for section: " + attendanceAssignment.getSectionId(), e);
            return new Error("Could not connect to Canvas to get assignment");
        }

        if(!assignmentOptional.isPresent()) {
            return new Error("Assignment not found in Canvas");
        }

        if (assignmentOptional.get().getPointsPossible().doubleValue() != courseConfigurationForm.getAssignmentPoints().doubleValue()) {
            return new Error("DISCREPANCY BETWEEN CANVAS AND DATABASE");
        }

        return null;
    }

    private boolean compareFormToDB(CourseConfigurationForm courseConfigurationForm, AttendanceAssignment attendanceAssignment) {
        return courseConfigurationForm.getAssignmentPoints().doubleValue() == attendanceAssignment.getAssignmentPoints().doubleValue() && courseConfigurationForm.getExcusedPoints().doubleValue() == attendanceAssignment.getExcusedPoints().doubleValue()
                && courseConfigurationForm.getAbsentPoints().doubleValue() == attendanceAssignment.getAbsentPoints().doubleValue() && courseConfigurationForm.getTardyPoints().doubleValue() == attendanceAssignment.getTardyPoints().doubleValue()
                && courseConfigurationForm.getPresentPoints().doubleValue() == attendanceAssignment.getPresentPoints().doubleValue();
    }

}
