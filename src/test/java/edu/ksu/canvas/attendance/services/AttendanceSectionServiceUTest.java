package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.repository.AttendanceAssignmentRepository;
import edu.ksu.canvas.attendance.repository.AttendanceSectionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AttendanceSectionServiceUTest {

    private static final long CANVAS_COURSE_ID = 1000L;
    private static final String SECTION_NAME = "SECTION NAME";
    private static final long CANVAS_SECTION_ID = 1010L;
    private static final long SECTION_ID = 1111L;
    private static final String ASSIGNMENT_NAME = "ASSIGNMENT NAME";
    private AttendanceSectionService sectionService;
    
    @Mock
    private AttendanceSectionRepository mockSectionRepository;

    @Mock
    private AttendanceAssignmentRepository mockAssignmentRepository;
    
    
    @Before
    public void setup() {
        sectionService = new AttendanceSectionService();
        Whitebox.setInternalState(sectionService, mockSectionRepository);
        Whitebox.setInternalState(sectionService, mockAssignmentRepository);
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

    @Test
    public void resetAttendanceAssignment_HappyPath() {
        AttendanceSection attendanceSection = new AttendanceSection();
        attendanceSection.setCanvasCourseId(CANVAS_COURSE_ID);
        attendanceSection.setName(SECTION_NAME);
        attendanceSection.setCanvasSectionId(CANVAS_SECTION_ID);
        attendanceSection.setSectionId(SECTION_ID);

        List<AttendanceSection> sectionList = new ArrayList<>();
        sectionList.add(attendanceSection);

        AttendanceAssignment attendanceAssignment = new AttendanceAssignment();
        attendanceAssignment.setGradingOn(true);
        attendanceAssignment.setAssignmentName(ASSIGNMENT_NAME);

        when(mockSectionRepository.findByCanvasCourseId(CANVAS_COURSE_ID)).thenReturn(sectionList);
        when(mockAssignmentRepository.findByAttendanceSection(sectionList.get(0))).thenReturn(attendanceAssignment);

        sectionService.resetAttendanceAssignmentsForCourse(CANVAS_COURSE_ID);
        assertEquals(null, attendanceAssignment.getAssignmentName());
        assertEquals(false, attendanceAssignment.getGradingOn());
    }
    
}
