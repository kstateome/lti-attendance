package edu.ksu.canvas.attendance.entity;

import org.hibernate.annotations.Check;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


@Entity
@Table(name = "attendance_makeup")
@Check(constraints = "minutes_madeup >= 0")
public class Makeup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "makeup_id")
    private Long makeupId;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_class")
    private Date dateOfClass;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_madeup")
    private Date dateMadeUp;

    @Column(name = "project_description")
    private String projectDescription;

    @Column(name = "minutes_madeup")
    private Integer minutesMadeUp;

    @ManyToOne
    @JoinColumn(name = "student_id", foreignKey = @ForeignKey(name = "fk_student_for_makeup"), nullable = false)
    private AttendanceStudent attendanceStudent;


    public Long getMakeupId() {
        return makeupId;
    }

    public void setId(Long makeupId) {
        this.makeupId = makeupId;
    }

    public Date getDateOfClass() {
        return dateOfClass;
    }

    public void setDateOfClass(Date dateOfClass) {
        this.dateOfClass = dateOfClass;
    }

    public Date getDateMadeUp() {
        return dateMadeUp;
    }

    public void setDateMadeUp(Date dateMadeUp) {
        this.dateMadeUp = dateMadeUp;
    }

    public Integer getMinutesMadeUp() {
        return minutesMadeUp;
    }

    public void setMinutesMadeUp(Integer minutesMadeUp) {
        this.minutesMadeUp = minutesMadeUp;
    }

    public AttendanceStudent getAttendanceStudent() {
        return attendanceStudent;
    }

    public void setAttendanceStudent(AttendanceStudent attendanceStudent) {
        this.attendanceStudent = attendanceStudent;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }


    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        return "Makeup [makeupId=" + makeupId + ", dateOfClass="
                + (dateOfClass == null ? null : sdf.format(dateOfClass)) + ", dateMadeUp="
                + (dateMadeUp == null ? null : sdf.format(dateMadeUp))
                + ", minutesMadeUp=" + minutesMadeUp + ", attendanceStudent="
                + (attendanceStudent == null ? null : attendanceStudent.getStudentId())
                + "projectDescription='" + projectDescription + "']";
    }

}
