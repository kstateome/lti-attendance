package edu.ksu.canvas.aviation.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import edu.ksu.canvas.aviation.entity.Makeup;
import edu.ksu.canvas.aviation.form.MakeupForm;
import edu.ksu.canvas.aviation.model.MakeupModel;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class MakeupServiceSaveUTest extends BaseMakeupServiceUTest {

    private MakeupForm form;
    private long studentId = 500;
    private List<MakeupModel> entries;
    
    
    @Before
    public void additionalSetup() {
        entries = new ArrayList<>();
        
        form = new MakeupForm();
        form.setStudentId(studentId);
        form.setEntries(entries);
    }
    
    
    @Test(expected = NullPointerException.class)
    public void save_NullMakeupForm() {
        makeupService.save(null);
    }
    
    @Test
    public void save_DeleteFlaggedEntry() {
        Long makeupIdToDelete = 200L;
        MakeupModel flaggedForDeletion = new MakeupModel();
        flaggedForDeletion.setToBeDeletedFlag(true);
        flaggedForDeletion.setMakeupId(makeupIdToDelete);
        entries.add(flaggedForDeletion);
        
        makeupService.save(form);
        
        verify(mockMakeupRepository, atLeastOnce()).delete(makeupIdToDelete);
    }
    
    @Test
    public void save_CreateNewEntry() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date expectedDateMadeup = sdf.parse("01/02/2016");
        Date expectedDateOfClass = sdf.parse("01/01/2016");
        Integer expectedMinutesMadeUp = 10;
        String expectedProjectDescription = "Sophistication at its finest";
        
        MakeupModel unsavedEntry = new MakeupModel();
        unsavedEntry.setDateMadeUp(expectedDateMadeup);
        unsavedEntry.setDateOfClass(expectedDateOfClass);
        unsavedEntry.setMinutesMadeUp(expectedMinutesMadeUp);
        unsavedEntry.setProjectDescription(expectedProjectDescription);
        
        entries.add(unsavedEntry);
        ArgumentCaptor<Makeup> capturedMakeup = ArgumentCaptor.forClass(Makeup.class);
        
        makeupService.save(form);
        
        verify(mockMakeupRepository, atLeastOnce()).save(capturedMakeup.capture());
        assertEquals(expectedDateMadeup, capturedMakeup.getValue().getDateMadeUp());
        assertEquals(expectedDateOfClass, capturedMakeup.getValue().getDateOfClass());
        assertEquals(expectedMinutesMadeUp, capturedMakeup.getValue().getMinutesMadeUp());
        assertEquals(expectedProjectDescription, capturedMakeup.getValue().getProjectDescription());
    }
    
    
    @Test
    public void save_UpdateExistingEntry() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Long existingMakeupId = 500L;
        Date existingDateMadeup = sdf.parse("01/02/2016");
        Date existingDateOfClass = sdf.parse("01/01/2016");
        Integer existingMinutesMadeUp = 10;
        String existingProjectDescription = "Sophistication at its finest";
        Makeup previouslySavedMakeup = new Makeup();
        previouslySavedMakeup.setDateMadeUp(existingDateMadeup);
        previouslySavedMakeup.setDateOfClass(existingDateOfClass);
        previouslySavedMakeup.setMinutesMadeUp(existingMinutesMadeUp);
        previouslySavedMakeup.setProjectDescription(existingProjectDescription);
        
        Date expectedDateMadeup = sdf.parse("02/02/2016");
        Date expectedDateOfClass = sdf.parse("02/01/2016");
        Integer expectedMinutesMadeUp = 20;
        String expectedProjectDescription = "Not Sophisticated";
        MakeupModel unsavedEntry = new MakeupModel();
        unsavedEntry.setMakeupId(existingMakeupId);
        unsavedEntry.setDateMadeUp(expectedDateMadeup);
        unsavedEntry.setDateOfClass(expectedDateOfClass);
        unsavedEntry.setMinutesMadeUp(expectedMinutesMadeUp);
        unsavedEntry.setProjectDescription(expectedProjectDescription);
        entries.add(unsavedEntry);
        
        when(mockMakeupRepository.findByMakeupId(existingMakeupId)).thenReturn(previouslySavedMakeup);
        makeupService.save(form);
        
        verify(mockMakeupRepository, atLeastOnce()).save(previouslySavedMakeup);
        assertEquals(expectedDateMadeup, previouslySavedMakeup.getDateMadeUp());
        assertEquals(expectedDateOfClass, previouslySavedMakeup.getDateOfClass());
        assertEquals(expectedMinutesMadeUp, previouslySavedMakeup.getMinutesMadeUp());
        assertEquals(expectedProjectDescription, previouslySavedMakeup.getProjectDescription());
    }
    
    
}
