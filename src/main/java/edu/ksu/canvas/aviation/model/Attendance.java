package edu.ksu.canvas.aviation.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "aviation_attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name="student_id", foreignKey = @ForeignKey(name = "fk_student"))
    private Student student;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name="minutes_missed")
    private int minutes;

    @Temporal(TemporalType.DATE)
    @Column(name="date_of_class")
    private Date dateOfClass;

    @OneToMany(mappedBy = "attendance")
    private List<MakeupTracker> madeupTracker;

    public void setStatus(Status status) { this.status = status; }

    public Status getStatus() { return status; }

    public int getMinutesMissed() {
        return minutes;
    }

    public void setMinutesMissed(int minutesMissed) {
        this.minutes = minutesMissed;
    }

    public void setMadeup(List<MakeupTracker> madeup) {this.madeupTracker = madeup;}

    public List<MakeupTracker> getMadeup() {return madeupTracker;}

    public Date getDateOfClass() {
        return dateOfClass;
    }

    public void setDateOfClass(Date dateOfClass) {
        this.dateOfClass = dateOfClass;
    }
}
