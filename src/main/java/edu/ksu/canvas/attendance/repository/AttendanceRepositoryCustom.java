package edu.ksu.canvas.attendance.repository;

import java.util.Date;
import java.util.List;

import edu.ksu.canvas.attendance.entity.Attendance;


public interface AttendanceRepositoryCustom {


    List<Attendance> getAttendanceByCourseAndDayOfClass(long courseId, Date dateOfClass);

    void saveInBatches(List<Attendance> attendances);

}
