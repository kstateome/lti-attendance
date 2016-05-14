package edu.ksu.canvas.aviation.factory;

import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.model.SectionInfo;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class SectionInfoFactory {

    @Autowired
    private AviationStudentRepository studentRepository;
    
    
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
        
        List<AviationStudent> aviationStudents = new ArrayList<>();
        aviationStudents.addAll(studentRepository.findBySectionId(aviationSection.getCanvasSectionId()));
        
        ret.setStudents(aviationStudents);
        
        return ret;
    }
    
}
