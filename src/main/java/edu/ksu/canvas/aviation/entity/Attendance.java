package edu.ksu.canvas.aviation.entity;

import edu.ksu.canvas.aviation.enums.Status;

import javax.persistence.*;

import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "aviation_attendance")
@Check(constraints="minutes_missed >= 0 and status IN ('PRESENT', 'TARDY', 'ABSENT', 'EXCUSED')")
public class Attendance implements Serializable {

    private static final long serialVersionUID = 1L;

    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "attendance_id")
    private long attendanceId;

    @ManyToOne
    @JoinColumn(name="student_id", foreignKey = @ForeignKey(name = "fk_student"), nullable=false)
    private Student student;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name="minutes_missed")
    private int minutesMissed;

    @Temporal(TemporalType.DATE)
    @Column(name="date_of_class")
    private Date dateOfClass;

    

    public long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public int getMinutesMissed() {
        return minutesMissed;
    }

    public void setMinutesMissed(int minutesMissed) {
        this.minutesMissed = minutesMissed;
    }

    public Date getDateOfClass() {
        return dateOfClass;
    }

    public void setDateOfClass(Date dateOfClass) {
        this.dateOfClass = dateOfClass;
    }
    
}
