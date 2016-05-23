package edu.ksu.canvas.aviation.form;

import edu.ksu.canvas.aviation.model.SectionModel;

import java.util.Date;
import java.util.List;


public class RosterForm extends CourseConfigurationForm {

    private List<SectionModel> sectionModels;
    private long sectionId;
    private Date currentDate;


    public List<SectionModel> getSectionModels() {
        return sectionModels;
    }

    public void setSectionModels(List<SectionModel> sectionModels) {
        this.sectionModels = sectionModels;
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
