package edu.ksu.canvas.attendance.config.arquillian;

import com.google.common.collect.ImmutableList;
import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.attendance.services.CanvasApiWrapperService;
import edu.ksu.canvas.attendance.services.SynchronizationService;
import edu.ksu.canvas.attendance.util.RoleChecker;
import edu.ksu.canvas.entity.lti.OauthToken;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.canvas.interfaces.CourseReader;
import edu.ksu.canvas.interfaces.EnrollmentsReader;
import edu.ksu.canvas.interfaces.SectionReader;
import edu.ksu.canvas.model.Course;
import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Section;
import edu.ksu.canvas.model.User;
import edu.ksu.canvas.repository.ConfigRepository;
import edu.ksu.lti.LtiLaunch;
import edu.ksu.lti.LtiLaunchData;
import edu.ksu.lti.model.LtiSession;
import edu.ksu.lti.util.CanvasInstanceChecker;
import edu.ksu.lti.util.CanvasUtil;
import org.apache.log4j.Logger;
import org.mockito.Mockito;
import org.springframework.context.annotation.*;
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

import static org.mockito.Matchers.*;
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
    private static final Logger LOG = Logger.getLogger(ArquillianSpringMVCConfig.class);
    
    
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
        when(mockCourseReader.getSingleCourse(any(), any())).thenReturn(Optional.of(new Course()));
        SectionReader mockSectionReader = Mockito.mock(SectionReader.class);
        when(mockSectionReader.listCourseSections(any(), any())).thenReturn(Collections.singletonList(buildFakeSection()));
        EnrollmentsReader mockEnrollmentsReader = Mockito.mock(EnrollmentsReader.class);
        when(mockEnrollmentsReader.getSectionEnrollments(any(), any())).thenReturn(Collections.singletonList(buildFakeEnrollment()));

        when(mockApiFactory.getReader(eq(CourseReader.class), anyString())).thenReturn(mockCourseReader);
        when(mockApiFactory.getReader(eq(SectionReader.class), anyString())).thenReturn(mockSectionReader);
        when(mockApiFactory.getReader(eq(EnrollmentsReader.class), anyString())).thenReturn(mockEnrollmentsReader);
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
        section.setCourseId(COURSE_ID_EXISTING.intValue());
        return section;
    }

    @Bean
    public CanvasInstanceChecker canvasInstanceChecker() {
         return new CanvasInstanceChecker();
    }
    
    @Bean
    public ConfigRepository configRepository() {
        return Mockito.mock(ConfigRepository.class);
    }
    
    @Bean
    public LtiLaunch ltiLaunch() throws NoLtiSessionException {
        LtiLaunch ltiLaunch = Mockito.mock(LtiLaunch.class);
        LtiSession fakeLtiSession = new LtiSession();
        fakeLtiSession.setEid("randomEid");
        fakeLtiSession.setCanvasCourseId(String.valueOf(COURSE_ID_EXISTING.intValue()));
        fakeLtiSession.setCanvasOauthToken(Mockito.mock(OauthToken.class));
        when(ltiLaunch.getLtiSession()).thenReturn(fakeLtiSession);
        return ltiLaunch;
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
    public SynchronizationService synchronizationService() {
        return new SynchronizationService();
    }
    
    @Bean
    public CanvasApiWrapperService canvasApiWrapperService() {

        return new CanvasApiWrapperService();
    }
    
    @Bean
    public CanvasUtil canvasUtil() {
        return Mockito.mock(CanvasUtil.class);
    }
    
    @Bean
    public JmsTemplate jmsTemplate() {
        return Mockito.mock(JmsTemplate.class);
    }

}
