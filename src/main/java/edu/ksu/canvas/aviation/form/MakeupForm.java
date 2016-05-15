package edu.ksu.canvas.aviation.form;

import java.util.List;

import edu.ksu.canvas.aviation.entity.MakeupTracker;


public class MakeupTrackerForm {

    private long sectionId;
    private long studentId;
    private List<MakeupTracker> entries;

    
    public long getSectionId() {
        return sectionId;
    }

    public void setSectionId(long sectionId) {
        this.sectionId = sectionId;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public List<MakeupTracker> getEntries() {
        return entries;
    }

    public void setEntries(List<MakeupTracker> entries) {
        this.entries = entries;
    }
    
}
