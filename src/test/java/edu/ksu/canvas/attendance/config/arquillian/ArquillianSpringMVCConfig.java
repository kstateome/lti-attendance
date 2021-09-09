package edu.ksu.canvas.attendance.config.arquillian;

import com.google.common.collect.ImmutableList;
import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.attendance.repository.AttendanceAssignmentRepository;
import edu.ksu.canvas.attendance.repository.ConfigRepository;
import edu.ksu.canvas.attendance.services.CanvasApiWrapperService;
import edu.ksu.canvas.attendance.services.SynchronizationService;
import edu.ksu.canvas.attendance.submitter.AssignmentSubmitter;
import edu.ksu.canvas.attendance.submitter.AssignmentValidator;
import edu.ksu.canvas.attendance.submitter.CanvasAssignmentAssistant;
import edu.ksu.canvas.attendance.util.RoleChecker;
import edu.ksu.canvas.interfaces.CourseReader;
import edu.ksu.canvas.interfaces.EnrollmentReader;
import edu.ksu.canvas.interfaces.SectionReader;
import edu.ksu.canvas.model.Course;
import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Section;
import edu.ksu.canvas.model.User;
import edu.ksu.canvas.oauth.OauthToken;
import edu.ksu.canvas.requestOptions.GetEnrollmentOptions;
import edu.ksu.canvas.requestOptions.GetSingleCourseOptions;
import edu.ksu.lti.launch.exception.NoLtiSessionException;
import edu.ksu.lti.launch.model.LtiLaunchData;
import edu.ksu.lti.launch.model.LtiSession;
import edu.ksu.lti.launch.oauth.LtiLaunch;
import edu.ksu.lti.launch.security.CanvasInstanceChecker;
import edu.ksu.lti.launch.service.LtiSessionService;
import org.apache.http.impl.client.HttpClientBuilder;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


@Configuration
@EnableWebMvc
@Profile("Arquillian")
@ComponentScan(
        basePackages = {
                "edu.ksu.canvas.attendance.config.arquillian",
                "edu.ksu.canvas.attendance.controller",
                "edu.ksu.canvas.attendance.form",
                "edu.ksu.canvas.attendance.model",
                "edu.ksu.canvas.attendance.services"},
        excludeFilters = {
           @ComponentScan.Filter(value = SynchronizationService.class, type = FilterType.ASSIGNABLE_TYPE),
           @ComponentScan.Filter(value = CanvasApiWrapperService.class, type = FilterType.ASSIGNABLE_TYPE)
        })
public class ArquillianSpringMVCConfig extends WebMvcConfigurerAdapter {
    
    public static final Long COURSE_ID_EXISTING = 50L;


    private final String FAKE_DOMAIN = "someFakeDomain";
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/bootstrap/**").addResourceLocations("/bootstrap/").setCachePeriod(31556926);
        registry.addResourceHandler("/img/**").addResourceLocations("/img/").setCachePeriod(31556926);
        registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(31556926);
        registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(31556926);
        registry.addResourceHandler("/stylesheets/**").addResourceLocations("/stylesheets/").setCachePeriod(31556926);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
    
    @Bean
    public UrlBasedViewResolver setupViewResolver() {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        return resolver;
    }

    @Bean
    public CanvasApiFactory canvasApiFactory() throws IOException {

        CanvasApiFactory mockApiFactory = Mockito.mock(CanvasApiFactory.class);

        CourseReader mockCourseReader = Mockito.mock(CourseReader.class);
        GetSingleCourseOptions mockGetSingleCourseOptions = new GetSingleCourseOptions(anyString());
        when(mockCourseReader.getSingleCourse(mockGetSingleCourseOptions)).thenReturn(Optional.of(new Course()));
        SectionReader mockSectionReader = Mockito.mock(SectionReader.class);
        when(mockSectionReader.listCourseSections(any(), any())).thenReturn(Collections.singletonList(buildFakeSection()));
        EnrollmentReader mockEnrollmentsReader = Mockito.mock(EnrollmentReader.class);
        when(mockEnrollmentsReader.getSectionEnrollments(new GetEnrollmentOptions(any()))).thenReturn(Collections.singletonList(buildFakeEnrollment()));

        when(mockApiFactory.getReader(eq(CourseReader.class), any(OauthToken.class))).thenReturn(mockCourseReader);
        when(mockApiFactory.getReader(eq(SectionReader.class), any(OauthToken.class))).thenReturn(mockSectionReader);
        when(mockApiFactory.getReader(eq(EnrollmentReader.class), any(OauthToken.class))).thenReturn(mockEnrollmentsReader);
        return mockApiFactory;
    }

    private Enrollment buildFakeEnrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setUser(new User());
        enrollment.getUser().setSisUserId("userId");
        return enrollment;
    }

    private Section buildFakeSection() {
        Section section = new Section();
        section.setId(10L);
        section.setCourseId(COURSE_ID_EXISTING);
        return section;
    }

    @Bean
    public CanvasInstanceChecker canvasInstanceChecker() {
         return Mockito.mock(CanvasInstanceChecker.class);
    }
    
    @Bean
    public ConfigRepository configRepository() {
        return Mockito.mock(ConfigRepository.class);
    }
    
    @Bean
    public LtiSessionService ltiSessionService() throws NoLtiSessionException {
        LtiSessionService ltiSessionService = Mockito.mock(LtiSessionService.class);
        LtiSession fakeLtiSession = new LtiSession();
        LtiLaunchData fakeLtiLaunchData = new LtiLaunchData();
        fakeLtiLaunchData.setRoles("Instructor");
        fakeLtiSession.setLtiLaunchData(fakeLtiLaunchData);
        fakeLtiSession.setEid("randomEid");
        fakeLtiSession.setCanvasCourseId(String.valueOf(COURSE_ID_EXISTING.intValue()));
        fakeLtiSession.setOauthToken(Mockito.mock(OauthToken.class));
        when(ltiSessionService.getLtiSession()).thenReturn(fakeLtiSession);
        return ltiSessionService;
    }
    
    @Bean
    public RoleChecker roleChecker() {
        final List<LtiLaunchData.InstitutionRole> validRoles = new ImmutableList.Builder<LtiLaunchData.InstitutionRole>()
                .add(LtiLaunchData.InstitutionRole.Instructor)
                .add(LtiLaunchData.InstitutionRole.TeachingAssistant)
                .add(LtiLaunchData.InstitutionRole.Administrator).build();
        return new RoleChecker(validRoles);
    }

    @Bean
    public HttpClientBuilder httpClientBuilder()  { return Mockito.mock(HttpClientBuilder.class); }

    @Bean
    public String canvasDomain() { return FAKE_DOMAIN; }

    @Bean
    public SynchronizationService synchronizationService() {
        return new SynchronizationService();
    }
    
    @Bean
    public CanvasApiWrapperService canvasApiWrapperService() {
        return new CanvasApiWrapperService();
    }

    @Bean
    public AttendanceAssignmentRepository attendanceAssignmentRepository () { return Mockito.mock(AttendanceAssignmentRepository.class); }

    @Bean
    public AssignmentSubmitter assignmentSubmitter() { return Mockito.mock(AssignmentSubmitter.class); }

    @Bean
    public CanvasAssignmentAssistant canvasAssignmentAssistant() { return Mockito.mock(CanvasAssignmentAssistant.class); }

    @Bean
    public AssignmentValidator assignmentValidator() { return Mockito.mock(AssignmentValidator.class); }

    @Bean
    public LtiLaunch ltiLaunch() {
        return Mockito.mock(LtiLaunch.class);
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return Mockito.mock(JmsTemplate.class);
    }

}
