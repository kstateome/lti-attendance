package edu.ksu.canvas.attendance.config;

import com.google.common.collect.ImmutableList;
import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.attendance.util.RoleChecker;
import edu.ksu.canvas.entity.config.ConfigItem;
import edu.ksu.canvas.repository.ConfigRepository;
import edu.ksu.lti.LtiLaunchData;
import edu.ksu.lti.config.CommonAppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

import java.util.List;


@Configuration
@EnableAutoConfiguration
@EnableWebMvcSecurity
@EnableJpaRepositories({"edu.ksu.canvas.attendance.repository"})
@ComponentScan({"edu.ksu.canvas", "edu.ksu.lti"})
@EntityScan({"edu.ksu.canvas", "edu.ksu.canvas.interfaces"})
@PropertySource({"classpath:application.properties"})
@Profile("prod")
public class AppConfig extends CommonAppConfig {

    @Autowired
    private ConfigRepository configRepo;


    @Bean
    public RoleChecker roleChecker() {
        final List<LtiLaunchData.InstitutionRole> validRoles = new ImmutableList.Builder<LtiLaunchData.InstitutionRole>()
                .add(LtiLaunchData.InstitutionRole.Instructor)
                .add(LtiLaunchData.InstitutionRole.TeachingAssistant)
                .add(LtiLaunchData.InstitutionRole.Learner)
                .add(LtiLaunchData.InstitutionRole.Administrator).build();
        return new RoleChecker(validRoles);
    }

    @Bean
    public CanvasApiFactory canvasApiFactory() {
        ConfigItem configItem = configRepo.findByLtiApplicationAndKey("COMMON", "canvas_url");
        String canvasBaseUrl = configItem.getValue();
        return new CanvasApiFactory(canvasBaseUrl);
    }

}
