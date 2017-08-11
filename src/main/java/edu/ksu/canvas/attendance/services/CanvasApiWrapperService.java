package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.enums.SectionIncludes;
import edu.ksu.canvas.exception.CanvasException;
import edu.ksu.canvas.exception.InvalidOauthTokenException;
import edu.ksu.canvas.interfaces.*;
import edu.ksu.canvas.model.Course;
import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Progress;
import edu.ksu.canvas.model.Section;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.model.assignment.AssignmentOverride;
import edu.ksu.canvas.oauth.OauthToken;
import edu.ksu.canvas.requestOptions.GetEnrollmentOptions;
import edu.ksu.canvas.requestOptions.GetSingleAssignmentOptions;
import edu.ksu.canvas.requestOptions.GetSingleCourseOptions;
import edu.ksu.canvas.requestOptions.MultipleSubmissionsOptions;
import edu.ksu.lti.launch.exception.NoLtiSessionException;
import edu.ksu.lti.launch.model.LtiLaunchData;
import edu.ksu.lti.launch.model.LtiSession;
import edu.ksu.lti.launch.oauth.LtiLaunch;
import edu.ksu.lti.launch.service.LtiSessionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;


@Component
@Scope(value="session")
public class CanvasApiWrapperService {

    private static final Logger LOG = Logger.getLogger(CanvasApiWrapperService.class);

    @Autowired
    protected LtiLaunch ltiLaunch;

    @Autowired
    private LtiSessionService ltiSessionService;

    @Autowired
    private CanvasApiFactory canvasApiFactory;

    @Autowired
    private String canvasDomain;

    private EnrollmentOptionsFactory enrollmentOptionsFactory = new EnrollmentOptionsFactory();

    public Integer getCourseId() throws NoLtiSessionException {
        LtiSession ltiSession = ltiSessionService.getLtiSession();
        return Integer.valueOf(ltiSession.getCanvasCourseId());
    }

    public String getCourseName() throws NoLtiSessionException {
        Optional<Course> course = getCourse();

        return course.isPresent() ? course.get().getName() : null;
    }

    private Optional<Course> getCourse() throws NoLtiSessionException {
        LtiSession ltiSession = ltiSessionService.getLtiSession();

        String canvasCourseId = ltiSession.getCanvasCourseId();
        OauthToken oAuthToken = ltiSession.getOauthToken();

        CourseReader reader = canvasApiFactory.getReader(CourseReader.class, oAuthToken);
        try {
            GetSingleCourseOptions getSingleCourseOptions = new GetSingleCourseOptions(canvasCourseId);
            getSingleCourseOptions.includes(Collections.emptyList());
            return reader.getSingleCourse(getSingleCourseOptions);
        } catch (IOException e) {
            throw new UnexpectedCanvasWrapperException("Unexpected problem getting Course from Canvas", e);
        }
    }

    public String getEid() throws NoLtiSessionException {
        LtiSession ltiSession = ltiSessionService.getLtiSession();
        return ltiSession.getEid();
    }

    public String getSisID() throws NoLtiSessionException {
        LtiSession ltiSession = ltiSessionService.getLtiSession();
        return ltiSession.getLtiLaunchData().getLis_person_sourcedid();
    }

    public List<Section> getSections(long canvasCourseId, OauthToken oauthToken) throws NoLtiSessionException {
        SectionReader sectionReader = canvasApiFactory.getReader(SectionReader.class, oauthToken);
        try {
            return sectionReader.listCourseSections(Long.toString(canvasCourseId), Collections.singletonList(SectionIncludes.STUDENTS));
        } catch (IOException e) {
            throw new UnexpectedCanvasWrapperException("Unexpected problem getting Sections from Canvas", e);
        }
    }

    public Map<Section,List<Enrollment>> getEnrollmentsFromCanvas(List<Section> sections, OauthToken oauthToken) throws NoLtiSessionException {
        EnrollmentReader enrollmentsReader = canvasApiFactory.getReader(EnrollmentReader.class, oauthToken);
        Map<Section, List<Enrollment>> ret = new HashMap<>();

        if(sections == null) {
            return null;
        }

        for (Section section : sections) {
            try {
                GetEnrollmentOptions enrollmentOptions = enrollmentOptionsFactory.buildEnrollmentOptions(section);

                enrollmentOptions.type(Collections.singletonList(GetEnrollmentOptions.EnrollmentType.STUDENT));

                for (Enrollment enrollment : enrollmentsReader.getSectionEnrollments(enrollmentOptions)) {
                    List<Enrollment> enrollments = ret.computeIfAbsent(section, k -> new ArrayList<>());

                    enrollments.add(enrollment);
                }
            } catch (InvalidOauthTokenException | IOException e) {
                throw new UnexpectedCanvasWrapperException("Unexpected problem getting Enrollments from Canvas", e);
            }
        }

        return ret;
    }

