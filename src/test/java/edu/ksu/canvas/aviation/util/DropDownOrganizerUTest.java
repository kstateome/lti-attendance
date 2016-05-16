package edu.ksu.canvas.aviation.util;

import edu.ksu.canvas.model.Section;
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
    private List<Section> preSortedSections = new ArrayList<>();
    private List<Section> unSortedSections = new ArrayList<>();
    private Section section1 = new Section();
    private Section section2 = new Section();
    private Section section3 = new Section();

    public DropDownOrganizerUTest() {
        section1.setId(sectionId1);
        section1.setName("A");
        section2.setId(sectionId2);
        section2.setName("B");
        section3.setId(sectionId3);
        section3.setName("C");
    }
    @Before
    public void setupPresortedSections() {
        preSortedSections = Arrays.asList(section1, section2, section3);
    }

    @Before
    public void setupUnSortedSections() {
        unSortedSections = Arrays.asList(section3, section1, section2);
    }

    @Test
    public void sortsWithNullSelectedSectionId() {
        List<Section> sortedSections = DropDownOrganizer.sortWithSelectedSectionFirst(unSortedSections, null);
        Assert.assertEquals("Expected sorted preSortedSections to match presortedSections", preSortedSections, sortedSections);
    }

    @Test
    public void putsSelectedSectionInFirstPosition() {
        long selectedSectionId = sectionId3;
        List<Section> sortedSections = DropDownOrganizer.sortWithSelectedSectionFirst(unSortedSections, String.valueOf(selectedSectionId));
        long index0SectionId = sortedSections.get(0).getId();
        Assert.assertEquals("Expected selected section to be at index 0 after sorted", selectedSectionId, index0SectionId);
    }

    @Test
    public void restOfListIsSortedWhenSectionIsSelected() {
        long selectedSectionId = sectionId3;
        List<Section> sortedSections = DropDownOrganizer.sortWithSelectedSectionFirst(unSortedSections, String.valueOf(selectedSectionId));
        long index1SectionId = sortedSections.get(1).getId();
        long index2SectionId = sortedSections.get(2).getId();
        Assert.assertEquals("Expected section 1 to be at 2nd position of list", sectionId1, index1SectionId);
        Assert.assertEquals("Expected section 2 to be at 3rd position of list", sectionId2, index2SectionId);
    }
}
