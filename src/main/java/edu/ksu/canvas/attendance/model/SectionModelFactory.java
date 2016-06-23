package edu.ksu.canvas.attendance.model;

import edu.ksu.canvas.attendance.entity.AviationSection;

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
        ret.setCanvasSectionId(aviationSection.getCanvasSectionId());
        ret.setCanvasCourseId(aviationSection.getCanvasCourseId());
        ret.setSectionName(aviationSection.getName());

        return ret;
    }

}
