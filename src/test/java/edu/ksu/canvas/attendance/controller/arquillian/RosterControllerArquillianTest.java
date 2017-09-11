package edu.ksu.canvas.attendance.controller.arquillian;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertTrue;

@RunAsClient
@RunWith(Arquillian.class)
public class RosterControllerArquillianTest extends BaseArquillianTest {

    private static final String page = "roster";

    @Test
    public void shouldBeAbleToSave() throws Exception {
        System.out.println("Testing Roster page.. using driver: "+driver+"  .. fetching this url: " + baseUrl + page);

        driver.navigate().to(baseUrl + page);
        driver.findElement(By.id("saveAttendanceOnTop")).click();

        assertTrue("Expected save success mesage to be displayed after saving attendence", driver.findElement(By.id("saveSuccessMessage")).isDisplayed());
    }

    @Test
    public void shouldBeAbleToChangeDate() throws Exception {
        final String arbitraryDate = "12/1/2016";
        driver.navigate().to(baseUrl + page);

        WebElement element = driver.findElement(By.id("currentDate"));
        for (int i = 0; i < 20; i++) {
            element.sendKeys(Keys.BACK_SPACE);
        }
        element.sendKeys(arbitraryDate);
        element.sendKeys(Keys.ENTER);
        driver.findElement(By.id("sectionSelect")).click();

        WebElement changedDate = driver.findElement(By.id("currentDate"));
        String changedDateValue = changedDate.getAttribute("value");
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");

        assertTrue("Date should be what was entered by the user", org.apache.commons.lang3.time.DateUtils.isSameDay(format.parse(arbitraryDate), format.parse(changedDateValue)));
    }
}
