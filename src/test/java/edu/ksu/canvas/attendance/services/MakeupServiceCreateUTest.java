package edu.ksu.canvas.attendance.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import edu.ksu.canvas.attendance.entity.Makeup;
import edu.ksu.canvas.attendance.form.MakeupForm;


@RunWith(MockitoJUnitRunner.class)
public class MakeupServiceCreateUTest extends BaseMakeupServiceUTest {

    private long studentId = 500;
    long sectionId = 600;
    private AttendanceStudent student;
    private Makeup firstMakeup, secondMakeup;

    
    @Before
    public void additionalSetup() {
        student = new AttendanceStudent();
        student.setStudentId(studentId);
        
        firstMakeup = new Makeup();
        firstMakeup.setId(1L);
        secondMakeup = new Makeup();
        secondMakeup.setId(2L);
    }
    
    
    @Test(expected = ContextedRuntimeException.class)
    public void createMakeupForm_NonExistantStudent() {
        long nonExistantStudentId = -1;
        long sectionId = 500;
        boolean addEmptyEntry = true;
        
        when(mockStudentRepository.findByStudentId(nonExistantStudentId)).thenReturn(null);
        makeupService.createMakeupForm(nonExistantStudentId, sectionId, addEmptyEntry);
    }
    
    @Test
    public void createMakeupForm_HappyPathWithNoEmptyEntry() {
        boolean dontAddEmptyEntry = false;
        List<Makeup> expectedMakeups = new ArrayList<Makeup>();
        expectedMakeups.add(firstMakeup);
        expectedMakeups.add(secondMakeup);
        
        when(mockStudentRepository.findByStudentId(studentId)).thenReturn(student);
        when(mockMakeupRepository.findByAviationStudentOrderByDateOfClassAsc(student)).thenReturn(expectedMakeups);
        MakeupForm makeupForm = makeupService.createMakeupForm(studentId, sectionId, dontAddEmptyEntry);
        
        assertEquals(sectionId, makeupForm.getSectionId());
        assertEquals(studentId, makeupForm.getStudentId());
        assertTrue(makeupFormContainsEntry(makeupForm, firstMakeup));
        assertTrue(makeupFormContainsEntry(makeupForm, secondMakeup));
    }
    
    private boolean makeupFormContainsEntry(MakeupForm makeupForm, Makeup makeup) {
        return makeupForm.getEntries()
                         .stream().filter(entry -> entry.getMakeupId() == makeup.getMakeupId())
                         .count() == 1;
    }
    
    @Test
    public void createMakeupForm_HappyPathWithEmptyEntry() {
        boolean addEmptyEntry = true;
        List<Makeup> expectedMakeups = new ArrayList<Makeup>();
        
        when(mockStudentRepository.findByStudentId(studentId)).thenReturn(student);
        when(mockMakeupRepository.findByAviationStudentOrderByDateOfClassAsc(student)).thenReturn(expectedMakeups);
        MakeupForm makeupForm = makeupService.createMakeupForm(studentId, sectionId, addEmptyEntry);
        
        assertEquals("Should contain one empty entry", 1, makeupForm.getEntries().size());
    }
    

}
