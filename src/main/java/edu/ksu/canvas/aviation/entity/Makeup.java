package edu.ksu.canvas.aviation.entity;

import javax.persistence.*;

import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


@Entity
@Table(name = "aviation_makeup_tracker")
@Check(constraints="minutes_madeup >= 0")
public class MakeupTracker implements Serializable {

    private static final long serialVersionUID = 1L;

    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "makeup_tracker_id")
    private Long makeupTrackerId;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_class")
    private Date dateOfClass;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_madeup")
    private Date dateMadeUp;
    
    @Column(name = "project_description")
    private String projectDescription;

    @Column(name = "minutes_madeup")
    private int minutesMadeUp;

    @ManyToOne
    @JoinColumn(name="student_id", foreignKey = @ForeignKey(name = "fk_student_for_makeup_tracker"), nullable=false)
    private AviationStudent aviationStudent;

    
    
    public Long getMakeupTrackerId() {
        return makeupTrackerId;
    }

    public void setMakeupTrackerId(Long makeupTrackerId) {
        this.makeupTrackerId = makeupTrackerId;
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

    public AviationStudent getAviationStudent() {
        return aviationStudent;
    }

    public void setAviationStudent(AviationStudent aviationStudent) {
        this.aviationStudent = aviationStudent;
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
        
        return "MakeupTracker [makeupTrackerId=" + makeupTrackerId + ", dateOfClass=" 
                + (dateOfClass == null ? null : sdf.format(dateOfClass)) + ", dateMadeUp="
                + (dateMadeUp == null ? null : sdf.format(dateMadeUp)) 
                + ", minutesMadeUp=" + minutesMadeUp + ", aviationStudent="
                + (aviationStudent == null ? null : aviationStudent.getStudentId())
                + "projectDescription='" + projectDescription + "']";
    }
      
}
