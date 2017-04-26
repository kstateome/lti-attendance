package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.repository.AttendanceAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AttendanceAssignmentService {

    @Autowired
    private AttendanceAssignmentRepository assignmentRepository;

    public AttendanceAssignment findByAssignmentId(long assignmentId) {
        return assignmentRepository.findByAssignmentId(assignmentId);
    }

    public AttendanceAssignment findBySectionId(long sectionId) {
        return assignmentRepository.findBySectionId(sectionId);
    }

}
