package edu.ksu.canvas.aviation.repository;

import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.entity.MakeupTracker;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MakeupTrackerRepository extends CrudRepository<MakeupTracker, Long> {
    
    List<MakeupTracker> findByAviationStudent(AviationStudent aviationStudent);
    
    MakeupTracker findByMakeupTrackerId(Long makeUpTrackerId);
    
}
