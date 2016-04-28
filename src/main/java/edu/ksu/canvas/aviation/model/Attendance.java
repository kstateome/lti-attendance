package edu.ksu.canvas.aviation.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by shreyak
 */
@Entity
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private Status status;
    private int minutes;
    private List<Tracker> madeup;
    private double percentageMissed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStatus(Status status) { this.status = status; }

    public Status getStatus() { return status; }

    public int getMinutesMissed() {
        return minutes;
    }

    public void setMinutesMissed(int minutesMissed) {
        this.minutes = minutesMissed;
    }

    public void setMadeup(List<Tracker> madeup) {this.madeup = madeup;}

    public List<Tracker> getMadeup() {return madeup;}

    public double getPercentageMissed() {
        return percentageMissed;
    }

    // FIXME: This will be hardcoded for now, change later
    public void setPercentageMissed() {
        this.percentageMissed = (this.minutes/100);
    }
}
