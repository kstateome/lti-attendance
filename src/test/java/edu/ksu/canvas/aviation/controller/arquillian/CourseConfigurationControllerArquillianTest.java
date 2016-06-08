package edu.ksu.canvas.aviation.controller.arquillian;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.Activity;
import org.jboss.arquillian.warp.Inspection;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.arquillian.warp.client.filter.http.HttpFilters;
import org.jboss.arquillian.warp.servlet.AfterServlet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertEquals;


@WarpTest
@RunAsClient
@RunWith(Arquillian.class)
public class CourseConfigurationControllerArquillianTest extends BaseArquillianTest{

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
