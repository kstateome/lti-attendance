package edu.ksu.canvas.aviation.repository;


import edu.ksu.canvas.aviation.entity.Attendance;
import edu.ksu.canvas.aviation.entity.AviationStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends CrudRepository<Attendance, Long> {
    List<Attendance> findByAviationStudent(AviationStudent aviationStudent);
    Attendance findByAttendanceId(Long attendanceId);
}
