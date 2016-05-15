package edu.ksu.canvas.aviation.factory;

import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.model.SectionModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class SectionModelFactory {
    
    
    public List<SectionModel> createSectionModels(List<AviationSection> sections) {
        List<SectionModel> ret = new ArrayList<>();
        
        for(AviationSection section : sections) {
            ret.add(createSectionModel(section));
        }
        
        return ret;
    }

    
    private SectionModel createSectionModel(AviationSection aviationSection) {
        SectionModel ret = new SectionModel();
        ret.setSectionId(aviationSection.getCanvasSectionId());
        ret.setCanvasCourseId(aviationSection.getCanvasCourseId());
        ret.setSectionName(aviationSection.getName());
        
        return ret;
    }
    
}
