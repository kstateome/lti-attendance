package edu.ksu.canvas.attendance.repository;

import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AviationStudentRepository extends CrudRepository<AttendanceStudent, Long> {


    AttendanceStudent findByStudentId(Long studentId);

    AttendanceStudent findBySisUserId(String sisUserId);

    AttendanceStudent findBySisUserIdAndCanvasSectionId(String sisUserId, Long canvasSectionId);

    List<AttendanceStudent> findByCanvasSectionIdOrderByNameAsc(long sectionId);

    List<AttendanceStudent> findByCanvasCourseId(long courseId);

}
