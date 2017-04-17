package edu.ksu.canvas.attendance.entity;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "attendance_assignment")
public class AttendanceAssignment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "assignment_id")
    private Long assignmentId; //attendance project's local section Id

    @JoinColumn(name = "section_id", foreignKey = @ForeignKey(name = "fk_section_attendance"))
    private Long sectionId; //attendance project's local section Id

    @Column(name = "canvas_assignment_id")
    private Long canvasAssignmentId;

    @Column(name = "assignment_points")
    private Integer assignmentPoints;

    @Column(name = "present_points")
    private Integer presentPoints;

    @Column(name = "tardy_points")
    private double tardyPoints;

    @Column(name = "excused_points")
    private double excusedPoints;

    @Column(name = "absent_points")
    private double absentPoints;

    @Column(name = "grading_on", nullable = false)
    private Boolean gradingOn = false;

    @Column(name = "assignment_name")
    private String assignmentName;

    public AttendanceAssignment() {
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
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

    public Integer getPresentPoints() {
        return presentPoints;
    }

    public void setPresentPoints(Integer presentPoints) {
        this.presentPoints = presentPoints;
    }

    public double getTardyPoints() {
        return tardyPoints;
    }

    public void setTardyPoints(double tardyPoints) {
        this.tardyPoints = tardyPoints;
    }

    public double getExcusedPoints() {
        return excusedPoints;
    }

    public void setExcusedPoints(double excusedPoints) {
        this.excusedPoints = excusedPoints;
    }

    public double getAbsentPoints() {
        return absentPoints;
    }

    public void setAbsentPoints(double absentPoints) {
        this.absentPoints = absentPoints;
    }

    public Boolean getGradingOn() {
        return gradingOn;
    }

    public void setGradingOn(Boolean gradingOn) {
        this.gradingOn = gradingOn;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    @Override
    public String toString() {
        return "AttendanceAssignment [assignmentId=" + assignmentId + ", sectionId=" + sectionId + ", canvasAssignmentId="
                + canvasAssignmentId + ", assignmentPoints=" + assignmentPoints + ", presentPoints=" + presentPoints
                + ", tardyPoints=" + tardyPoints + ", excusedPoints=" + excusedPoints + ", absentPoints=" + absentPoints
                + ", gradingOn=" + gradingOn + ", assignmentName=" + assignmentName +"]";
    }
}
