package edu.ksu.canvas.attendance.model;

import edu.ksu.canvas.attendance.entity.AttendanceSection;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class SectionModelFactory {


    public List<SectionModel> createSectionModels(List<AttendanceSection> sections) {
        List<SectionModel> ret = new ArrayList<>();

        for (AttendanceSection section : sections) {
            ret.add(createSectionModel(section));
        }

        return ret;
    }


    private SectionModel createSectionModel(AttendanceSection attendanceSection) {
        SectionModel ret = new SectionModel();
        ret.setCanvasSectionId(attendanceSection.getCanvasSectionId());
        ret.setCanvasCourseId(attendanceSection.getCanvasCourseId());
        ret.setSectionName(attendanceSection.getName());

        return ret;
    }

}
