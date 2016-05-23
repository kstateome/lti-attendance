package edu.ksu.canvas.aviation.model;

import java.util.Date;

import edu.ksu.canvas.aviation.entity.Attendance;
import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.enums.Status;


public class AttendanceModel {

    private Long attendanceId;
    private Long aviationStudentId;
    private String aviationStudentName;
    private String aviationStudentSisUserId;
    private Status status;
    private Integer minutesMissed;
    private Date dateOfClass;


    public AttendanceModel() { }

    public AttendanceModel(AviationStudent student, Status status, Date dateOfClass) {
        this.aviationStudentId = student == null ? null : student.getStudentId();
        this.aviationStudentName = student == null ? null : student.getName();
        this.aviationStudentSisUserId = student == null ? null : student.getSisUserId();
        this.status = status;
        this.dateOfClass = dateOfClass;
    }

    public AttendanceModel(Attendance attendance) {
        if (attendance == null) {
            return;
        }

        this.attendanceId = attendance.getAttendanceId();
        this.aviationStudentId = attendance.getAviationStudent() == null ? null : attendance.getAviationStudent().getStudentId();
        this.aviationStudentName = attendance.getAviationStudent() == null ? null : attendance.getAviationStudent().getName();
        this.aviationStudentSisUserId = attendance.getAviationStudent() == null ? null : attendance.getAviationStudent().getSisUserId();
        this.status = attendance.getStatus();
        this.minutesMissed = attendance.getMinutesMissed();
        this.dateOfClass = attendance.getDateOfClass();
    }


    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Long getAviationStudentId() {
        return aviationStudentId;
    }

    public String getAviationStudentName() {
        return aviationStudentName;
    }

    public void setAviationStudentName(String aviationStudentName) {
        this.aviationStudentName = aviationStudentName;
    }

    public String getAviationStudentSisUserId() {
        return aviationStudentSisUserId;
    }

    public void setAviationStudentSisUserId(String aviationStudentSisUserId) {
        this.aviationStudentSisUserId = aviationStudentSisUserId;
    }

    public void setAviationStudentId(Long aviationStudentId) {
        this.aviationStudentId = aviationStudentId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getMinutesMissed() {
        return minutesMissed;
    }

    public void setMinutesMissed(Integer minutesMissed) {
        this.minutesMissed = minutesMissed;
    }

    public Date getDateOfClass() {
        return dateOfClass;
    }

    public void setDateOfClass(Date dateOfClass) {
        this.dateOfClass = dateOfClass;
    }


    @Override
    public String toString() {
        return "AttendanceModel [attendanceId=" + attendanceId + ", aviationStudentId=" + aviationStudentId
                + ", aviationStudentName=" + aviationStudentName + ", aviationStudentSisUserId="
                + aviationStudentSisUserId + ", status=" + status + ", minutesMissed=" + minutesMissed
                + ", dateOfClass=" + dateOfClass + "]";
    }

}
