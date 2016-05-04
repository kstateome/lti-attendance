package edu.ksu.canvas.aviation.model;

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
}
