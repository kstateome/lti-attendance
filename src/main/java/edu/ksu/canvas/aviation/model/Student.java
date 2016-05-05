package edu.ksu.canvas.aviation.model;

import javax.persistence.*;

import java.util.List;

@Entity
@Table(name = "aviation_student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "student_id")
    private long studentId; //aviation project's local student Id

    @Column(name = "sis_user_id", nullable = false)
    private String sisUserId; //institution's student ID, e.g. WID

    @Column(name = "student_name")
    private String name;

    @Column(name = "section_id")
    private long sectionID;

    @OneToMany(mappedBy = "student")
    private List<Attendance> attendances;

    @Transient
    private double percentageOfCourseMissed;

    public double getPercentageOfCourseMissed() {
        return percentageOfCourseMissed;
    }

    public void setPercentageOfCourseMissed(double percentageOfCourseMissed) {
        this.percentageOfCourseMissed = percentageOfCourseMissed;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSectionID(long sectionID) {
        this.sectionID = sectionID;
    }

    public long getSectionID() {
        return sectionID;
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
}
