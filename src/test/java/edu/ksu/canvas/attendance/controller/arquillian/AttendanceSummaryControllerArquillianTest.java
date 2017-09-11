package edu.ksu.canvas.attendance.controller.arquillian;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.junit.Assert.assertFalse;

@RunAsClient
@RunWith(Arquillian.class)
public class AttendanceSummaryControllerArquillianTest extends BaseArquillianTest {

    @Test
    public void pageShouldHaveReportTable() throws Exception {
        System.out.println("Testing summary page.. using driver: "+driver+"  .. fetching this url: " + baseUrl + "attendanceSummary");

        driver.navigate().to(baseUrl + "attendanceSummary");
        driver.findElement(By.id("attendanceSummaryLink")).click();
        assertFalse("There should be at least one report table.", driver.findElements(By.className("sectionTable")).isEmpty());

    }
}
