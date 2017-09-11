package edu.ksu.canvas.attendance.controller.arquillian.page;

import edu.ksu.canvas.attendance.controller.arquillian.page.fragments.DateTableFragment;
import edu.ksu.canvas.attendance.controller.arquillian.page.fragments.SectionSelectDropDown;
import edu.ksu.canvas.attendance.controller.arquillian.page.fragments.StudentRoster;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Location("roster")
public class RosterPage {
    @Drone
    private WebDriver browser;

    @FindBy(id = "sectionSelectFormGroup")
    private SectionSelectDropDown sectionSelectDropDown;

    @FindBy(id = "dateTable")
    private DateTableFragment dateTableFragment;

    @FindBy(css = "sectionTable[hidden=false]")
    private StudentRoster visibleRoster;

    public void selectSection(String section) {
        sectionSelectDropDown.selectSection(section);
    }

    public void changeDate(String date) {
        dateTableFragment.changeDateAndSubmit(date);
        Graphene.waitAjax();
    }

    public void clickSaveButtonOnTop() {
        dateTableFragment.pressSaveButton();
    }

    public void deleteAttendance() {
        dateTableFragment.pressDeleteButton();
    }

    public WebElement getCurrentDate() {
        return dateTableFragment.getCurrentDate();
    }
}
