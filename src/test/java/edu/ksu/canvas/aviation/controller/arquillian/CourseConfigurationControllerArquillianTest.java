package edu.ksu.canvas.aviation.controller.arquillian;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.Activity;
import org.jboss.arquillian.warp.Inspection;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.arquillian.warp.client.filter.http.HttpFilters;
import org.jboss.arquillian.warp.servlet.AfterServlet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.*;


@WarpTest
@RunAsClient
@RunWith(Arquillian.class)
public class CourseConfigurationControllerArquillianTest {
    
    @Deployment
    @OverProtocol("Servlet 3.0")
    public static WebArchive create() {

        File[] dependencies = Maven.resolver()
                                   .loadPomFromClassLoaderResource("pom.xml")
                                   .importRuntimeAndTestDependencies()
                                   .resolve()
                                   .withTransitivity()
                                   .asFile();
        
        WebArchive war = ShrinkWrap.create(WebArchive.class)
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/jsp/roster.jsp"),"jsp/roster.jsp")
                .addPackages(true, "edu.ksu.canvas.aviation")
                .addAsLibraries(dependencies);
        
        addStaticResourcesTo(war);
        
        //System.err.println("The war file contains the following files...\n"+war.toString(true));
        return war;
    }
    
    private static WebArchive addStaticResourcesTo(WebArchive archive) {
        final File webAppDirectory = new File("src/main/resources/static");
        for (File file : FileUtils.listFiles(webAppDirectory, null, true)) {
            if (!file.isDirectory()) {
                String pathInWar = file.getPath().substring("src/main/resources/static".length());
                //System.err.println("adding file "+file+" to path: "+pathInWar);
                archive.addAsWebResource(file, pathInWar);
            }
        }
        return archive;
    }
    
    @ArquillianResource
    private URL baseUrl;
    
    @Drone
    private WebDriver driver;
    

    @Test
    public void shouldBeAbleToInject() throws Exception {
        System.out.println("first test.. using driver: "+driver+"  .. fetching this url: " + baseUrl + "roster");

        driver.navigate().to(baseUrl + "roster");
        System.out.println("Page source: \n" + driver.getPageSource());

        Warp.initiate(new Activity() {
            
            @Override
            public void perform() {
                System.err.println("Submitting roster form");
                driver.findElement(By.id("saveAttendanceOnTop")).click();
            }
        }).observe(HttpFilters.request().uri().contains("roster"))
        .inspect(new Inspection() {
            private static final long serialVersionUID = 1L;
            
            @ArquillianResource
            private ModelAndView modelAndView; 
            
            @AfterServlet
            public void testAfterServlet() {
                //cannot use logger above
                System.out.println("modelAndView after save: "+modelAndView);
                
                Boolean saveSuccessExpected = Boolean.TRUE;
                Boolean saveSuccessActual = (Boolean) modelAndView.getModel().get("saveSuccess");
                assertEquals(saveSuccessExpected, saveSuccessActual);
            }
        });

    }
    
}
