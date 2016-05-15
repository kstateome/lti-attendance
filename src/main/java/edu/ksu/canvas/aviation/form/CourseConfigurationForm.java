package edu.ksu.canvas.aviation.form;


public class CourseConfigurationForm {

    private int totalClassMinutes;
    private int defaultMinutesPerSession;
    
    
    public int getTotalClassMinutes() {
        return totalClassMinutes;
    }
    
    public void setTotalClassMinutes(int totalClassMinutes) {
        this.totalClassMinutes = totalClassMinutes;
    }
    
    public int getDefaultMinutesPerSession() {
        return defaultMinutesPerSession;
    }
    
    public void setDefaultMinutesPerSession(int defaultMinutesPerSession) {
        this.defaultMinutesPerSession = defaultMinutesPerSession;
    }
    
}
