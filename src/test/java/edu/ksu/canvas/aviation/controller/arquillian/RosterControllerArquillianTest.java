package edu.ksu.canvas.aviation.controller.arquillian;

import edu.ksu.canvas.aviation.form.RosterForm;
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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.springframework.web.servlet.ModelAndView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@WarpTest
@RunAsClient
@RunWith(Arquillian.class)
public class RosterControllerArquillianTest extends BaseArquillianTest {

    private static final String page = "roster";

    @Test
    public void shouldBeAbleToSave() throws Exception {
        System.out.println("Testing Roster page.. using driver: "+driver+"  .. fetching this url: " + baseUrl + page);

        driver.navigate().to(baseUrl + page);

        Warp.initiate(new Activity() {

            @Override
            public void perform() {
                System.err.println("Submitting roster form");
                driver.findElement(By.id("saveAttendanceOnTop")).click();
            }
        }).observe(HttpFilters.request().uri().contains(page))
                .inspect(new Inspection() {
                    private static final long serialVersionUID = 1L;

                    @ArquillianResource
                    private ModelAndView modelAndView;

                    @AfterServlet
                    public void testAfterServlet() {

                        Boolean saveSuccessExpected = Boolean.TRUE;
                        Boolean saveSuccessActual = (Boolean) modelAndView.getModel().get("saveSuccess");
                        assertEquals(saveSuccessExpected, saveSuccessActual);
                    }
                });
    }

    @Test
    public void shouldBeAbleToChangeDate() throws Exception {
        final String arbitraryDate = "12/1/2016";
        driver.navigate().to(baseUrl + page);

        Warp.initiate(new Activity() {
            @Override
            public void perform() {
                WebElement element = driver.findElement(By.id("currentDate"));
                for (int i = 0; i < 20; i++) {
                    element.sendKeys(Keys.BACK_SPACE);
                }
                element.sendKeys(arbitraryDate);
                element.sendKeys(Keys.ENTER);
                driver.findElement(By.id("sectionSelect")).click();
            }
        }).observe(HttpFilters.request().method().equal(HttpMethod.POST))
                .inspect(new Inspection() {
                    private static final long serialVersionUID = 1L;

                    @ArquillianResource
                    private ModelAndView modelAndView;

                    @AfterServlet
                    public void testAfterServlet() throws ParseException {
                        RosterForm form = (RosterForm) modelAndView.getModel().get("rosterForm");
                        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                        Date date = format.parse(arbitraryDate);
                        assertTrue("Form should have the correct date.", org.apache.commons.lang3.time.DateUtils.isSameDay(date, form.getCurrentDate()));
                    }
                });



    }
}
