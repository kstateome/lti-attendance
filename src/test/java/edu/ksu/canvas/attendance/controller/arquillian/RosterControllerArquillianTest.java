package edu.ksu.canvas.attendance.controller.arquillian;

import edu.ksu.canvas.attendance.controller.arquillian.page.RosterPage;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.graphene.page.InitialPage;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertTrue;

@RunAsClient
@RunWith(Arquillian.class)
public class RosterControllerArquillianTest extends BaseArquillianTest {

    private static final String page = "roster";

    @Page
    private RosterPage rosterPage;

    @Test
    public void shouldBeAbleToSave(@InitialPage RosterPage rosterPage) throws Exception {
        rosterPage.clickSaveButtonOnTop();

        assertTrue("Expected save success message to be displayed after saving attendance", driver.findElement(By.id("saveSuccessMessage")).isDisplayed());
    }

    @Test
    public void shouldBeAbleToChangeDate(@InitialPage RosterPage rosterPage) throws Exception {
        final String arbitraryDate = "12/1/2016";
        rosterPage.changeDate(arbitraryDate);

        WebElement changedDate = rosterPage.getCurrentDate();
        String changedDateValue = changedDate.getAttribute("value");
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");

        assertTrue("Date should be what was entered by the user", org.apache.commons.lang3.time.DateUtils.isSameDay(format.parse(arbitraryDate), format.parse(changedDateValue)));
    }

}
