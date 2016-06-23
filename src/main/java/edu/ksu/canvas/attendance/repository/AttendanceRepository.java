package edu.ksu.canvas.attendance.repository;


import edu.ksu.canvas.attendance.entity.Attendance;
import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AttendanceRepository extends CrudRepository<Attendance, Long>, AttendanceRepositoryCustom {


    List<Attendance> findByAviationStudent(AttendanceStudent attendanceStudent);

    Attendance findByAttendanceId(Long attendanceId);

}
