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

    @Column(name = "canvas_assignment_id")
    private Long canvasAssignmentId;

    @Column(name = "assignment_points")
    private Integer assignmentPoints;

    @Column(name = "grading_on", nullable = false)
    private Boolean gradingOn = false;


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

    public Long getCanvasAssignmentId() {
        return canvasAssignmentId;
    }

    public void setCanvasAssignmentId(Long canvasAssignmentId) {
        this.canvasAssignmentId = canvasAssignmentId;
    }

    public Integer getAssignmentPoints() {
        return assignmentPoints;
    }

    public void setAssignmentPoints(Integer assignmentPoints) {
        this.assignmentPoints = assignmentPoints;
    }

    public Boolean getGradingOn() {
        return gradingOn;
    }

    public void setGradingOn(Boolean gradingOn) {
        this.gradingOn = gradingOn;
    }


    @Override
    public String toString() {
        return "AttendanceSection [sectionId=" + sectionId + ", canvasCourseId=" + canvasCourseId + ", canvasSectionId="
                + canvasSectionId + ", name=" + name + ", gradingOn=" + gradingOn + ", canvasAssignmentId=" + canvasAssignmentId
                + ", assignmentPoints=" + assignmentPoints +"]";
    }

}
