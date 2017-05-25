package edu.ksu.canvas.attendance.form;

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

    private Boolean showNotesToStudents;

    private Double assignmentPoints;

    private Double presentPoints;

    private Double tardyPoints;

    private Double excusedPoints;

    private Double absentPoints;

    private Boolean gradingOn;

    private String assignmentName;


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

    public Boolean getShowNotesToStudents() {
        return showNotesToStudents;
    }

    public void setShowNotesToStudents(Boolean showNotesToStudents) {
        this.showNotesToStudents = showNotesToStudents;
    }

    public void setSimpleAttendance(Boolean simpleAttendance) {
        this.simpleAttendance = simpleAttendance;
    }

    public Double getAssignmentPoints() {
        return assignmentPoints;
    }

    public void setAssignmentPoints(Double assignmentPoints) {
        this.assignmentPoints = assignmentPoints;
    }

    public Double getPresentPoints() {
        return presentPoints;
    }

    public void setPresentPoints(Double presentPoints) {
        this.presentPoints = presentPoints;
    }

    public Double getTardyPoints() {
        return tardyPoints;
    }

    public void setTardyPoints(Double tardyPoints) {
        this.tardyPoints = tardyPoints;
    }

    public Double getExcusedPoints() {
        return excusedPoints;
    }

    public void setExcusedPoints(Double excusedPoints) {
        this.excusedPoints = excusedPoints;
    }

    public Double getAbsentPoints() {
        return absentPoints;
    }

    public void setAbsentPoints(Double absentPoints) {
        this.absentPoints = absentPoints;
    }

    public Boolean getGradingOn() {
        return gradingOn;
    }

    public void setGradingOn(Boolean gradingOn) {
        this.gradingOn = gradingOn;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }
}
