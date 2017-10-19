package edu.ksu.canvas.attendance.controller.arquillian.page.fragments;

import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SectionSelectDropDown {
    @Root
    private WebElement root;

    public void selectSection(String section) {
        root.findElement(By.cssSelector("option[value=" + section + "]")).click();
    }
}
