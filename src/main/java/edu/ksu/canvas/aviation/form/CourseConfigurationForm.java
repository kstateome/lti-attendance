package edu.ksu.canvas.aviation.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


public class CourseConfigurationForm {

    @NotNull()
    @Min(1)
    private int totalClassMinutes;

    @NotNull()
    @Min(1)
    private int defaultMinutesPerSession;

    private Boolean simpleAttendance;


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

    public void setSimpleAttendance(boolean simpleAttendance) {
        this.simpleAttendance = simpleAttendance;
    }

    public Boolean getSimpleAttendance() {
        return simpleAttendance;
    }
}
