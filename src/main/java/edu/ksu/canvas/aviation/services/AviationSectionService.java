package edu.ksu.canvas.aviation.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.repository.AviationSectionRepository;


@Component
public class AviationSectionService {

    @Autowired
    private AviationSectionRepository sectionRepository;
    

    public AviationSection getSection(long canvasSectionId) {
        return sectionRepository.findByCanvasSectionId(canvasSectionId);
    }
    
    public AviationSection getFirstSectionOfCourse(long canvasCourseId) {
        List<AviationSection> sections = sectionRepository.findByCanvasCourseId(canvasCourseId);
        return sections.isEmpty() ? null : sections.get(0);
    }
    
    
    public List<AviationSection> getSectionsByCourse(long canvasCourseId) {
        return sectionRepository.findByCanvasCourseId(canvasCourseId);
    }
    
}
