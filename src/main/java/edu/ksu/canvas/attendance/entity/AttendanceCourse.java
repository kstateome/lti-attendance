package edu.ksu.canvas.attendance.entity;

import edu.ksu.canvas.attendance.enums.AttendanceType;
import org.hibernate.annotations.Check;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "aviation_course")
@Check(constraints = "default_minutes_per_session >= 0 and total_minutes >= 0")
public class AttendanceCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "total_minutes")
    private Integer totalMinutes;

    @Column(name = "default_minutes_per_session")
    private Integer defaultMinutesPerSession;

    @Column(name = "attendance_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AttendanceType attendanceType;

    @Column(name = "canvas_course_id", nullable = false, unique = true)
    private Long canvasCourseId;


    public AttendanceCourse() {
        this.attendanceType = AttendanceType.SIMPLE;
    }

    public AttendanceCourse(Long canvasCourseId, int totalClassMinutes, int defaultMinutesPerSession) {
        this.canvasCourseId = canvasCourseId;
        this.totalMinutes = totalClassMinutes;
        this.defaultMinutesPerSession = defaultMinutesPerSession;
        this.attendanceType = AttendanceType.SIMPLE;
    }


    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public Integer getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(int totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public Integer getDefaultMinutesPerSession() {
        return defaultMinutesPerSession;
    }

    public void setDefaultMinutesPerSession(int defaultMinutesPerSession) {
        this.defaultMinutesPerSession = defaultMinutesPerSession;
    }

    public Long getCanvasCourseId() {
        return canvasCourseId;
    }

    public void setCanvasCourseId(Long canvasCourseId) {
        this.canvasCourseId = canvasCourseId;
    }


    @Override
    public String toString() {
        return "AviationCourse [courseId=" + courseId + ", totalMinutes=" + totalMinutes + ", defaultMinutesPerSession="
                + defaultMinutesPerSession + ", canvasCourseId=" + canvasCourseId + "]";
    }

    public AttendanceType getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(AttendanceType attendanceType) {
        this.attendanceType = attendanceType;
    }
}