package edu.ksu.canvas.attendance.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.repository.AttendanceSectionRepository;


@Component
public class AttendanceSectionService {

    @Autowired
    private AttendanceSectionRepository sectionRepository;


    public AttendanceSection getSection(long canvasSectionId) {
        return sectionRepository.findByCanvasSectionId(canvasSectionId);
    }

    public AttendanceSection getFirstSectionOfCourse(long canvasCourseId) {
        List<AttendanceSection> sections = sectionRepository.findByCanvasCourseId(canvasCourseId);
        return sections.isEmpty() ? null : sections.get(0);
    }


    public List<AttendanceSection> getSectionsByCourse(long canvasCourseId) {
        return sectionRepository.findByCanvasCourseId(canvasCourseId);
    }

}
