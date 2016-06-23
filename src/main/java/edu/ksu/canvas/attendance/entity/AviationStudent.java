package edu.ksu.canvas.attendance.entity;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;


@Entity
@Table(name = "aviation_student")
public class AviationStudent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "student_id")
    private Long studentId; //attendance project's local student Id

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

    @Column(name = "deleted", columnDefinition = "NUMBER(1,0) default 0", nullable = false)
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

    public AviationStudent() {
        deleted = false;
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

    public Boolean getDeleted() {
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
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AviationStudent that = (AviationStudent) o;
        return new EqualsBuilder()
                .append(canvasCourseId, that.canvasCourseId)
                .append(canvasSectionId, that.canvasSectionId)
                .append(deleted, that.deleted)
                .append(name, that.name)
                .append(sisUserId, that.sisUserId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,31)
                .append(canvasCourseId)
                .append(canvasSectionId)
                .append(deleted)
                .append(name)
                .append(sisUserId)
                .toHashCode();
    }
}
