package edu.ksu.canvas.aviation.controller.arquillian;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.warp.WarpTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

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

        navigateToMakeupPage();
        driver.findElement(By.id("addMakeupBtn")).click();
        assertFalse("Clicking the add makeup btn should add a new row", driver.findElements(By.className("addedMakeupRow")).isEmpty());
    }

    private void navigateToMakeupPage() {
        driver.navigate().to(baseUrl + "roster");
        driver.findElement(By.id("attendanceSummaryLink")).click();
        driver.findElement(By.cssSelector("a[href*='studentMakeup']")).click();
        System.out.println("Page source: \n" + driver.getPageSource());
    }

}
