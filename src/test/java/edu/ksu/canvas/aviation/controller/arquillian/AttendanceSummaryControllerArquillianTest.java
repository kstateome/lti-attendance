package edu.ksu.canvas.aviation.controller.arquillian;

import edu.ksu.canvas.aviation.model.AttendanceSummaryModel;
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

import static org.junit.Assert.assertEquals;

@WarpTest
@RunAsClient
@RunWith(Arquillian.class)
public class AttendanceSummaryControllerArquillianTest extends BaseArquillianTest {

    @Test
    public void shouldBeAbleToViewReport() throws Exception {
        System.out.println("Testing summary page.. using driver: "+driver+"  .. fetching this url: " + baseUrl + "attendanceSummary");

        driver.navigate().to(baseUrl + "attendanceSummary");

        System.out.println("Page source: \n" + driver.getPageSource());

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
                        System.out.println("modelAndView: " + modelAndView);

                        List<AttendanceSummaryModel> summaryList = (ArrayList<AttendanceSummaryModel>) modelAndView.getModel().get("attendanceSummaryForSections");
                        assertEquals(false, summaryList.isEmpty());

                    }
                });



    }
}
