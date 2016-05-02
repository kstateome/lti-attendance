package edu.ksu.canvas.aviation.model;

import javax.persistence.Column;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

public class MakeupTracker {

    @ManyToOne
    @JoinColumn(name="attendance_id", foreignKey = @ForeignKey(name = "fk_attendance"))
    private Attendance attendance;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "date_of_class")
    private Date dateOfClass;

    @Column(name = "date_madeup")
    private Date dateMadeUp;

    @Column(name = "minutes_madeup")
    private int minutesMadeUp;
}
