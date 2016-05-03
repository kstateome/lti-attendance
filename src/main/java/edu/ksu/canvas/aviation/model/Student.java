package edu.ksu.canvas.aviation.model;

import javax.persistence.*;

import java.util.List;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "student_id")
    private int studentId;

    @Column
    private String name;

    @Column(name = "section_id")
    private long sectionID;

    @OneToMany
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
}
