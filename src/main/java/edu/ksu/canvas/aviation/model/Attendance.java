package edu.ksu.canvas.aviation.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "aviation_attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "attendance_id")
    private long attendanceId;

    @ManyToOne
    @JoinColumn(name="student_id", foreignKey = @ForeignKey(name = "fk_student"))
    private Student student;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name="minutes_missed")
    private int minutesMissed;

    @Temporal(TemporalType.DATE)
    @Column(name="date_of_class")
    private Date dateOfClass;

    @OneToMany(mappedBy = "attendance")
    private List<MakeupTracker> madeupTracker;

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

    public void setMadeup(List<MakeupTracker> madeup) {
        this.madeupTracker = madeup;
    }

    public List<MakeupTracker> getMadeup() {
        return madeupTracker;
    }

    public Date getDateOfClass() {
        return dateOfClass;
    }

    public void setDateOfClass(Date dateOfClass) {
        this.dateOfClass = dateOfClass;
    }
}
