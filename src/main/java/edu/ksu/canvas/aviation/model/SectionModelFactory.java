package edu.ksu.canvas.aviation.model;

import edu.ksu.canvas.aviation.entity.AviationSection;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class SectionModelFactory {


    public List<SectionModel> createSectionModels(List<AviationSection> sections) {
        List<SectionModel> ret = new ArrayList<>();

        for (AviationSection section : sections) {
            ret.add(createSectionModel(section));
        }

        return ret;
    }


    private SectionModel createSectionModel(AviationSection aviationSection) {
        SectionModel ret = new SectionModel();
        ret.setSectionId(aviationSection.getSectionId());
        ret.setCanvasCourseId(aviationSection.getCanvasCourseId());
        ret.setSectionName(aviationSection.getName());

        return ret;
    }

}
