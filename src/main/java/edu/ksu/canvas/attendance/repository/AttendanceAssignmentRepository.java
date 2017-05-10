package edu.ksu.canvas.attendance.repository;

import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceAssignmentRepository extends CrudRepository<AttendanceAssignment, Long> {

    AttendanceAssignment findByAssignmentId(Long assignmentId);

    AttendanceAssignment findByAttendanceSection(AttendanceSection section);

}
