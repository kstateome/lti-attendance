package edu.ksu.canvas.aviation.entity;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Check;



@Entity
@Table(name = "aviation_course")
@Check(constraints="default_minutes_per_session >= 0 and total_minutes >= 0")
public class AviationCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "course_id")
    private long courseId;
    
    @Column(name="total_minutes")
    private int totalMinutes;
    
    @Column(name="default_minutes_per_session")
    private int defaultMinutesPerSession;
    
    @Column(name="canvas_course_id", nullable=false, unique=true)
    private long canvasCourseId;

    
    
    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public int getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(int totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public int getDefaultMinutesPerSession() {
        return defaultMinutesPerSession;
    }

    public void setDefaultMinutesPerSession(int defaultMinutesPerSession) {
        this.defaultMinutesPerSession = defaultMinutesPerSession;
    }

    public long getCanvasCourseId() {
        return canvasCourseId;
    }

    public void setCanvasCourseId(long canvasCourseId) {
        this.canvasCourseId = canvasCourseId;
    }
    
}