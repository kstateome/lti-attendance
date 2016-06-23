package edu.ksu.canvas.attendance.util;

import edu.ksu.canvas.attendance.entity.AviationSection;
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
    private List<AviationSection> preSortedAviationSections = new ArrayList<>();
    private List<AviationSection> unSortedAviationSections = new ArrayList<>();
    private AviationSection section1 = new AviationSection();
    private AviationSection section2 = new AviationSection();
    private AviationSection section3 = new AviationSection();


    public DropDownOrganizerUTest() {
        section1.setCanvasSectionId(sectionId1);
        section1.setName("A");
        section2.setCanvasSectionId(sectionId2);
        section2.setName("B");
        section3.setCanvasSectionId(sectionId3);
        section3.setName("C");
    }

    @Before
    public void setupPresortedAviationSections() {
        preSortedAviationSections = Arrays.asList(section1, section2, section3);
    }

    @Before
    public void setupUnSortedAviationSections() {
        unSortedAviationSections = Arrays.asList(section3, section1, section2);
    }

    @Test
    public void sortsWithNullSelectedAviationSectionId() {
        List<AviationSection> sortedAviationSections = DropDownOrganizer.sortWithSelectedSectionFirst(unSortedAviationSections, null);
        Assert.assertEquals("Expected sorted preSortedAviationSections to match presortedAviationSections", preSortedAviationSections, sortedAviationSections);
    }

    @Test
    public void putsSelectedAviationSectionInFirstPosition() {
        long selectedAviationSectionId = sectionId3;
        List<AviationSection> sortedAviationSections = DropDownOrganizer.sortWithSelectedSectionFirst(unSortedAviationSections, String.valueOf(selectedAviationSectionId));
        long index0AviationSectionId = sortedAviationSections.get(0).getCanvasSectionId();
        Assert.assertEquals("Expected selected section to be at index 0 after sorted", selectedAviationSectionId, index0AviationSectionId);
    }

    @Test
    public void restOfListIsSortedWhenAviationSectionIsSelected() {
        long selectedAviationSectionId = sectionId3;
        List<AviationSection> sortedAviationSections = DropDownOrganizer.sortWithSelectedSectionFirst(unSortedAviationSections, String.valueOf(selectedAviationSectionId));
        long index1AviationSectionId = sortedAviationSections.get(1).getCanvasSectionId();
        long index2AviationSectionId = sortedAviationSections.get(2).getCanvasSectionId();
        Assert.assertEquals("Expected section 1 to be at 2nd position of list", sectionId1, index1AviationSectionId);
        Assert.assertEquals("Expected section 2 to be at 3rd position of list", sectionId2, index2AviationSectionId);
    }

}
