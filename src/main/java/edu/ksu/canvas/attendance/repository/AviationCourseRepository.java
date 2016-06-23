package edu.ksu.canvas.attendance.repository;

import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AviationCourseRepository extends CrudRepository<AttendanceCourse, Long> {


    AttendanceCourse findByCourseId(Long courseId);

    AttendanceCourse findByCanvasCourseId(Long canvasCourseId);

}
