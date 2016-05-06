package edu.ksu.canvas.aviation.form;

import edu.ksu.canvas.aviation.enums.Status;
import edu.ksu.canvas.aviation.model.SectionInfo;

import java.util.List;


public class RosterForm {

    private List<SectionInfo> sectionInfoList;
    private long sectionId;
    private int classTotalMinutes;
    private int defaultMinutesPerSession;

    
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

    public int getClassTotalMinutes() {
        return classTotalMinutes;
    }

    public void setClassTotalMinutes(int classTotalMinutes) {
        this.classTotalMinutes = classTotalMinutes;
    }

    public int getDefaultMinutesPerSession() {
        return defaultMinutesPerSession;
    }

    public void setDefaultMinutesPerSession(int defaultMinutesPerSession) {
        this.defaultMinutesPerSession = defaultMinutesPerSession;
    }
}
