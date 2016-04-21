package edu.ksu.canvas.aviation.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyak on 4/21/16.
 */
public class Attendance {

    private List<Day> days = new ArrayList<>();

    public void setDays(List<Day> days){ this.days = days; }

    public List<Day> getDays() { return days; }

    public void addDay(Day day) { this.days.add(day); }

    public void removeDay(Day day) { this.days.remove(day); }

    public String toString() {
        StringBuffer stb = new StringBuffer();
        stb.append("Days: \r\n");
        for (Day day : this.getDays()){
            stb.append(day.toSring());
        }
        return stb.toString();
    }
}
