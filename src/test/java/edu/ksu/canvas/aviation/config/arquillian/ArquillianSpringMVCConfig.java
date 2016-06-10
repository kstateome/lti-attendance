package edu.ksu.canvas.aviation.config.arquillian;

import com.google.common.collect.ImmutableList;
import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.aviation.services.CanvasApiWrapperService;
import edu.ksu.canvas.aviation.services.SynchronizationService;
import edu.ksu.canvas.aviation.util.RoleChecker;
import edu.ksu.canvas.entity.lti.OauthToken;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.canvas.interfaces.CourseReader;
import edu.ksu.canvas.model.Course;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;


@Configuration
@EnableWebMvc
@Profile("Arquillian")
@ComponentScan(
        basePackages = {
           "edu.ksu.canvas.aviation.config.arquillian",
           "edu.ksu.canvas.aviation.controller",
           "edu.ksu.canvas.aviation.form",
           "edu.ksu.canvas.aviation.model",
           "edu.ksu.canvas.aviation.services"},
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
        when(mockApiFactory.getReader(eq(CourseReader.class), anyString())).thenReturn(mockCourseReader);
        return mockApiFactory;
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
        return Mockito.mock(SynchronizationService.class);
    }
    
    @Bean
    public CanvasApiWrapperService canvasApiWrapperService() {
//        CanvasApiWrapperService ret = Mockito.mock(CanvasApiWrapperService.class);
//
//        try {
//            when(ret.getCourseId()).thenReturn(COURSE_ID_EXISTING.intValue());
//            when(ret.getEid()).thenReturn("randomEid");
//        } catch (NoLtiSessionException e) {
//            LOG.error("failed to setup CanvasApiWrapper", e);
//        }
//
//        return ret;
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
