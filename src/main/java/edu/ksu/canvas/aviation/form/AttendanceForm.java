package edu.ksu.canvas.aviation.form;

import edu.ksu.canvas.aviation.model.SectionInfo;

import java.util.List;

public class AttendanceForm {

    private List<SectionInfo> sectionInfoList;

    private long sectionId;

    public List<SectionInfo> getSectionInfoList() {
        return sectionInfoList;
    }

    public void setSectionInfoList(List<SectionInfo> sectionInfoList) {
        this.sectionInfoList = sectionInfoList;
    }

    public long getSectionId() {
        return sectionId;
    }

    public void setSectionId(long sectionId) {
        this.sectionId = sectionId;
    }
}
