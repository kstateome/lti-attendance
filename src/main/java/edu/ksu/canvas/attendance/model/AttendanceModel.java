package edu.ksu.canvas.attendance.model;

import edu.ksu.canvas.attendance.entity.Attendance;
import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import edu.ksu.canvas.attendance.enums.Status;

import java.util.Date;


public class AttendanceModel {

    private Long attendanceId;
    private Long attendanceStudentId;
    private String attendanceStudentName;
    private String attendanceStudentSisUserId;
    private Status status;
    private Integer minutesMissed;
    private Date dateOfClass;
    private Boolean dropped;
    private String notes;


    public AttendanceModel() {

    }

    public AttendanceModel(AttendanceStudent student, Status status, Date dateOfClass) {
        this.attendanceStudentId = student == null ? null : student.getStudentId();
        this.attendanceStudentName = student == null ? null : student.getName();
        this.attendanceStudentSisUserId = student == null ? null : student.getSisUserId();
        this.status = status;
        this.dateOfClass = dateOfClass;
        this.dropped = student == null ? null : student.getDeleted();
        this.notes = "";
    }

    public AttendanceModel(Attendance attendance) {
        if (attendance == null) {
            return;
        }

        this.attendanceId = attendance.getAttendanceId();
        this.attendanceStudentId = attendance.getAttendanceStudent() == null ? null : attendance.getAttendanceStudent().getStudentId();
        this.attendanceStudentName = attendance.getAttendanceStudent() == null ? null : attendance.getAttendanceStudent().getName();
        this.attendanceStudentSisUserId = attendance.getAttendanceStudent() == null ? null : attendance.getAttendanceStudent().getSisUserId();
        this.status = attendance.getStatus();
        this.minutesMissed = attendance.getMinutesMissed();
        this.dateOfClass = attendance.getDateOfClass();
        this.dropped = attendance.getAttendanceStudent() == null ? null : attendance.getAttendanceStudent().getDeleted();
        this.notes = attendance.getNotes();
    }


    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Long getAttendanceStudentId() {
        return attendanceStudentId;
    }

    public String getAttendanceStudentName() {
        return attendanceStudentName;
    }

    public void setAttendanceStudentName(String attendanceStudentName) {
        this.attendanceStudentName = attendanceStudentName;
    }

    public String getAttendanceStudentSisUserId() {
        return attendanceStudentSisUserId;
    }

    public void setAttendanceStudentSisUserId(String attendanceStudentSisUserId) {
        this.attendanceStudentSisUserId = attendanceStudentSisUserId;
    }

    public void setAttendanceStudentId(Long attendanceStudentId) {
        this.attendanceStudentId = attendanceStudentId;
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

    public Boolean getDropped() {
        return dropped;
    }

    public void setDropped(Boolean dropped) {
        this.dropped = dropped;
    }
    
    public String getNotes() {
    	return notes;
    }

    public void setNotes(String notes) {
    	this.notes = notes;
    }


    @Override
    public String toString() {
        return "AttendanceModel [attendanceId=" + attendanceId + ", attendanceStudentId=" + attendanceStudentId
                + ", attendanceStudentName=" + attendanceStudentName + ", attendanceStudentSisUserId="
                + attendanceStudentSisUserId + ", status=" + status + ", minutesMissed=" + minutesMissed
                + ", dateOfClass=" + dateOfClass + "]";
    }

}
