package edu.ksu.canvas.aviation.repository;

import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.entity.Makeup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MakeupRepository extends CrudRepository<Makeup, Long> {
    
    List<Makeup> findByAviationStudent(AviationStudent aviationStudent);
    
    Makeup findByMakeupId(Long makeUpId);
    
}
