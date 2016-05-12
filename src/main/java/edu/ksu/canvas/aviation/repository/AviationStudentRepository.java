package edu.ksu.canvas.aviation.repository;

import edu.ksu.canvas.aviation.entity.AviationStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface AviationStudentRepository extends CrudRepository<AviationStudent, Long> {
    
    
    AviationStudent findByStudentId(Long studentId);
    
    AviationStudent findBySisUserId(String sisUserId);
    
    AviationStudent findBySisUserIdAndSectionId(String sisUserId, Long sectionId);

    Set<AviationStudent> findBySectionId(long sectionId);
}
