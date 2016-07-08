package edu.ksu.canvas.attendance.util;

import edu.ksu.canvas.attendance.entity.AttendanceSection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DropDownOrganizerUTest {

    private static long sectionId1 = 1;
    private static long sectionId2 = 2;
    private static long sectionId3 = 3;
    private List<AttendanceSection> preSortedAttendanceSections = new ArrayList<>();
    private List<AttendanceSection> unSortedAttendanceSections = new ArrayList<>();
    private AttendanceSection section1 = new AttendanceSection();
    private AttendanceSection section2 = new AttendanceSection();
    private AttendanceSection section3 = new AttendanceSection();


    public DropDownOrganizerUTest() {
        section1.setCanvasSectionId(sectionId1);
        section1.setName("A");
        section2.setCanvasSectionId(sectionId2);
        section2.setName("B");
        section3.setCanvasSectionId(sectionId3);
        section3.setName("C");
    }

    @Before
    public void setupPresortedAttendanceSections() {
        preSortedAttendanceSections = Arrays.asList(section1, section2, section3);
    }

    @Before
    public void setupUnSortedAttendanceSections() {
        unSortedAttendanceSections = Arrays.asList(section3, section1, section2);
    }

    @Test
    public void sortsWithNullSelectedAttendanceSectionId() {
        List<AttendanceSection> sortedAttendanceSections = DropDownOrganizer.sortWithSelectedSectionFirst(unSortedAttendanceSections, null);
        Assert.assertEquals("Expected sorted preSortedAttendanceSections to match presortedAttendanceSections", preSortedAttendanceSections, sortedAttendanceSections);
    }

    @Test
    public void putsSelectedAttendanceSectionInFirstPosition() {
        long selectedAttendanceSectionId = sectionId3;
        List<AttendanceSection> sortedAttendanceSections = DropDownOrganizer.sortWithSelectedSectionFirst(unSortedAttendanceSections, String.valueOf(selectedAttendanceSectionId));
        long index0AttendanceSectionId = sortedAttendanceSections.get(0).getCanvasSectionId();
        Assert.assertEquals("Expected selected section to be at index 0 after sorted", selectedAttendanceSectionId, index0AttendanceSectionId);
    }

    @Test
    public void restOfListIsSortedWhenAttendanceSectionIsSelected() {
        long selectedAttendanceSectionId = sectionId3;
        List<AttendanceSection> sortedAttendanceSections = DropDownOrganizer.sortWithSelectedSectionFirst(unSortedAttendanceSections, String.valueOf(selectedAttendanceSectionId));
        long index1AttendanceSectionId = sortedAttendanceSections.get(1).getCanvasSectionId();
        long index2AttendanceSectionId = sortedAttendanceSections.get(2).getCanvasSectionId();
        Assert.assertEquals("Expected section 1 to be at 2nd position of list", sectionId1, index1AttendanceSectionId);
        Assert.assertEquals("Expected section 2 to be at 3rd position of list", sectionId2, index2AttendanceSectionId);
    }

}
