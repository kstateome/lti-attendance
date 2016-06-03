package edu.ksu.canvas.aviation.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Entity
@Table(name = "aviation_student")
public class AviationStudent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "student_id")
    private Long studentId; //aviation project's local student Id

    // Canvas has the authoritative data.
    @Column(name = "sis_user_id", nullable = false)
    private String sisUserId; //institution's student ID, e.g. WID

    // Canvas has the authoritative data.
    @Column(name = "student_name")
    private String name;

    @Column(name = "canvas_course_id", nullable = false)
    private Long canvasCourseId;

    // Canvas has the authoritative data.
    @Column(name = "canvas_section_id", nullable = false)
    private Long canvasSectionId;

    @Column(name = "deleted")
    private Boolean deleted;

    @OneToMany(mappedBy = "aviationStudent")
    private List<Attendance> attendances;


    @Transient
    private Double percentageOfCourseMissed;

    public Double getPercentageOfCourseMissed() {
        return percentageOfCourseMissed;
    }

    public void setPercentageOfCourseMissed(double percentageOfCourseMissed) {
        this.percentageOfCourseMissed = percentageOfCourseMissed;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCanvasSectionId(Long canvasSectionId) {
        this.canvasSectionId = canvasSectionId;
    }

    public Long getCanvasSectionId() {
        return canvasSectionId;
    }

    public List<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances;
    }

    public String getSisUserId() {
        return sisUserId;
    }

    public void setSisUserId(String sisUserId) {
        this.sisUserId = sisUserId;
    }

    public Long getCanvasCourseId() {
        return canvasCourseId;
    }

    public void setCanvasCourseId(Long canvasCourseId) {
        this.canvasCourseId = canvasCourseId;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }


    @Override
    public String toString() {
        return "Student [studentId=" + studentId + ", sisUserId=" + sisUserId + ", name=" + name + ", canvasCourseId="
                + canvasCourseId + ", canvasSectionId=" + canvasSectionId + ", percentageOfCourseMissed="
                + percentageOfCourseMissed + ", deleted=" + deleted + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AviationStudent that = (AviationStudent) o;

        if (canvasCourseId != null ? !canvasCourseId.equals(that.canvasCourseId) : that.canvasCourseId != null)
            return false;
        if (canvasSectionId != null ? !canvasSectionId.equals(that.canvasSectionId) : that.canvasSectionId != null)
            return false;
        if (deleted != null ? !deleted.equals(that.deleted) : that.deleted != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (sisUserId != null ? !sisUserId.equals(that.sisUserId) : that.sisUserId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sisUserId != null ? sisUserId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (canvasCourseId != null ? canvasCourseId.hashCode() : 0);
        result = 31 * result + (canvasSectionId != null ? canvasSectionId.hashCode() : 0);
        result = 31 * result + (deleted != null ? deleted.hashCode() : 0);
        return result;
    }
}
