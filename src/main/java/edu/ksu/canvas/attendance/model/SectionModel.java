package edu.ksu.canvas.attendance.model;

import edu.ksu.canvas.model.Section;

import java.util.List;


public class SectionModel {

    private Long canvasSectionId;
    private String sectionName;
    private Long canvasCourseId;
    private Long sectionId;
    private List<AttendanceModel> attendances;


    public SectionModel() {

    }

    public SectionModel(Section section) {
        canvasSectionId = section.getId();
        sectionName = section.getName();
        canvasCourseId = section.getCourseId() == null ? null : Long.valueOf(section.getCourseId());
    }


    public Long getCanvasSectionId() {
        return canvasSectionId;
    }

    public void setCanvasSectionId(Long canvasSectionId) {
        this.canvasSectionId = canvasSectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Long getCanvasCourseId() {
        return canvasCourseId;
    }

    public void setCanvasCourseId(Long canvasCourseId) {
        this.canvasCourseId = canvasCourseId;
    }

    public List<AttendanceModel> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<AttendanceModel> attendances) {
        this.attendances = attendances;
    }


    @Override
    public String toString() {
        return "SectionModel{" +
                "canvasSectionId=" + canvasSectionId +
                ", sectionName='" + sectionName + '\'' +
                ", canvasCourseId=" + canvasCourseId +
                '}';
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }
}
