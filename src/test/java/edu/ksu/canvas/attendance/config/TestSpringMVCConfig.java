package edu.ksu.canvas.attendance.config;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.attendance.repository.AttendanceAssignmentRepository;
import edu.ksu.canvas.attendance.services.*;
import edu.ksu.canvas.attendance.util.RoleChecker;
import edu.ksu.canvas.repository.ConfigRepository;
import edu.ksu.canvas.repository.LtiKeyRepository;
import edu.ksu.canvas.repository.OauthTokenRepository;
import edu.ksu.lti.launch.oauth.LtiLaunch;
import edu.ksu.lti.launch.security.CanvasInstanceChecker;
import edu.ksu.lti.launch.service.*;
import org.apache.http.impl.client.HttpClientBuilder;
import org.mockito.Mockito;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;


@Configuration
@EnableWebMvc
@Profile("test")
@ComponentScan(
        basePackages = {
                "edu.ksu.canvas.attendance.controller",
                "edu.ksu.canvas.attendance.form",
                "edu.ksu.canvas.attendance.model",
                "edu.ksu.canvas.attendance.services"},
        excludeFilters = {
           @ComponentScan.Filter(value = SynchronizationService.class, type = FilterType.ASSIGNABLE_TYPE),
           @ComponentScan.Filter(value = CanvasApiWrapperService.class, type = FilterType.ASSIGNABLE_TYPE)
        })
public class TestSpringMVCConfig {

    private final String FAKE_DOMAIN = "someFakeDomain";

    @Bean
    public CanvasApiFactory canvasApiFactory() {
        return Mockito.mock(CanvasApiFactory.class);
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
    public AttendanceLtiLaunchKeyService attendanceLtiLaunchKeyService () { return Mockito.mock(AttendanceLtiLaunchKeyService.class); }

    @Bean
    public String canvasDomain() { return FAKE_DOMAIN; }

    @Bean
    public HttpClientBuilder httpClientBuilder()  { return Mockito.mock(HttpClientBuilder.class); }

    @Bean
    public OauthTokenRepository oauthTokenRepository () { return Mockito.mock(OauthTokenRepository.class); }

    @Bean
    public AttendanceOauthTokenService attendanceOauthTokenService() { return  Mockito.mock(AttendanceOauthTokenService.class); }

    @Bean
    public LtiSessionService ltiSessionService() { return Mockito.mock(LtiSessionService.class); }

    @Bean
    public LtiLaunch ltiLaunch() {
        return Mockito.mock(LtiLaunch.class);
    }

    @Bean
    public AttendanceConfigService attendanceConfigService () { return Mockito.mock(AttendanceConfigService.class); }

    @Bean
    public LtiKeyRepository ltiKeyRepository () { return Mockito.mock(LtiKeyRepository.class); }

    @Bean
    public AttendanceAssignmentRepository attendanceAssignmentRepository () { return Mockito.mock(AttendanceAssignmentRepository.class); }

    @Bean
    public RoleChecker roleChecker() {
        return new AppConfig().roleChecker();
    }

    @Bean
    public SynchronizationService synchronizationService() {
        return Mockito.mock(SynchronizationService.class);
    }
    
    @Bean
    public CanvasApiWrapperService canvasApiWrapperService() {
        return Mockito.mock(CanvasApiWrapperService.class);
    }

    @Bean
    public UrlBasedViewResolver setupViewResolver() {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        return resolver;
    }

}
