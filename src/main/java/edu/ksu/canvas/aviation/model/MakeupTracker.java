package edu.ksu.canvas.aviation.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "aviation_makeup_tracker")
public class MakeupTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int makeupTrackerId;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_class")
    private Date dateOfClass;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_madeup")
    private Date dateMadeUp;

    @Column(name = "minutes_madeup")
    private int minutesMadeUp;

    @ManyToOne
    @JoinColumn(name="attendance_id", foreignKey = @ForeignKey(name = "fk_attendance"))
    private Attendance attendance;

    public int getMakeupTrackerId() {
        return makeupTrackerId;
    }

    public void setMakeupTrackerId(int makeupTrackerId) {
        this.makeupTrackerId = makeupTrackerId;
    }

    public Date getDateOfClass() {
        return dateOfClass;
    }

    public void setDateOfClass(Date dateOfClass) {
        this.dateOfClass = dateOfClass;
    }

    public Date getDateMadeUp() {
        return dateMadeUp;
    }

    public void setDateMadeUp(Date dateMadeUp) {
        this.dateMadeUp = dateMadeUp;
    }

    public int getMinutesMadeUp() {
        return minutesMadeUp;
    }

    public void setMinutesMadeUp(int minutesMadeUp) {
        this.minutesMadeUp = minutesMadeUp;
    }

    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }
}
