package edu.ksu.canvas.attendance.repository;

import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AttendanceStudentRepository extends CrudRepository<AttendanceStudent, Long> {


    AttendanceStudent findByStudentId(Long studentId);

    AttendanceStudent findBySisUserId(String sisUserId);

    AttendanceStudent findBySisUserIdAndCanvasCourseId(String sisUserId, Integer canvasCourseId);

    AttendanceStudent findBySisUserIdAndCanvasSectionId(String sisUserId, Long canvasSectionId);

    List<AttendanceStudent> findByCanvasSectionIdOrderByNameAsc(long sectionId);

    List<AttendanceStudent> findByCanvasCourseId(long courseId);

}
