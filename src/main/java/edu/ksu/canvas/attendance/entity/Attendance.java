package edu.ksu.canvas.attendance.entity;

import org.hibernate.annotations.Check;

import edu.ksu.canvas.attendance.enums.Status;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


@Entity
@Table(name = "attendance")
@Check(constraints = "minutes_missed >= 0 and status IN ('PRESENT', 'TARDY', 'ABSENT', 'EXCUSED', 'NA')")
public class Attendance implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "ATTENDANCE_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ATTENDANCE_SEQ", sequenceName = "ATTENDANCE_SEQ", allocationSize = 50)
    @Column(name = "attendance_id")
    private Long attendanceId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id", foreignKey = @ForeignKey(name = "fk_student_attendance"))
    private AttendanceStudent attendanceStudent;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "minutes_missed")
    private Integer minutesMissed;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_class")
    private Date dateOfClass;

    @Column(name = "notes")
    private String notes;


    public Attendance() {

    }

    public Attendance(AttendanceStudent attendanceStudent, Status status, Date dateOfClass) {
        this.attendanceStudent = attendanceStudent;
        this.status = status;
        this.dateOfClass = dateOfClass;
    }


    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public AttendanceStudent getAttendanceStudent() {
        return attendanceStudent;
    }

    public void setAttendanceStudent(AttendanceStudent attendanceStudent) {
        this.attendanceStudent = attendanceStudent;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
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
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        return "Attendance [attendanceId=" + attendanceId + ", attendanceStudent="
                + (attendanceStudent == null ? null : attendanceStudent.getStudentId()) + ", status=" + status
                + ", minutesMissed=" + minutesMissed + ", dateOfClass="
                + (dateOfClass == null ? null : sdf.format(dateOfClass)) + "]";
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
