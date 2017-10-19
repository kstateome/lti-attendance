package edu.ksu.canvas.attendance.controller.arquillian.page.fragments;

import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class StudentRoster {
    @Root
    private WebElement root;

    @FindBy(css = "sectionTable")
    private List<StudentRow> studentRows;

    public List<StudentRow> getStudentRows() {
        return studentRows;
    }

    public void setStudentRows(List<StudentRow> studentRows) {
        this.studentRows = studentRows;
    }
}
