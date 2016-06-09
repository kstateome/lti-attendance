package edu.ksu.canvas.aviation.controller.arquillian;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.Activity;
import org.jboss.arquillian.warp.Inspection;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.arquillian.warp.client.filter.http.HttpFilters;
import org.jboss.arquillian.warp.client.filter.http.HttpMethod;
import org.jboss.arquillian.warp.servlet.AfterServlet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@WarpTest
@RunAsClient
@RunWith(Arquillian.class)
public class CourseConfigurationControllerArquillianTest extends BaseArquillianTest{

    @Test
    public void shouldBeAbleToSaveConfiguration() throws Exception {
        System.out.println("Testing config page.. using driver: "+driver+"  .. fetching this url: " + baseUrl + "courseConfiguration");

        driver.navigate().to(baseUrl + "courseConfiguration");
        driver.findElement(By.id("classSetupLink")).click();
        System.out.println("Page source: \n" + driver.getPageSource());

        Warp.initiate(new Activity() {
            @Override
            public void perform() {
                System.err.println("Saving configuration");
                driver.findElement(By.id("saveCourseConfiguration")).click();
            }
        }).observe(HttpFilters.request().method().equal(HttpMethod.POST))
                .inspect(new Inspection() {
                    private static final long serialVersionUID = 2L;

                    @ArquillianResource
                    private ModelAndView modelAndView;

                    @AfterServlet
                    public void testAfterServlet() {
                        //cannot use logger above
                        System.out.println("modelAndView after save: " + modelAndView);
                        Boolean updateSuccessful = (Boolean) modelAndView.getModel().get("updateSuccessful");
                        assertEquals(true, updateSuccessful);
                    }
                });
    }

    @Test
    public void shouldBeAbleToUseSynchronizationButton() throws Exception {
        driver.navigate().to(baseUrl + "courseConfiguration");
        driver.findElement(By.id("classSetupLink")).click();
        System.out.println("Page source: \n" + driver.getPageSource());

        System.out.println("Synchronizing course");
        driver.findElement(By.id("synchronizeWithCanvas")).click();
        assertTrue(driver.findElement(By.id("synchronizationSuccessful")).isDisplayed());
    }
    
}
