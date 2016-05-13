package edu.ksu.canvas.aviation.factory;


import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.model.SectionInfo;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;
import edu.ksu.canvas.enums.EnrollmentType;
import edu.ksu.canvas.interfaces.EnrollmentsReader;
import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


@Component
public class SectionInfoFactory {

    @Autowired
    private AviationStudentRepository studentRepository;


    @Deprecated
    public SectionInfo getSectionInfo(Section section, EnrollmentsReader enrollmentsReader) throws IOException {
        //Initialize a new section info with the basic information
        SectionInfo info = new SectionInfo(section);

        Set<AviationStudent> students = studentRepository.findBySectionId(info.getSectionId());
        //Add any students that don't already exist (in case a student was added/deleted)
        for (Enrollment e : enrollmentsReader.getSectionEnrollments(Math.toIntExact(info.getSectionId()), Collections.singletonList(EnrollmentType.STUDENT))) {
            AviationStudent student = new AviationStudent();
            student.setSisUserId(e.getUser().getSisUserId());
            student.setName(e.getUser().getSortableName());
            student.setSectionId(section.getId());
            student.setCanvasCourseId(section.getCourseId());
            students.add(student);
        }

        List<AviationStudent> studentList = new ArrayList<>(students);
        info.setStudents(studentList);
        info.setTotalStudents(studentList.size());


        return info;
    }
}
