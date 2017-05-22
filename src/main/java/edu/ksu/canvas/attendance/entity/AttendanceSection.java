package edu.ksu.canvas.attendance.entity;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "attendance_section")
public class AttendanceSection implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "section_id")
    private Long sectionId; //attendance project's local section Id

    // Canvas has the authoritative data.
    @Column(name = "canvas_course_id", nullable = false)
    private Long canvasCourseId;

    @Column(name = "canvas_section_id", nullable = false)
    private Long canvasSectionId;

    // Canvas has the authoritative data.
    @Column(name = "section_name")
    private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private AttendanceAssignment attendanceAssignment;

    public AttendanceSection() {

    }


    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getCanvasCourseId() {
        return canvasCourseId;
    }

    public void setCanvasCourseId(Long canvasCourseId) {
        this.canvasCourseId = canvasCourseId;
    }

    public Long getCanvasSectionId() {
        return canvasSectionId;
    }

    public void setCanvasSectionId(Long canvasSectionId) {
        this.canvasSectionId = canvasSectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AttendanceAssignment getAttendanceAssignment() {
        return attendanceAssignment;
    }


    public void setAttendanceAssignment(AttendanceAssignment attendanceAssignment) {
        this.attendanceAssignment = attendanceAssignment;
    }


    @Override
    public String toString() {
        return "AttendanceSection [sectionId=" + sectionId + ", canvasCourseId=" + canvasCourseId + ", canvasSectionId="
                + canvasSectionId + ", name=" + name +"]";
    }

}
