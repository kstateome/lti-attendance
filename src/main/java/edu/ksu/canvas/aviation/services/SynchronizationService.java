package edu.ksu.canvas.aviation.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
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

    public static final int DEFAULT_TOTAL_CLASS_MINUTES = 2160; //DEFAULT_MINUTES_PER_CLASS * 3 days a week * 16 weeks
    public static final int DEFAULT_MINUTES_PER_CLASS = 45;

    @Autowired
    private AviationCourseRepository aviationCourseRepository;

    @Autowired
    private AviationStudentRepository studentRepository;

    @Autowired
    private AviationSectionRepository sectionRepository;

    @Autowired
    protected CanvasApiFactory canvasApiFactory;


    /**
     * @throws NullPointerException when ltiSession parameter is null
     */
    public void synchronizeWhenCourseNotExistsInDB(LtiSession ltiSession, long canvasCourseId) throws IOException {
        Validate.notNull(ltiSession, "The ltiSession parameter must not be null");
        
        if (aviationCourseRepository.findByCanvasCourseId(canvasCourseId) == null) {
            synchronize(ltiSession, canvasCourseId);
        }
    }

    /**
     * Synchronizes Canvas related information to the database
     * 
     * @throws NullPointerException when ltiSession parameter is null
     */
    public void synchronize(LtiSession ltiSession, long canvasCourseId) throws IOException {
        Validate.notNull(ltiSession, "The ltiSession parameter must not be null");

        OauthToken oauthToken = ltiSession.getCanvasOauthToken();
        SectionReader sectionReader = canvasApiFactory.getReader(SectionReader.class, oauthToken.getToken());
        List<Section> sections = sectionReader.listCourseSections(Long.valueOf(canvasCourseId).intValue(), Collections.singletonList(SectionIncludes.students));

        synchronizeCourseFromCanvasToDb(Long.valueOf(canvasCourseId));
        synchronizeSectionsFromCanvasToDb(sections);

        EnrollmentsReader enrollmentsReader = canvasApiFactory.getReader(EnrollmentsReader.class, oauthToken.getToken());
        Map<Section,List<Enrollment>> canvasSectionMap = getEnrollmentsFromCanvas(sections, enrollmentsReader);
        synchronizeStudentsFromCanvasToDb(canvasSectionMap);
    }

    private Map<Section,List<Enrollment>> getEnrollmentsFromCanvas(List<Section> sections, EnrollmentsReader enrollmentsReader) throws InvalidOauthTokenException, IOException {
        Map<Section, List<Enrollment>> ret = new HashMap<>();

        if(sections == null) return null;
        for (Section section : sections) {
            for (Enrollment enrollment : enrollmentsReader.getSectionEnrollments((int) section.getId(), Collections.singletonList(EnrollmentType.STUDENT))) {
                List<Enrollment> enrollments = ret.get(section);

                if(enrollments == null) {
                    enrollments = new ArrayList<>();
                    ret.put(section, enrollments);
                }

                enrollments.add(enrollment);
            }
        }

        return ret;
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

            ret.add(sectionRepository.save(aviationSection));
        }

        return ret;
    }

    // ToDo: Determine what to do with drops...
    private List<AviationStudent> synchronizeStudentsFromCanvasToDb(Map<Section, List<Enrollment>> canvasSectionMap) {
        List<AviationStudent> ret = new ArrayList<>();
        List<AviationStudent> existingStudentsInDb = null;

        for(Section section: canvasSectionMap.keySet()) {

            if(existingStudentsInDb == null) {
                existingStudentsInDb = studentRepository.findByCanvasCourseId(section.getCourseId());
            }

            for(Enrollment enrollment: canvasSectionMap.get(section)) {

                Optional<AviationStudent> foundUser = 
                        existingStudentsInDb.stream()
                                        .filter(u -> u.getSisUserId().equals(enrollment.getUser().getSisUserId()))
                                        .findFirst();

                AviationStudent student = foundUser.isPresent() ? foundUser.get() : new AviationStudent();
                student.setSisUserId(enrollment.getUser().getSisUserId());
                student.setName(enrollment.getUser().getSortableName());
                student.setSectionId(section.getId());
                student.setCanvasCourseId(section.getCourseId() == null ? null : Long.valueOf(section.getCourseId()));

                ret.add(studentRepository.save(student));
            }
        }

        return ret;
    }

}
