package edu.ksu.canvas.attendance.submitter;


import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.repository.AttendanceAssignmentRepository;
import edu.ksu.canvas.attendance.services.AttendanceAssignmentService;
import edu.ksu.canvas.attendance.services.AttendanceSectionService;
import edu.ksu.canvas.attendance.services.CanvasApiWrapperService;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.oauth.OauthToken;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@Scope(value="session")
public class CanvasAssignmentAssistant {

    private static final Logger LOG = Logger.getLogger(CanvasAssignmentAssistant.class);
    private static final String ASSIGNMENT_DESCRIPTION = "This result of this assignment is based on attendances of each student. Detailed and individual information in the grading comments.";

    @Autowired
    private AttendanceAssignmentService assignmentService;

    @Autowired
    private AttendanceSectionService attendanceSectionService;

    @Autowired
    private AttendanceAssignmentRepository assignmentRepository;

    @Autowired
    private CanvasApiWrapperService canvasApiWrapperService;

    public Error createAssignmentInCanvas(Long courseId, AttendanceAssignment attendanceAssignment, OauthToken oauthToken) {
        Optional<Assignment> canvasAssignmentOptional;

        Assignment assignment = generateCanvasAssignment(courseId, attendanceAssignment);
        try {
            canvasAssignmentOptional = canvasApiWrapperService.createAssignment(courseId, assignment, oauthToken);
        } catch (IOException e) {
            LOG.error("Error while creating canvas assignment for section: " +  attendanceAssignment.getAttendanceSection().getSectionId(), e);
            return new Error("Error while creating canvas assignment for section: " +  attendanceAssignment.getAttendanceSection().getName());
        }

        if(!canvasAssignmentOptional.isPresent())  {
            LOG.error("Error while creating canvas assignment for section: " +  attendanceAssignment.getAttendanceSection().getSectionId());
            return new Error("Error while creating canvas assignment for section: " + attendanceAssignment.getAttendanceSection().getName());
        }

        LOG.info("Created canvas assignment: " + canvasAssignmentOptional.get().getId());
        saveCanvasAssignmentId(courseId, canvasAssignmentOptional.get());

        return null;
    }

    private Assignment generateCanvasAssignment(Long courseId, AttendanceAssignment attendanceAssignment) {
        Assignment assignment = new Assignment();
        assignment.setName(attendanceAssignment.getAssignmentName());
        assignment.setPointsPossible(Double.valueOf(attendanceAssignment.getAssignmentPoints()));
        assignment.setCourseId(courseId.toString());
        assignment.setDescription(ASSIGNMENT_DESCRIPTION);
        assignment.setPublished(true);
        assignment.setUnpublishable(false);
        return assignment;
    }

    /**
     * This function saves the canvas assignment id into the attendance assignment of all sections.
     * This works this way because for the user this works in the course level. But in the back-end
     * this works section based.
     */
    private void saveCanvasAssignmentId(Long courseId, Assignment canvasAssignment) {
        List<AttendanceSection> sectionList = attendanceSectionService.getSectionsByCourse(courseId);
        for(AttendanceSection section: sectionList) {
            AttendanceAssignment attendanceAssignment = assignmentService.findBySection(section);
            attendanceAssignment.setCanvasAssignmentId(Long.valueOf(canvasAssignment.getId()));
            assignmentRepository.save(attendanceAssignment);
        }
    }


    public Error editAssignmentInCanvas(Long courseId, AttendanceAssignment attendanceAssignment, OauthToken oauthToken) {

        Optional<Assignment> canvasAssignmentOptional;
        try {
            canvasAssignmentOptional = canvasApiWrapperService.getSingleAssignment(courseId, oauthToken, attendanceAssignment.getCanvasAssignmentId().toString());
        } catch (IOException e) {
            LOG.error("Error while getting assignment from canvas for section: " + attendanceAssignment.getAttendanceSection().getSectionId(), e);
            return new Error("Could not connect to Canvas to get assignment");
        }

        if(!canvasAssignmentOptional.isPresent()) {
            return new Error("Assignment not found in Canvas");
        }

        Assignment canvasAssignment = canvasAssignmentOptional.get();
        canvasAssignment.setPointsPossible(Double.valueOf(attendanceAssignment.getAssignmentPoints()));
        try {
            canvasApiWrapperService.editAssignment(courseId.toString(), canvasAssignment, oauthToken);
        } catch (IOException e) {
            LOG.error("Error while editing canvas assignment for section: " + attendanceAssignment.getAttendanceSection().getSectionId(), e);
            return new Error("Error while editing canvas assignment for section: " + attendanceAssignment.getAttendanceSection().getName());
        }

        LOG.info("Canvas assignment edited based on information in the section configuration.");
        return null;
    }

    public Error deleteAssignmentInCanvas(Long canvasCourseId, OauthToken oauthToken) {

        List<AttendanceSection> sections = attendanceSectionService.getSectionByCanvasCourseId(canvasCourseId);
        if(sections == null || sections.isEmpty()) {
            RuntimeException e = new IllegalArgumentException("Cannot load data into courseForm for non-existent course");
            throw new ContextedRuntimeException(e).addContextValue("courseId", canvasCourseId);
        }

        AttendanceAssignment assignment = assignmentRepository.findByAttendanceSection(sections.get(0));

        if(assignment == null) {
            LOG.error("Attendance assignment not found for section: " + sections.get(0).getName());
            return new Error("Attendance assignment not found for section: " + sections.get(0).getName());
        } else if (assignment.getCanvasAssignmentId() == null) {
            LOG.info("No Canvas assignment associated to Attendance assignment  to be deleted for section: " + sections.get(0).getName());
            return new Error("NO CANVAS ASSIGNMENT ASSOCIATED");
        }

        try {
            canvasApiWrapperService.deleteAssignment(canvasCourseId.toString(), assignment.getCanvasAssignmentId().toString(), oauthToken);
        } catch (IOException e) {
            LOG.error("Error while deleting canvas assignment: " + assignment.getCanvasAssignmentId(), e);
            return new Error("Error while deleting canvas assignment: " + assignment.getCanvasAssignmentId());
        }
        LOG.info("Canvas assignment " + assignment.getCanvasAssignmentId() + " was successfully deleted.");
        return null;
    }
}
