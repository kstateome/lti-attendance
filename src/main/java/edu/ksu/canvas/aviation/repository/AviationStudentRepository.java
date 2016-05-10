package edu.ksu.canvas.aviation.repository;

import edu.ksu.canvas.aviation.entity.AviationStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AviationStudentRepository extends CrudRepository<AviationStudent, Long> {
    AviationStudent findByStudentId(Long studentId);
    AviationStudent findBysisUserId(String sisUserId);
}
