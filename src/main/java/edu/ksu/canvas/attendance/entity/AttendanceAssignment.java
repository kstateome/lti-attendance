package edu.ksu.canvas.attendance.entity;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "attendance_assignment")
public class AttendanceAssignment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "assignment_id")
    private Long assignmentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", foreignKey = @ForeignKey(name = "fk_section_attendance"), nullable = false, unique = true)
    private AttendanceSection attendanceSection;

    @Column(name = "canvas_assignment_id")
    private Long canvasAssignmentId;

    @Column(name = "assignment_points")
    private String assignmentPoints;

    @Column(name = "present_points")
    private String presentPoints;

    @Column(name = "tardy_points")
    private String tardyPoints;

    @Column(name = "excused_points")
    private String excusedPoints;

    @Column(name = "absent_points")
    private String absentPoints;

    @Column(name = "grading_on", nullable = false)
    private boolean gradingOn = false;

    @Column(name = "assignment_name")
    private String assignmentName;

    @Transient
    public Status status;

    public enum Status{
        UNKNOWN,
        OKAY,
        NOT_LINKED_TO_CANVAS,
        CANVAS_AND_DB_DISCREPANCY
    }


    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public AttendanceSection getAttendanceSection() {
        return attendanceSection;
    }

    public void setAttendanceSection(AttendanceSection attendanceSection) {
        this.attendanceSection = attendanceSection;
    }

    public Long getCanvasAssignmentId() {
        return canvasAssignmentId;
    }

    public void setCanvasAssignmentId(Long canvasAssignmentId) {
        this.canvasAssignmentId = canvasAssignmentId;
    }

    public String getAssignmentPoints() {
        return assignmentPoints;
    }

    public void setAssignmentPoints(String assignmentPoints) {
        this.assignmentPoints = assignmentPoints;
    }

    public String getPresentPoints() {
        return presentPoints;
    }

    public void setPresentPoints(String presentPoints) {
        this.presentPoints = presentPoints;
    }

    public String getTardyPoints() {
        return tardyPoints;
    }

    public void setTardyPoints(String tardyPoints) {
        this.tardyPoints = tardyPoints;
    }

    public String getExcusedPoints() {
        return excusedPoints;
    }

    public void setExcusedPoints(String excusedPoints) {
        this.excusedPoints = excusedPoints;
    }

    public String getAbsentPoints() {
        return absentPoints;
    }

    public void setAbsentPoints(String absentPoints) {
        this.absentPoints = absentPoints;
    }

    public boolean getGradingOn() {
        return gradingOn;
    }

    public void setGradingOn(boolean gradingOn) {
        this.gradingOn = gradingOn;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }



    @Override
    public String toString() {
        return "AttendanceAssignment [assignmentId=" + assignmentId + ", sectionId=" +
                (attendanceSection == null? null : attendanceSection.getSectionId()) + ", canvasAssignmentId="
                + canvasAssignmentId + ", assignmentPoints=" + assignmentPoints + ", presentPoints=" + presentPoints
                + ", tardyPoints=" + tardyPoints + ", excusedPoints=" + excusedPoints + ", absentPoints=" + absentPoints
                + ", gradingOn=" + gradingOn + ", assignmentName=" + assignmentName +"]";
    }
}
