package edu.ksu.canvas.attendance.controller.arquillian;


import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.net.URL;

public class BaseArquillianTest {

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
                .addPackages(true, "edu.ksu.canvas.attendance")
                .addAsLibraries(dependencies);

        addStaticResourcesTo(war);
        addJspsToWar(war);

        //System.err.println("The war file contains the following files...\n"+war.toString(true));
        return war;
    }

    private static WebArchive addStaticResourcesTo(WebArchive archive) {
        final File staticResourcesDirectory = new File("src/main/resources/static");
        for (File file : FileUtils.listFiles(staticResourcesDirectory, null, true)) {
            if (!file.isDirectory()) {
                String pathInWar = file.getPath().substring("src/main/resources/static".length());
                //System.err.println("adding file "+file+" to path: "+pathInWar);
                archive.addAsWebResource(file, pathInWar);
            }
        }
        return archive;
    }

    private static WebArchive addJspsToWar(WebArchive archive) {
        final File jspDirectory = new File("src/main/webapp/WEB-INF/jsp");
        for (File file : FileUtils.listFiles(jspDirectory, null, true)) {
            if (!file.isDirectory()) {
                String pathInWar = file.getPath().substring("src/main/webapp/WEB-INF/".length());
                archive.addAsWebInfResource(file, pathInWar);
            }
        }
        return archive;
    }

    @ArquillianResource
    protected URL baseUrl;

    @Drone
    protected WebDriver driver;
}
