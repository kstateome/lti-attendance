package edu.ksu.canvas.aviation.entity;

import edu.ksu.canvas.aviation.entity.Attendance;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "aviation_makeup_tracker")
public class MakeupTracker {

    @ManyToOne
    @JoinColumn(name="attendance_id", foreignKey = @ForeignKey(name = "fk_attendance"))
    private Attendance attendance;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_class")
    private Date dateOfClass;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_madeup")
    private Date dateMadeUp;

    @Column(name = "minutes_madeup")
    private int minutesMadeUp;

    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateOfClass() {
        return dateOfClass;
    }

    public void setDateOfClass(Date dateOfClass) {
        this.dateOfClass = dateOfClass;
    }

    public int getMinutesMadeUp() {
        return minutesMadeUp;
    }

    public void setMinutesMadeUp(int minutesMadeUp) {
        this.minutesMadeUp = minutesMadeUp;
    }

    public Date getDateMadeUp() {
        return dateMadeUp;
    }

    public void setDateMadeUp(Date dateMadeUp) {
        this.dateMadeUp = dateMadeUp;
    }
}
