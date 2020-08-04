package edu.ksu.canvas.attendance.config;

import com.google.common.collect.ImmutableList;
import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.attendance.util.RoleChecker;
import edu.ksu.canvas.attendance.entity.ConfigItem;
import edu.ksu.canvas.attendance.repository.ConfigRepository;
import edu.ksu.lti.launch.model.LtiLaunchData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import java.util.List;


@Configuration
@EnableAutoConfiguration
@EnableWebMvcSecurity
@EnableJpaRepositories({"edu.ksu.canvas.attendance.repository"})
@ComponentScan({"edu.ksu.lti", "edu.ksu.canvas"})
@EntityScan({"edu.ksu.canvas", "edu.ksu.canvas.interfaces" })
@PropertySource({"classpath:application.properties"})
@Profile("prod")
public class AppConfig {

    private static final Logger LOG = Logger.getLogger(AppConfig.class);

    @Autowired
    private ConfigRepository configRepo;



    @Bean
    public UrlBasedViewResolver setupViewResolver() {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        return resolver;
    }


    @Bean
    public RoleChecker roleChecker() {
        final List<LtiLaunchData.InstitutionRole> validRoles = new ImmutableList.Builder<LtiLaunchData.InstitutionRole>()
                .add(LtiLaunchData.InstitutionRole.Instructor)
                .add(LtiLaunchData.InstitutionRole.TeachingAssistant)
                .add(LtiLaunchData.InstitutionRole.Learner)
                .add(LtiLaunchData.InstitutionRole.Designer)
                .add(LtiLaunchData.InstitutionRole.Administrator).build();
        return new RoleChecker(validRoles);
    }

    @Bean
    public CanvasApiFactory canvasApiFactory() {
        ConfigItem configItem = configRepo.findByLtiApplicationAndKey("COMMON", "canvas_url");
        if(configItem == null) {
            throw new RuntimeException("Missing canvas_url from config");
        }
        String canvasBaseUrl = configItem.getValue();
        return new CanvasApiFactory(canvasBaseUrl);
    }

    @Bean
    public String canvasDomain() {
        ConfigItem configItem = configRepo.findByLtiApplicationAndKey("COMMON", "canvas_url");
        if(configItem == null) {
            throw new RuntimeException("Missing canvas_url from config");
        }
        String canvasUrl = configItem.getValue();
        canvasUrl = canvasUrl.replace("https://", "");
        canvasUrl = canvasUrl.replaceAll("/", "");
        return canvasUrl;
    }

}
