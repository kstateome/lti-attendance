package edu.ksu.canvas.aviation.entity;

import edu.ksu.canvas.aviation.entity.Attendance;

import javax.persistence.*;

import java.util.List;

@Entity
@Table(name = "aviation_student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "student_id")
    private String studentId;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
