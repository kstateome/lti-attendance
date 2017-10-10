package edu.ksu.canvas.attendance.controller.arquillian.page.fragments;

import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DateTableFragment {
    @Root
    private WebElement root;

    @FindBy(id = "currentDate")
    private WebElement currentDate;

    @FindBy(className = "datePicker")
    private WebElement datePicker;

    @FindBy(id = "saveAttendanceOnTop")
    private WebElement saveButton;

    @FindBy(id = "deleteAttendance")
    private WebElement deleteButton;

    public void pressSaveButton() {
        saveButton.click();
    }

    public void pressDeleteButton() {
        deleteButton.click();
    }

    public void changeDateAndSubmit(String date) {
        removeDateText();
        enterDateText(date);
    }

    private void removeDateText() {
        for (int i = 0; i < 20; i++) {
            currentDate.sendKeys(Keys.BACK_SPACE);
        }
    }

    private void enterDateText(String date) {
        currentDate.sendKeys(date);
        currentDate.sendKeys(Keys.ENTER);
    }

    public WebElement getCurrentDate() {
        return currentDate;
    }
}
