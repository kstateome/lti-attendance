package edu.ksu.canvas.aviation.model;

import edu.ksu.canvas.model.Section;

import java.util.List;


public class SectionModel {

    private long sectionId; //canvas section ID
    private String sectionName;
    private Integer canvasCourseId;
    private List<AttendanceModel> attendances;


    public SectionModel() { }

    public SectionModel(Section section) {
        sectionId = section.getId();
        sectionName = section.getName();
        canvasCourseId = section.getCourseId();
    }


    public long getSectionId() {
        return sectionId;
    }

    public void setSectionId(long sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Integer getCanvasCourseId() {
        return canvasCourseId;
    }

    public void setCanvasCourseId(Integer canvasCourseId) {
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
                "sectionId=" + sectionId +
                ", sectionName='" + sectionName + '\'' +
                ", canvasCourseId=" + canvasCourseId +
                '}';
    }

}
