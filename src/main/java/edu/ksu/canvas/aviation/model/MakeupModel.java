package edu.ksu.canvas.aviation.model;

import java.util.Date;


public class MakeupModel {

    private Long makeupId;
    private Date dateOfClass;
    private Date dateMadeUp;
    private String projectDescription;
    private int minutesMadeUp;
    private boolean toBeDeletedFlag;
    
    
    public MakeupModel() { }
    
    public MakeupModel(Long makeupId, Date dateOfClass, Date dateMadeUp, String projectDescription, int minutesMadeUp) {
        this.makeupId = makeupId;
        this.dateOfClass = dateOfClass;
        this.dateMadeUp = dateMadeUp;
        this.projectDescription = projectDescription;
        this.minutesMadeUp = minutesMadeUp;
    }
    
    
    public Long getMakeupId() {
        return makeupId;
    }
    
    public void setMakeupId(Long makeupId) {
        this.makeupId = makeupId;
    }
    
    public Date getDateOfClass() {
        return dateOfClass;
    }
    
    public void setDateOfClass(Date dateOfClass) {
        this.dateOfClass = dateOfClass;
    }
    
    public Date getDateMadeUp() {
        return dateMadeUp;
    }
    
    public void setDateMadeUp(Date dateMadeUp) {
        this.dateMadeUp = dateMadeUp;
    }
    
    public String getProjectDescription() {
        return projectDescription;
    }
    
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }
    
    public int getMinutesMadeUp() {
        return minutesMadeUp;
    }
    
    public void setMinutesMadeUp(int minutesMadeUp) {
        this.minutesMadeUp = minutesMadeUp;
    }
    
    public boolean isToBeDeletedFlag() {
        return toBeDeletedFlag;
    }
    
    public void setToBeDeletedFlag(boolean toBeDeletedFlag) {
        this.toBeDeletedFlag = toBeDeletedFlag;
    }

    
    @Override
    public String toString() {
        return "MakeupModel [makeupId=" + makeupId + ", dateOfClass=" + dateOfClass + ", dateMadeUp=" + dateMadeUp
                + ", projectDescription=" + projectDescription + ", minutesMadeUp=" + minutesMadeUp
                + ", toBeDeletedFlag=" + toBeDeletedFlag + "]";
    }

}
