package edu.ksu.canvas.attendance.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@EnableAutoConfiguration
@ComponentScan
@Profile("prod")
public class AppInit extends SpringBootServletInitializer {

    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AppConfig.class);
    }
    
}
