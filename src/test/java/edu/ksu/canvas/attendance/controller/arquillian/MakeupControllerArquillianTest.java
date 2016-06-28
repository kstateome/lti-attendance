package edu.ksu.canvas.attendance.controller.arquillian;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.warp.WarpTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@WarpTest
@RunAsClient
@RunWith(Arquillian.class)
public class MakeupControllerArquillianTest extends BaseArquillianTest{

    @Test
    public void shouldBeAbleToGetMakeupPage() throws Exception{
        System.out.println("Testing makeup page...");

        navigateToMakeupPage();
        assertTrue("Makeup form should be displayed", driver.findElement(By.id("makeupForm")).isDisplayed());

    }

    @Test
    public void shouldBeAbleToAddMakeupRow() throws Exception{
        System.out.println("Testing adding makeup row...");

        final String arbitraryDate = "12/1/16";
        final String arbitrayValue = "20";
        final String arbitraryDescription = "Something";

        navigateToMakeupPage();
        driver.findElement(By.id("addMakeupBtn")).click();
        List<WebElement> makeupRows = driver.findElements(By.className("addedMakeupRow"));
        assertFalse("Clicking the add makeup btn should add a new row", makeupRows.isEmpty());
        WebElement newMakeupRow = makeupRows.get(0);
        newMakeupRow.findElement(By.cssSelector("input[name*='dateOfClass']")).sendKeys(arbitraryDate);
        newMakeupRow.findElement(By.cssSelector("input[name*='dateMadeUp']")).sendKeys(arbitraryDate);
        newMakeupRow.findElement(By.cssSelector("input[name*='minutesMadeUp']")).sendKeys(arbitrayValue);
        newMakeupRow.findElement(By.cssSelector("input[name*='projectDescription']")).sendKeys(arbitraryDescription);

        driver.findElement(By.id("saveMakeupBtn")).click();
        assertTrue("Should be able to save the makeup successfully", driver.findElement(By.id("saveSuccessMessage")).isDisplayed());

    }

    private void navigateToMakeupPage() {
        driver.navigate().to(baseUrl + "roster");
        driver.findElement(By.id("attendanceSummaryLink")).click();
        driver.findElement(By.cssSelector("a[href*='studentMakeup']")).click();
    }

}
