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

/*    @OneToOne(cascade = CascadeType.ALL)*/
    @JoinColumn(name = "section_id", foreignKey = @ForeignKey(name = "fk_section_attendance"), nullable = false)
    private Long sectionId; //attendance project's local section Id

    @Column(name = "canvas_assignment_id")
    private Long canvasAssignmentId;

    @Column(name = "assignment_points")
    private Double assignmentPoints;

    @Column(name = "present_points")
    private Double presentPoints;

    @Column(name = "tardy_points")
    private Double tardyPoints;

    @Column(name = "excused_points")
    private Double excusedPoints;

    @Column(name = "absent_points")
    private Double absentPoints;

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

    public Double getAssignmentPoints() {
        return assignmentPoints;
    }

    public void setAssignmentPoints(Double assignmentPoints) {
        this.assignmentPoints = assignmentPoints;
    }

    public Double getPresentPoints() {
        return presentPoints;
    }

    public void setPresentPoints(Double presentPoints) {
        this.presentPoints = presentPoints;
    }

    public Double getTardyPoints() {
        return tardyPoints;
    }

    public void setTardyPoints(Double tardyPoints) {
        this.tardyPoints = tardyPoints;
    }

    public Double getExcusedPoints() {
        return excusedPoints;
    }

    public void setExcusedPoints(Double excusedPoints) {
        this.excusedPoints = excusedPoints;
    }

    public Double getAbsentPoints() {
        return absentPoints;
    }

    public void setAbsentPoints(Double absentPoints) {
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
