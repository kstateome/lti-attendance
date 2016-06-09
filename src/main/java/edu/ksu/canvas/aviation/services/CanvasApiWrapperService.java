package edu.ksu.canvas.aviation.services;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.entity.lti.OauthToken;
import edu.ksu.canvas.enums.EnrollmentType;
import edu.ksu.canvas.enums.SectionIncludes;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.canvas.exception.InvalidOauthTokenException;
import edu.ksu.canvas.interfaces.CourseReader;
import edu.ksu.canvas.interfaces.EnrollmentsReader;
import edu.ksu.canvas.interfaces.SectionReader;
import edu.ksu.canvas.model.Course;
import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Section;
import edu.ksu.lti.LtiLaunch;
import edu.ksu.lti.LtiLaunchData;
import edu.ksu.lti.model.LtiSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
@Scope(value="session")
public class CanvasApiWrapperService {

    @Autowired
    protected LtiLaunch ltiLaunch;

    @Autowired
    private CanvasApiFactory canvasApiFactory;


    public Integer getCourseId() throws NoLtiSessionException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        return Integer.valueOf(ltiSession.getCanvasCourseId());
    }

    public String getCourseName() throws NoLtiSessionException {
        Optional<Course> course = getCourse();

        return course.isPresent() ? course.get().getName() : null;
    }

    private Optional<Course> getCourse() throws NoLtiSessionException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();

        String canvasCourseId = ltiSession.getCanvasCourseId();
        String oAuthToken = ltiSession.getCanvasOauthToken().getToken();

        CourseReader reader = canvasApiFactory.getReader(CourseReader.class, oAuthToken);
        try {
            return reader.getSingleCourse(canvasCourseId, Collections.emptyList());
        } catch (IOException e) {
            throw new UnexpectedCanvasWrapperException("Unexpected problem getting Course from Canvas", e);
        }
    }

    public String getEid() throws NoLtiSessionException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        return ltiSession.getEid();
    }

    public List<Section> getSections(long canvasCourseId) throws NoLtiSessionException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        OauthToken oauthToken = ltiSession.getCanvasOauthToken();
        SectionReader sectionReader = canvasApiFactory.getReader(SectionReader.class, oauthToken.getToken());
        try {
            return sectionReader.listCourseSections((int) canvasCourseId, Collections.singletonList(SectionIncludes.students));
        } catch (IOException e) {
            throw new UnexpectedCanvasWrapperException("Unexpected problem getting Sections from Canvas", e);
        }
    }

    public Map<Section,List<Enrollment>> getEnrollmentsFromCanvas(List<Section> sections) throws NoLtiSessionException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        OauthToken oauthToken = ltiSession.getCanvasOauthToken();
        EnrollmentsReader enrollmentsReader = canvasApiFactory.getReader(EnrollmentsReader.class, oauthToken.getToken());
        Map<Section, List<Enrollment>> ret = new HashMap<>();

        if(sections == null) {
            return null;
        }

        for (Section section : sections) {
            try {
                for (Enrollment enrollment : enrollmentsReader.getSectionEnrollments((int) section.getId(), Collections.singletonList(EnrollmentType.STUDENT))) {
                    List<Enrollment> enrollments = ret.get(section);

                    if(enrollments == null) {
                        enrollments = new ArrayList<>();
                        ret.put(section, enrollments);
                    }

                    enrollments.add(enrollment);
                }
            } catch (InvalidOauthTokenException | IOException e) {
                throw new UnexpectedCanvasWrapperException("Unexpected problem getting Enrollments from Canvas", e);
            }
        }

        return ret;
    }

    public List<LtiLaunchData.InstitutionRole> getRoles() throws NoLtiSessionException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LtiLaunchData launchData = ltiSession.getLtiLaunchData();
        return launchData.getRolesList();
    }

    public void validateOAuthToken() throws NoLtiSessionException {
        ltiLaunch.validateOAuthToken();
    }

    public void ensureApiTokenPresent(String applicationName) throws NoLtiSessionException {
        ltiLaunch.ensureApiTokenPresent(applicationName);
    }

}
