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
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import java.util.List;

@Configuration
@EnableAutoConfiguration
@EnableWebMvcSecurity
@ComponentScan({"edu.ksu.canvas", "edu.ksu.lti"})
@EntityScan({"edu.ksu.canvas", "edu.ksu.canvas.interfaces"})
@PropertySource({"classpath:application.properties"})
public class AppConfig extends CommonAppConfig {

//    @Autowired
//    private ConfigRepository configRepo;

    @Bean
    public RoleChecker roleChecker() {
        final List<LtiLaunchData.InstitutionRole> validRoles = new ImmutableList.Builder<LtiLaunchData.InstitutionRole>()
                .add(LtiLaunchData.InstitutionRole.Instructor)
                .add(LtiLaunchData.InstitutionRole.TeachingAssistant)
                .add(LtiLaunchData.InstitutionRole.Administrator).build();
        return new RoleChecker(validRoles);
    }
//
//    @Override
//    public void addResourceHandlers (ResourceHandlerRegistry registry){
//        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
//    }

//
//    @Bean(name = "canvasURLBuilder")
//    public CanvasURLBuilder canvasURLBuilder() {
//        ConfigItem configItem = configRepo.findByLtiApplicationAndKey("COMMON", "canvas_url");
//        CanvasURLBuilder canvasURLBuilder = new CanvasURLBuilder();
//        canvasURLBuilder.setCanvasBaseUrl(configItem.getValue());
//        return canvasURLBuilder;
//    }

//    @Bean
//    public EnrollmentsReader enrollmentsReader() {
//        String canvasBaseUrl = null;
//        Integer apiVersion = 0;
//        String oauthToken = null;
//        return new EnrollmentsImpl(canvasBaseUrl, apiVersion, oauthToken, restClient);
//    }
}
