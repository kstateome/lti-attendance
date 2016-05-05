package edu.ksu.canvas.aviation.config;

import com.google.common.collect.ImmutableList;
import edu.ksu.canvas.aviation.util.RoleChecker;
import edu.ksu.canvas.entity.config.ConfigItem;
import edu.ksu.canvas.impl.EnrollmentsImpl;
import edu.ksu.canvas.interfaces.EnrollmentsReader;
import edu.ksu.canvas.net.RestClient;
import edu.ksu.canvas.repository.ConfigRepository;
import edu.ksu.lti.LtiLaunchData;
import edu.ksu.lti.config.CommonAppConfig;
import edu.ksu.lti.util.CanvasURLBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import java.util.List;

@Configuration
@EnableAutoConfiguration
@EnableWebMvcSecurity
@EnableJpaRepositories({"edu.ksu.canvas.aviation.repository"})
@ComponentScan({"edu.ksu.canvas", "edu.ksu.lti"})
@EntityScan({"edu.ksu.canvas", "edu.ksu.canvas.interfaces"})
@PropertySource({"classpath:application.properties"})
public class AppConfig extends CommonAppConfig {

    @Bean
    public RoleChecker roleChecker() {
        final List<LtiLaunchData.InstitutionRole> validRoles = new ImmutableList.Builder<LtiLaunchData.InstitutionRole>()
                .add(LtiLaunchData.InstitutionRole.Instructor)
                .add(LtiLaunchData.InstitutionRole.TeachingAssistant)
                .add(LtiLaunchData.InstitutionRole.Administrator).build();
        return new RoleChecker(validRoles);
    }
}
