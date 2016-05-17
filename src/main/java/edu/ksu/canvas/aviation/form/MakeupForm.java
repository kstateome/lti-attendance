package edu.ksu.canvas.aviation.form;

import java.util.ArrayList;
import java.util.List;

import edu.ksu.canvas.aviation.entity.Makeup;
import edu.ksu.canvas.aviation.model.MakeupModel;


public class MakeupForm {

    private long sectionId;
    private long studentId;
    private List<MakeupModel> entries;

    
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

    public List<MakeupModel> getEntries() {
        return entries;
    }

    public void setEntries(List<MakeupModel> entries) {
        this.entries = entries;
    }
    
    public void setEntriesFromMakeEntities(List<Makeup> entries) {
        this.entries = new ArrayList<>();
        
        if(entries == null || entries.isEmpty()) {
            return;
        }
        

        for(Makeup entry : entries) {
            MakeupModel modelEntry = new MakeupModel(entry.getMakeupId(), entry.getDateOfClass(), entry.getDateMadeUp(), entry.getProjectDescription(), entry.getMinutesMadeUp());
            this.entries.add(modelEntry);
        }
       
    }

    @Override
    public String toString() {
        return "MakeupForm{" +
                "sectionId=" + sectionId +
                ", studentId=" + studentId +
                ", entries=" + entries +
                '}';
    }
}
