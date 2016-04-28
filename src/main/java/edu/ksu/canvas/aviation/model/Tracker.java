package edu.ksu.canvas.aviation.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jesusorr on 4/28/16.
 */
@Entity
public class Tracker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private Date dayOfClass;
    private Date madeup;
    private int minutesMadeup;

    public void setId(long id) {this.id = id;}

    public long getId() {return id;}

    public void setDayOfClass(Date dayOfClass) {this.dayOfClass = dayOfClass;}

    public Date getDayOfClass() {return dayOfClass;}

    public void setMadeup(Date madeup) {this.madeup = madeup;}

    public Date getMadeup() {return madeup;}

    public void setMinutesMadeup(int minutesMadeup) {this.minutesMadeup = minutesMadeup;}

    public int getMinutesMadeup() {return minutesMadeup;}
}
