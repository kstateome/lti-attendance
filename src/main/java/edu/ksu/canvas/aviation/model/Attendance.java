package edu.ksu.canvas.aviation.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyak
 */
public class Attendance {

    private long id;
    private boolean onTime;
    private int minutes;

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

}
