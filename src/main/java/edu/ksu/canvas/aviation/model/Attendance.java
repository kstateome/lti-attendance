package edu.ksu.canvas.aviation.model;

import java.util.Date;

/**
 * Created by shreyak
 */
public class Attendance {

    private long id;
    private boolean onTime;
    private int minutes;
    private Date dateMadeUp;
    private double percentageMissed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isOnTime() {
        return onTime;
    }

    public void setOnTime(boolean onTime) {
        this.onTime = onTime;
    }

    public int getMinutesMissed() {
        return minutes;
    }

    public void setMinutesMissed(int minutesMissed) {
        this.minutes = minutesMissed;
    }

    public Date getDateMadeUp() {
        return dateMadeUp;
    }

    public void setDateMadeUp(Date dateMadeUp) {
        this.dateMadeUp = dateMadeUp;
    }

    public double getPercentageMissed() {
        return percentageMissed;
    }

    public void setPercentageMissed(double percentageMissed) {
        this.percentageMissed = percentageMissed;
    }
}
