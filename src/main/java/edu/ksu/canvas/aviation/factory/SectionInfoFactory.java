package edu.ksu.canvas.aviation.factory;

import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.model.SectionInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class SectionInfoFactory {
    
    
    public List<SectionInfo> getSectionInfos(List<AviationSection> sections) {
        List<SectionInfo> ret = new ArrayList<>();
        
        for(AviationSection section : sections) {
            ret.add(getSectionInfo(section));
        }
        
        return ret;
    }

    
    public SectionInfo getSectionInfo(AviationSection aviationSection) {
        SectionInfo ret = new SectionInfo();
        ret.setSectionId(aviationSection.getCanvasSectionId());
        ret.setCanvasCourseId(aviationSection.getCanvasCourseId());
        ret.setSectionName(aviationSection.getName());
        
        return ret;
    }
    
}
