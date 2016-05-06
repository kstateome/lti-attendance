package edu.ksu.canvas.aviation.entity;

import edu.ksu.canvas.aviation.enums.Status;

import javax.persistence.*;

import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


@Entity
@Table(name = "aviation_attendance")
@Check(constraints="minutes_missed >= 0 and status IN ('PRESENT', 'TARDY', 'ABSENT', 'EXCUSED')")
public class Attendance implements Serializable {

    private static final long serialVersionUID = 1L;

    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "attendance_id")
    private Long attendanceId;

    @ManyToOne
    @JoinColumn(name="student_id", foreignKey = @ForeignKey(name = "fk_student"), nullable=false)
    private AviationStudent aviationStudent;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name="minutes_missed")
    private Integer minutesMissed;

    @Temporal(TemporalType.DATE)
    @Column(name="date_of_class")
    private Date dateOfClass;

    

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public AviationStudent getAviationStudent() {
        return aviationStudent;
    }

    public void setAviationStudent(AviationStudent aviationStudent) {
        this.aviationStudent = aviationStudent;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getMinutesMissed() {
        return minutesMissed;
    }

    public void setMinutesMissed(Integer minutesMissed) {
        this.minutesMissed = minutesMissed;
    }

    public Date getDateOfClass() {
        return dateOfClass;
    }

    public void setDateOfClass(Date dateOfClass) {
        this.dateOfClass = dateOfClass;
    }

    
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        
        return "Attendance [attendanceId=" + attendanceId + ", aviationStudent="
                + (aviationStudent == null ? null : aviationStudent.getStudentId()) + ", status=" + status
                + ", minutesMissed=" + minutesMissed + ", dateOfClass=" 
                + (dateOfClass == null ? null : sdf.format(dateOfClass)) + "]";
    }
    
}
