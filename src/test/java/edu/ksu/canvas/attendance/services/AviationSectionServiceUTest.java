package edu.ksu.canvas.attendance.services;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import edu.ksu.canvas.attendance.entity.AviationSection;
import edu.ksu.canvas.attendance.repository.AviationSectionRepository;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class AviationSectionServiceUTest {

    private AviationSectionService sectionService;
    
    @Mock
    private AviationSectionRepository mockSectionRepository;
    
    
    @Before
    public void setup() {
        sectionService = new AviationSectionService();
        Whitebox.setInternalState(sectionService, mockSectionRepository);
    }
    
    
    @Test
    public void getSection_HappyPath() {
        long canvasSectionId = 500;
        AviationSection expectedSection = new AviationSection();
        
        when(mockSectionRepository.findByCanvasSectionId(canvasSectionId)).thenReturn(expectedSection);
        AviationSection actualSection = sectionService.getSection(canvasSectionId);
        
        assertSame(expectedSection, actualSection);
    }
    
    @Test
    public void getFirstSectionOfCourse_NoCourseFound() {
        long nonExistantCanvaseCourseId = -1;
        
        when(mockSectionRepository.findByCanvasCourseId(nonExistantCanvaseCourseId)).thenReturn(new ArrayList<AviationSection>());
        AviationSection actualSection = sectionService.getFirstSectionOfCourse(nonExistantCanvaseCourseId);
        
        assertNull("If nonexistant course, should return null result", actualSection);
    }
    
    @Test
    public void getFirstSectionOfCourse() {
        long canvasCourseId = 500;
        AviationSection expectedFirstSection = new AviationSection();
        AviationSection secondSection = new AviationSection();
        List<AviationSection> sections = new ArrayList<AviationSection>();
        sections.add(expectedFirstSection);
        sections.add(secondSection);
        
        when(mockSectionRepository.findByCanvasCourseId(canvasCourseId)).thenReturn(sections);
        AviationSection actualFirstSection = sectionService.getFirstSectionOfCourse(canvasCourseId);

        assertEquals(expectedFirstSection, actualFirstSection);
    }
    
    @Test
    public void getSectionsByCourse_HappyPath() {
        long canvasCourseId = 500;
        List<AviationSection> sections = new ArrayList<>();
        AviationSection firstSection = new AviationSection();
        firstSection.setSectionId(1L);
        sections.add(firstSection);
        AviationSection secondSection = new AviationSection();
        secondSection.setSectionId(2L);
        sections.add(secondSection);
        
        when(mockSectionRepository.findByCanvasCourseId(canvasCourseId)).thenReturn(sections);
        List<AviationSection> actualSections = sectionService.getSectionsByCourse(canvasCourseId);
        
        assertThat(actualSections, contains(firstSection, secondSection));
    }
    
}
