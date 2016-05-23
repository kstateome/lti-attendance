package edu.ksu.canvas.aviation.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.aviation.entity.AviationCourse;
import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.repository.AviationCourseRepository;
import edu.ksu.canvas.aviation.repository.AviationSectionRepository;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;
import edu.ksu.canvas.entity.lti.OauthToken;
import edu.ksu.canvas.enums.EnrollmentType;
import edu.ksu.canvas.enums.SectionIncludes;
import edu.ksu.canvas.exception.InvalidOauthTokenException;
import edu.ksu.canvas.interfaces.EnrollmentsReader;
import edu.ksu.canvas.interfaces.SectionReader;
import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Section;
import edu.ksu.lti.model.LtiSession;


@Component
public class SynchronizationService {

    private static final int DEFAULT_TOTAL_CLASS_MINUTES = 2160; //DEFAULT_MINUTES_PER_CLASS * 3 days a week * 16 weeks
    private static final int DEFAULT_MINUTES_PER_CLASS = 45;

    @Autowired
    private AviationCourseRepository aviationCourseRepository;

    @Autowired
    private AviationStudentRepository studentRepository;

    @Autowired
    private AviationSectionRepository sectionRepository;

    @Autowired
    protected CanvasApiFactory canvasApiFactory;


    public void synchronizeWhenCourseNotExistsInDB(LtiSession ltiSession, long canvasCourseId) throws IOException {
        if (aviationCourseRepository.findByCanvasCourseId(canvasCourseId) == null) {
            synchronize(ltiSession, canvasCourseId);
        }
    }

    /**
     * Synchronizes Canvas related information to the database
     */
    public void synchronize(LtiSession ltiSession, long canvasCourseId) throws IOException {
        OauthToken oauthToken = ltiSession.getCanvasOauthToken();

        EnrollmentsReader enrollmentsReader = canvasApiFactory.getReader(EnrollmentsReader.class, oauthToken.getToken());

        String courseID = ltiSession.getCanvasCourseId();
        SectionReader sectionReader = canvasApiFactory.getReader(SectionReader.class, oauthToken.getToken());
        List<Section> sections = sectionReader.listCourseSections(Integer.parseInt(courseID), Collections.singletonList(SectionIncludes.students));

        synchronizeCourseFromCanvasToDb(Long.valueOf(canvasCourseId));
        synchronizeSectionsFromCanvasToDb(sections);
        synchronizeStudentsFromCanvasToDb(sections, enrollmentsReader);
    }

    private AviationCourse synchronizeCourseFromCanvasToDb(long canvasCourseId) {
        AviationCourse aviationCourse = aviationCourseRepository.findByCanvasCourseId(canvasCourseId);

        if (aviationCourse == null) {
            aviationCourse = new AviationCourse();
            aviationCourse.setTotalMinutes(DEFAULT_TOTAL_CLASS_MINUTES);
            aviationCourse.setDefaultMinutesPerSession(DEFAULT_MINUTES_PER_CLASS);
            aviationCourse.setCanvasCourseId(canvasCourseId);
        }

        return aviationCourseRepository.save(aviationCourse);
    }

    private List<AviationSection> synchronizeSectionsFromCanvasToDb(List<Section> sections) {
        List<AviationSection> ret = new ArrayList<>();

        for (Section section : sections) {
            AviationSection aviationSection = sectionRepository.findByCanvasSectionId(Long.valueOf(section.getId()));

            if (aviationSection == null) {
                aviationSection = new AviationSection();
            }

            aviationSection.setName(section.getName());
            aviationSection.setCanvasSectionId(Long.valueOf(section.getId()));
            aviationSection.setCanvasCourseId(Long.valueOf(section.getCourseId()));

            sectionRepository.save(aviationSection);
        }

        return ret;
    }


    private List<AviationStudent> synchronizeStudentsFromCanvasToDb(List<Section> sections, EnrollmentsReader enrollmentsReader) throws InvalidOauthTokenException, IOException {
        List<AviationStudent> ret = new ArrayList<>();

        for (Section section : sections) {
            List<AviationStudent> existingStudents = studentRepository.findBySectionIdOrderByNameAsc(section.getId());

            for (Enrollment enrollment : enrollmentsReader.getSectionEnrollments((int) section.getId(), Collections.singletonList(EnrollmentType.STUDENT))) {
                List<AviationStudent> foundUsers = existingStudents.stream().filter(u -> u.getSisUserId().equals(enrollment.getUser().getSisUserId())).collect(Collectors.toList());

                if (foundUsers.isEmpty()) {
                    AviationStudent student = new AviationStudent();
                    student.setSisUserId(enrollment.getUser().getSisUserId());
                    student.setName(enrollment.getUser().getSortableName());
                    student.setSectionId(section.getId());
                    student.setCanvasCourseId(section.getCourseId());

                    studentRepository.save(student);
                    ret.add(student);
                } else {
                    ret.addAll(foundUsers);
                }
            }
        }

        return ret;
    }

}
