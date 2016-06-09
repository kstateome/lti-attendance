package edu.ksu.canvas.aviation.config.arquillian;

import javax.servlet.ServletContext;  
import javax.servlet.ServletException;  
import javax.servlet.ServletRegistration.Dynamic;

import org.jboss.arquillian.warp.extension.spring.servlet.WarpDispatcherServlet;
import org.springframework.web.WebApplicationInitializer;  
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;


public class ArquillianWebAppInitializer implements WebApplicationInitializer {
    
    public void onStartup(ServletContext servletContext) throws ServletException {  
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.getEnvironment().setActiveProfiles("Arquillian");
        ctx.register(ArquillianSpringMVCConfig.class);
        ctx.setServletContext(servletContext);
        
        Dynamic dynamic = servletContext.addServlet("dispatcher", new WarpDispatcherServlet(ctx));
        dynamic.addMapping("/");  
        dynamic.setLoadOnStartup(1);  
   }  
} 
