package edu.ksu.canvas.aviation.model;

import edu.ksu.canvas.model.Section;

import java.util.List;


public class SectionInfo {

    private long sectionId; //canvas section ID
    private String sectionName;
    private Long canvasCourseId;
    private List<AttendanceInfo> attendances;

    public SectionInfo(Section section){
        sectionId = section.getId();
        sectionName = section.getName();
        canvasCourseId = section.getCourseId() == null ? null : Long.valueOf(section.getCourseId());
    }
    

    public SectionInfo() { }
    

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

    public Long getCanvasCourseId() {
        return canvasCourseId;
    }

    public void setCanvasCourseId(Long canvasCourseId) {
        this.canvasCourseId = canvasCourseId;
    }

    public List<AttendanceInfo> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<AttendanceInfo> attendances) {
        this.attendances = attendances;
    }


    @Override
    public String toString() {
        return "SectionInfo{" +
                "sectionId=" + sectionId +
                ", sectionName='" + sectionName + '\'' +
                ", canvasCourseId=" + canvasCourseId +
                '}';
    }
}
