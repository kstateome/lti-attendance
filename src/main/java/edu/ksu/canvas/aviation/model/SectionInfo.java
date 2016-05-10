package edu.ksu.canvas.aviation.model;

import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.enums.EnrollmentType;
import edu.ksu.canvas.interfaces.EnrollmentsReader;
import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Section;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SectionInfo {

    private long sectionId;
    private String sectionName;
    private Integer canvasCourseId;
    private Integer totalStudents;
    private List<AviationStudent> students;

    private static final Logger LOG = Logger.getLogger(SectionInfo.class);

    public SectionInfo(Section section, EnrollmentsReader enrollmentsReader) throws IOException {
        List<Enrollment> enrollments = enrollmentsReader.getSectionEnrollments((int) section.getId(), Collections.singletonList(EnrollmentType.STUDENT));
        List<AviationStudent> students = new ArrayList<>();
        for (Enrollment e : enrollments) {
            AviationStudent student = new AviationStudent();
            student.setSisUserId(e.getUser().getSisUserId());
            student.setName(e.getUser().getSortableName());
            student.setSectionId(section.getId());
            student.setCanvasCourseId(section.getCourseId());
            students.add(student);
        }
        totalStudents = students.size();
        if (students.size() > 0) {
            this.students = students;
            sectionId = section.getId();
            sectionName = section.getName();
            canvasCourseId = section.getCourseId();
        }
    }

    public SectionInfo() {}

    public long getSectionId() {
        return sectionId;
    }

    public void setSectionId(long sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Integer getCanvasCourseId() {
        return canvasCourseId;
    }

    public void setCanvasCourseId(Integer canvasCourseId) {
        this.canvasCourseId = canvasCourseId;
    }

    public Integer getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Integer totalStudents) {
        this.totalStudents = totalStudents;
    }

    public List<AviationStudent> getStudents() {
        return students;
    }

    public void setStudents(List<AviationStudent> students) {
        this.students = students;
    }

    @Override
    public String toString() {
        return "SectionInfo{" +
                "sectionId=" + sectionId +
                ", sectionName='" + sectionName + '\'' +
                ", canvasCourseId=" + canvasCourseId +
                ", totalStudents=" + totalStudents +
                ", students=" + students +
                '}';
    }
}
