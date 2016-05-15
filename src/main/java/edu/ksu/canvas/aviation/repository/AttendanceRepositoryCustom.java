package edu.ksu.canvas.aviation.repository;

import java.util.Date;
import java.util.List;

import edu.ksu.canvas.aviation.entity.Attendance;


public interface AttendanceRepositoryCustom {

 
    List<Attendance> getAttendanceByCourseByDayOfClass(Long courseId, Date dateOfClass);
    
    void saveInBatches(List<Attendance> attendances);
    
}