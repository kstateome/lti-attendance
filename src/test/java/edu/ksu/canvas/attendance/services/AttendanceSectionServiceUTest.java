package edu.ksu.canvas.attendance.services;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import edu.ksu.canvas.attendance.entity.AttendanceSection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import edu.ksu.canvas.attendance.repository.AttendanceSectionRepository;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class AttendanceSectionServiceUTest {

    private AttendanceSectionService sectionService;
    
    @Mock
    private AttendanceSectionRepository mockSectionRepository;
    
    
    @Before
    public void setup() {
        sectionService = new AttendanceSectionService();
        Whitebox.setInternalState(sectionService, mockSectionRepository);
    }
    
    
    @Test
    public void getSection_HappyPath() {
        long canvasSectionId = 500;
        AttendanceSection expectedSection = new AttendanceSection();
        
        when(mockSectionRepository.findByCanvasSectionId(canvasSectionId)).thenReturn(expectedSection);
        AttendanceSection actualSection = sectionService.getSection(canvasSectionId);
        
        assertSame(expectedSection, actualSection);
    }
    
    @Test
    public void getFirstSectionOfCourse_NoCourseFound() {
        long nonExistantCanvaseCourseId = -1;
        
        when(mockSectionRepository.findByCanvasCourseId(nonExistantCanvaseCourseId)).thenReturn(new ArrayList<AttendanceSection>());
        AttendanceSection actualSection = sectionService.getFirstSectionOfCourse(nonExistantCanvaseCourseId);
        
        assertNull("If nonexistant course, should return null result", actualSection);
    }
    
    @Test
    public void getFirstSectionOfCourse() {
        long canvasCourseId = 500;
        AttendanceSection expectedFirstSection = new AttendanceSection();
        AttendanceSection secondSection = new AttendanceSection();
        List<AttendanceSection> sections = new ArrayList<AttendanceSection>();
        sections.add(expectedFirstSection);
        sections.add(secondSection);
        
        when(mockSectionRepository.findByCanvasCourseId(canvasCourseId)).thenReturn(sections);
        AttendanceSection actualFirstSection = sectionService.getFirstSectionOfCourse(canvasCourseId);

        assertEquals(expectedFirstSection, actualFirstSection);
    }
    
    @Test
    public void getSectionsByCourse_HappyPath() {
        long canvasCourseId = 500;
        List<AttendanceSection> sections = new ArrayList<>();
        AttendanceSection firstSection = new AttendanceSection();
        firstSection.setSectionId(1L);
        sections.add(firstSection);
        AttendanceSection secondSection = new AttendanceSection();
        secondSection.setSectionId(2L);
        sections.add(secondSection);
        
        when(mockSectionRepository.findByCanvasCourseId(canvasCourseId)).thenReturn(sections);
        List<AttendanceSection> actualSections = sectionService.getSectionsByCourse(canvasCourseId);
        
        assertThat(actualSections, contains(firstSection, secondSection));
    }
    
}
