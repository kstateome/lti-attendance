package edu.ksu.canvas.attendance.repository;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class that represents the data extracted from one attendance.
 * This is needed in order to organize and group comments by student.
 */
public class AttendanceCommentEntry implements Serializable{
    private Long studentId;
    private String comment;

    public AttendanceCommentEntry(Long studentId, Date date, String note) {
        this.studentId = studentId;
        this.comment = new SimpleDateFormat("MM/dd/yyyy").format(date)+ ": " + note;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}