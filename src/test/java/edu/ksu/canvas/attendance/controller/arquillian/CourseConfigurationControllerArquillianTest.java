package edu.ksu.canvas.attendance.controller.arquillian;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.junit.Assert.assertTrue;


@RunAsClient
@RunWith(Arquillian.class)
public class CourseConfigurationControllerArquillianTest extends BaseArquillianTest{

    @Test
    public void shouldBeAbleToSaveConfiguration() throws Exception {
        System.out.println("Testing config page.. using driver: "+driver+"  .. fetching this url: " + baseUrl + "courseConfiguration");

        driver.navigate().to(baseUrl + "courseConfiguration");
        driver.findElement(By.id("classSetupLink")).click();
        driver.findElement(By.id("saveCourseConfiguration")).click();

        Assert.assertTrue("Expected updated success message to be displayed after save", driver.findElement(By.id("updateSucessMessage")).isDisplayed());
    }

    @Test
    public void shouldBeAbleToUseSynchronizationButton() throws Exception {
        driver.navigate().to(baseUrl + "courseConfiguration");
        driver.findElement(By.id("classSetupLink")).click();

        System.out.println("Synchronizing course");
        driver.findElement(By.id("synchronizeWithCanvas")).click();
        assertTrue(driver.findElement(By.id("synchronizationSuccessful")).isDisplayed());
    }
    
}
