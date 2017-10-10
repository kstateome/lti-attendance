package edu.ksu.canvas.attendance.controller.arquillian.page.fragments;

import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class StudentRow {
    @Root
    private WebElement root;

    @FindBy(css = "studentName")
    private WebElement studentName;

    @FindBy(css = "studentSisUserId")
    private WebElement studentSisUserId;

    @FindBy(css = "studentStatus")
    private WebElement studentStatus;

    @FindBy(css = "studentNotes")
    private WebElement studentNotes;

}
