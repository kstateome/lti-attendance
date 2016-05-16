package edu.ksu.canvas.aviation.form;

import java.util.List;

import edu.ksu.canvas.aviation.entity.Makeup;


public class MakeupForm {

    private long sectionId;
    private long studentId;
    private List<Makeup> entries;

    
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

    public List<Makeup> getEntries() {
        return entries;
    }

    public void setEntries(List<Makeup> entries) {
        this.entries = entries;
    }
    
}
