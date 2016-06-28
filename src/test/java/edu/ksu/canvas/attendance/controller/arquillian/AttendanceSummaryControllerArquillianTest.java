package edu.ksu.canvas.attendance.controller.arquillian;

import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;

@WarpTest
@RunAsClient
@RunWith(Arquillian.class)
public class AttendanceSummaryControllerArquillianTest extends BaseArquillianTest {

    @Test
    public void shouldBeAbleToViewReport() throws Exception {
        System.out.println("Testing summary page.. using driver: "+driver+"  .. fetching this url: " + baseUrl + "attendanceSummary");

        driver.navigate().to(baseUrl + "attendanceSummary");

        Warp.initiate(new Activity() {
            @Override
            public void perform() {
                driver.findElement(By.id("attendanceSummaryLink")).click();
            }
        }).observe(HttpFilters.request().uri().contains("attendanceSummary"))
                .inspect(new Inspection() {
                    private static final long serialVersionUID = 3L;

                    @ArquillianResource
                    private ModelAndView modelAndView;

                    @AfterServlet
                    public void testAfterServlet() {
                        List<AttendanceSummaryModel> summaryList = (ArrayList<AttendanceSummaryModel>) modelAndView.getModel().get("attendanceSummaryForSections");
                        assertFalse("There should be a report contained in the page", summaryList.isEmpty());

                    }
                });



    }

    @Test
    public void pageShouldHaveReportTable() throws Exception {
        System.out.println("Testing summary page.. using driver: "+driver+"  .. fetching this url: " + baseUrl + "attendanceSummary");

        driver.navigate().to(baseUrl + "attendanceSummary");
        driver.findElement(By.id("attendanceSummaryLink")).click();
        assertFalse("There should be at least one report table.", driver.findElements(By.className("sectionTable")).isEmpty());

    }
}
