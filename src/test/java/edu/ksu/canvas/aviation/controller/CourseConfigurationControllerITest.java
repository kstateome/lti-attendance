package edu.ksu.canvas.aviation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.ksu.canvas.aviation.entity.AviationCourse;
import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.repository.AviationCourseRepository;
import edu.ksu.canvas.aviation.repository.AviationSectionRepository;
import edu.ksu.canvas.aviation.services.CanvasApiWrapperService;
import edu.ksu.canvas.aviation.services.SynchronizationService;
import edu.ksu.canvas.error.NoLtiSessionException;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
public class CourseConfigurationControllerITest extends BaseControllerITest {

    private AviationCourse existingCourse;
    private AviationSection existingSection;
    
    @Autowired
    private AviationCourseRepository courseRepository;
    
    @Autowired
    private AviationSectionRepository sectionRepository;
    
    @Autowired
    private CanvasApiWrapperService canvasService;


    @Before
    public void additionalSetup() throws NoLtiSessionException {
        existingCourse = new AviationCourse();
        existingCourse.setCanvasCourseId(2000L);
        existingCourse.setDefaultMinutesPerSession(10);
        existingCourse.setTotalMinutes(SynchronizationService.DEFAULT_TOTAL_CLASS_MINUTES);
        existingCourse = courseRepository.save(existingCourse);
        
        existingSection = new AviationSection();
        existingSection.setCanvasCourseId(existingCourse.getCanvasCourseId());
        existingSection.setCanvasSectionId(1000L);
        existingSection = sectionRepository.save(existingSection);

        when(canvasService.getCourseId()).thenReturn(existingCourse.getCanvasCourseId().intValue());
    }


    @Test
    public void classSetup_nonExistantSectionId() throws Exception {
        Long nonExistantSectionId = 2000L;
        
        mockMvc.perform(get("/courseConfiguration/"+nonExistantSectionId))
            .andExpect(status().isOk())
            .andExpect(view().name("forward:roster"));
    }

    @Test
    public void classSetup_badNumberForSectionId() throws Exception {
        String badSectionId = "hackerDelight";
        
        mockMvc.perform(get("/courseConfiguration/"+badSectionId))
            .andExpect(status().isOk())
            .andExpect(view().name("forward:roster"));
    }

    @Test
    public void classSetup_existingSectionId_HappyPath() throws Exception {
        mockMvc.perform(get("/courseConfiguration/"+existingSection.getCanvasSectionId()))
            .andExpect(status().isOk())
            .andExpect(view().name("courseConfiguration"))
            .andExpect(model().attribute("selectedSectionId", is(existingSection.getCanvasSectionId())))
            .andExpect(model().attribute("updateSuccessful", is(false)))
            .andExpect(model().attribute("courseConfigurationForm", notNullValue()));
    }
    
    @Test
    public void saveCourseConfiguration_HappyPath() throws Exception {
        Long irrlevantSectionId = 3000L;
        Integer expectedDefaultMinutesPerSession = 100;
        Integer expectedTotalClassMinutes = 1000;
        
        mockMvc.perform(post("/courseConfiguration/"+irrlevantSectionId+"/save")
                .param("saveCourseConfiguration", "Save Course Configuration")
                .param("defaultMinutesPerSession", String.valueOf(expectedDefaultMinutesPerSession))
                .param("totalClassMinutes", String.valueOf(expectedTotalClassMinutes)))
                .andExpect(status().isOk())
                .andExpect(view().name("forward:/courseConfiguration/"+irrlevantSectionId+"?updateSuccessful=true"));
        
        AviationCourse course = courseRepository.findByCourseId(existingCourse.getCourseId());
        assertEquals(expectedDefaultMinutesPerSession, course.getDefaultMinutesPerSession());
        assertEquals(expectedTotalClassMinutes, course.getTotalMinutes());
    }
    
    @Test
    public void saveCourseConfiguration_BadFormData() throws Exception {
        Long irrlevantSectionId = 3000L;
        Integer invalidDefaultMinutesPerSession = -1;
        Integer invalidTotalClassMinutes = -1;
        
        String postPageURL = "/courseConfiguration/"+irrlevantSectionId+"/save";
        mockMvc.perform(post(postPageURL)
                .param("saveCourseConfiguration", "Save Course Configuration")
                .param("defaultMinutesPerSession", String.valueOf(invalidDefaultMinutesPerSession))
                .param("totalClassMinutes", String.valueOf(invalidTotalClassMinutes)))
                .andExpect(status().isOk())
                .andExpect(view().name("/courseConfiguration"))
                .andExpect(model().attribute("selectedSectionId", is(String.valueOf(irrlevantSectionId))))
                .andExpect(model().attribute("error", notNullValue()));
    }
    
    @Test
    public void synchronizeWithCanvas_HappyPath() throws Exception {
        Long irrlevantSectionId = 3000L;
        
        mockMvc.perform(post("/courseConfiguration/"+irrlevantSectionId+"/save")
                .param("synchronizeWithCanvas", "Synchronize With Canvas"))
                .andExpect(status().isOk())
                .andExpect(view().name("forward:/courseConfiguration/"+irrlevantSectionId));
    }
    
}
