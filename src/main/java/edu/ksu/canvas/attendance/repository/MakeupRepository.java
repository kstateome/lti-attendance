package edu.ksu.canvas.attendance.repository;

import edu.ksu.canvas.attendance.entity.AviationStudent;
import edu.ksu.canvas.attendance.entity.Makeup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MakeupRepository extends CrudRepository<Makeup, Long> {
    

    List<Makeup> findByAviationStudentOrderByDateOfClassAsc(AviationStudent aviationStudent);
    
    Makeup findByMakeupId(Long makeUpId);
    
}
