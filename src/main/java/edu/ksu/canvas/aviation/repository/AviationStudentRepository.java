package edu.ksu.canvas.aviation.repository;

import edu.ksu.canvas.aviation.entity.AviationStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AviationStudentRepository extends CrudRepository<AviationStudent, Long> {


    AviationStudent findByStudentId(Long studentId);

    AviationStudent findBySisUserId(String sisUserId);

    AviationStudent findBySisUserIdAndCanvasSectionId(String sisUserId, Long canvasSectionId);

    List<AviationStudent> findByCanvasSectionIdOrderByNameAsc(long sectionId);

    List<AviationStudent> findByCanvasCourseId(long courseId);

}
