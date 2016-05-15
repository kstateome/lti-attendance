package edu.ksu.canvas.aviation.form;

import edu.ksu.canvas.aviation.model.SectionInfo;

import java.util.Date;
import java.util.List;


public class RosterForm extends CourseConfigurationForm {

    private List<SectionInfo> sectionInfoList;
    private long sectionId;
    private Date currentDate;

    
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

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }
    
}
