package edu.ksu.canvas.aviation.model;

import java.util.Date;
import java.util.List;

/**
 * Created by shreyak
 */
public class Day {

    private long id;
    private Date date;
    private List<Attendance> attendances;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Attendance> getAttendances() {
        return this.attendances;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
