package edu.ksu.canvas.aviation.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.aviation.services.SynchronizationService;
import edu.ksu.canvas.aviation.util.RoleChecker;
import edu.ksu.canvas.repository.ConfigRepository;
import edu.ksu.lti.LtiLaunch;
import edu.ksu.lti.util.CanvasInstanceChecker;


@Configuration
@EnableWebMvc
@Profile("test")
@ComponentScan(
        basePackages = {
           "edu.ksu.canvas.aviation.controller",
           "edu.ksu.canvas.aviation.form",
           "edu.ksu.canvas.aviation.model",
           "edu.ksu.canvas.aviation.services"},
        excludeFilters = {
           @ComponentScan.Filter(value = SynchronizationService.class, type = FilterType.ASSIGNABLE_TYPE)
        })
public class TestSpringMVCConfig {

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
    public LtiLaunch ltiLaunch() {
        return Mockito.mock(LtiLaunch.class);
    }
    
    @Bean
    public RoleChecker roleChecker() {
        return new AppConfig().roleChecker();
    }

    @Bean
    public SynchronizationService synchornizationService() {
        return Mockito.mock(SynchronizationService.class);
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
