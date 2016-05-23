package edu.ksu.canvas.aviation.repository;

import edu.ksu.canvas.aviation.entity.AviationCourse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AviationCourseRepository extends CrudRepository<AviationCourse, Long> {


    AviationCourse findByCourseId(Long courseId);

    AviationCourse findByCanvasCourseId(Long canvasCourseId);

}
