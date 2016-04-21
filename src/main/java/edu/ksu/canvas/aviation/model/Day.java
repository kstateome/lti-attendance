package edu.ksu.canvas.aviation.model;

import java.util.Date;

/**
 * Created by shreyak on 4/21/16.
 */
public class Day {

    private Date date;
    private int minutesMissed;
    private Status status;

    public void setDate(Date date) { this.date = date; }

    public void setMinutesMissed(int minutesMissed) { this.minutesMissed = minutesMissed; }

    public void setStatus(Status status) { this.status = status; }

    public Date getDate() { return date; }

    public int getMinutesMissed() { return minutesMissed; }

    public Status getStatus() { return status; }

    public String toSring() {
        StringBuffer stb  = new StringBuffer();
        stb.append("Date: "+getDate()+"\r\n");
        stb.append("Minutes: "+ getMinutesMissed()+"\r\n");
        stb.append("Status: "+getStatus() + "\r\n");
        return stb.toString();
    }
}