    public List<LtiLaunchData.InstitutionRole> getRoles() throws NoLtiSessionException {
        LtiSession ltiSession = ltiSessionService.getLtiSession();
        LtiLaunchData launchData = ltiSession.getLtiLaunchData();
        return launchData.getRolesList();
    }

    public OauthToken getOauthToken() throws NoLtiSessionException {
        return ltiSessionService.getLtiSession().getOauthToken();
    }

    public void validateOAuthToken() throws NoLtiSessionException, IOException {
        ltiLaunch.validateOAuthToken();
    }

    public void ensureApiTokenPresent() throws NoLtiSessionException {
        ltiLaunch.ensureApiTokenPresent();
    }

    void setEnrollmentOptionsFactory(EnrollmentOptionsFactory enrollmentOptionsFactory) {
        this.enrollmentOptionsFactory = enrollmentOptionsFactory;
    }

    public Optional<Assignment> getSingleAssignment(Long courseId, OauthToken oauthToken, String assignmentId) throws IOException {
        AssignmentReader assignmentReader = canvasApiFactory.getReader(AssignmentReader.class, oauthToken);
        GetSingleAssignmentOptions singleAssignmentOptions = new GetSingleAssignmentOptions(courseId.toString(), assignmentId);
        return assignmentReader.getSingleAssignment(singleAssignmentOptions);
    }

    public void editAssignment(String courseId, Assignment canvasAssignment, OauthToken oauthToken) throws IOException {
        AssignmentWriter assignmentWriter = canvasApiFactory.getWriter(AssignmentWriter.class, oauthToken);
        assignmentWriter.editAssignment(courseId, canvasAssignment);
    }

    public Optional<Progress> gradeMultipleSubmissionsBySection(OauthToken oauthToken, MultipleSubmissionsOptions submissionOptions) throws IOException {
        SubmissionWriter submissionWriter = canvasApiFactory.getWriter(SubmissionWriter.class, oauthToken);
        return submissionWriter.gradeMultipleSubmissionsBySection(submissionOptions);
    }

    public Optional<Assignment> createAssignment(Long courseId, Assignment assignment, OauthToken oauthToken) throws IOException {
        AssignmentWriter assignmentWriter = canvasApiFactory.getWriter(AssignmentWriter.class, oauthToken);
        return assignmentWriter.createAssignment(courseId.toString(), assignment);
    }

    public void deleteAssignment(String courseId, String canvasAssignmentId, OauthToken oauthToken) throws IOException {
        AssignmentWriter assignmentWriter = canvasApiFactory.getWriter(AssignmentWriter.class, oauthToken);
        assignmentWriter.deleteAssignment(courseId, canvasAssignmentId);
    }

    public AssignmentOverride createAssignmentOverride(OauthToken oauthToken, Integer assignmentId,
                                                       Integer sectionId, String courseId) {
        AssignmentOverrideWriter writer = getApiFactory().getWriter(AssignmentOverrideWriter.class, oauthToken);
        AssignmentOverride override = new AssignmentOverride();
        override.setAssignmentId(assignmentId);
        override.setCourseSectionId(sectionId);
        try {
            Optional<AssignmentOverride> opt = writer.createAssignmentOverride(courseId, override);
            LOG.warn(opt + "=========================================================================");
            return opt.orElse(null);
        } catch (IOException | CanvasException e) {
            LOG.error("Error while creating assignment override for assignment " + assignmentId, e);
        }
        return null;
    }

    private CanvasApiFactory getApiFactory() {
        if(canvasApiFactory == null) {
            LOG.debug("Creating new API factory for Canvas domain " + canvasDomain);
            canvasApiFactory = new CanvasApiFactory("https://" + canvasDomain);
        }
        return canvasApiFactory;
    }

    public void setAssignmentOnlyVisibleToOverrides(OauthToken oauthToken, String courseId, Assignment assignment) {
        assignment.setOnlyVisibleToOverrides(true);
        AssignmentWriter writer = getApiFactory().getWriter(AssignmentWriter.class, oauthToken);
        try {
            writer.editAssignment(courseId, assignment);
        } catch(IOException | CanvasException e) {
            LOG.error("Error setting visible to overrides", e);
        }
    }

    class EnrollmentOptionsFactory {

        public GetEnrollmentOptions buildEnrollmentOptions(Section section) {
           return new GetEnrollmentOptions(Long.toString(section.getId()));
        }
    }
}
