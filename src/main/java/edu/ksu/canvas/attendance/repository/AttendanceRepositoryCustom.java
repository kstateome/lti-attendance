package edu.ksu.canvas.attendance.repository;

import edu.ksu.canvas.attendance.entity.Attendance;

import java.util.Date;
import java.util.List;
import java.util.Map;


public interface AttendanceRepositoryCustom {


    List<Attendance> getAttendanceByCourseAndDayOfClass(long courseId, Date dateOfClass);

    void saveInBatches(List<Attendance> attendances);

    void deleteAttendanceByCourseAndDayOfClass(long courseId, Date dateOfClass, long sectionId);

    Map<Long, String> getAttendanceCommentsBySectionId(long sectionId);

}
