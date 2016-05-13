package edu.ksu.canvas.aviation.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.ksu.canvas.aviation.entity.AviationSection;


@Repository
public interface AviationSectionRepository extends CrudRepository<AviationSection, Long>{

    
    AviationSection findBySectionId(Long sectionId);
    
    AviationSection findByCanvasSectionId(Long canvasSectionId);
    
}
