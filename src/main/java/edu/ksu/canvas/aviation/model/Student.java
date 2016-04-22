package edu.ksu.canvas.aviation.model;

import java.util.List;

/**
 * Created by allanjay808
 */
public class Student {

    private long id;
    private List<Attendance> attendances;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances;
    }
}
