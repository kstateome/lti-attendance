package edu.ksu.canvas.aviation.controller.arquillian;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.warp.WarpTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.junit.Assert.assertTrue;

@WarpTest
@RunAsClient
@RunWith(Arquillian.class)
public class MakeupControllerArquillianTest extends BaseArquillianTest{

    @Test
    public void shouldBeAbleToGetMakeupPage() throws Exception{
        System.out.println("Testing makeup page...");

        driver.navigate().to(baseUrl + "roster");
        driver.findElement(By.id("attendanceSummaryLink")).click();

        System.err.println("Page source: \n" + driver.getPageSource());
        driver.findElement(By.cssSelector("a[href*='studentMakeup']")).click();
        assertTrue(driver.findElement(By.id("makeupForm")).isDisplayed());

    }

}
