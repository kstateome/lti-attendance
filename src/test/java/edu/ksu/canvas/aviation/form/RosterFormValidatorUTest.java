package edu.ksu.canvas.aviation.form;

import edu.ksu.canvas.aviation.enums.Status;
import edu.ksu.canvas.aviation.model.AttendanceModel;
import edu.ksu.canvas.aviation.model.SectionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class RosterFormValidatorUTest {

    @Mock
    private Errors errors;
    
    private RosterFormValidator rosterFormValidator;
    private RosterForm rosterForm;
    private AttendanceModel attendanceModel;
    
    private String expectedMinutesMissedField = "sectionModels[0].attendances[0].minutesMissed";
    
    
    @Before
    public void setup() {
        rosterFormValidator = new RosterFormValidator();
        
        rosterForm = new RosterForm();
        List<SectionModel> sectionModels = new ArrayList<>();
        SectionModel sectionModel = new SectionModel();
        List<AttendanceModel> attendanceModels = new ArrayList<>();
        attendanceModel = new AttendanceModel();
        attendanceModels.add(attendanceModel);
        sectionModel.setAttendances(attendanceModels);
        sectionModels.add(sectionModel);
        rosterForm.setSectionModels(sectionModels);
        rosterForm.setSimpleAttendance(false);
    }

    @Test
    public void validate_rejectZeroMinutesMissed() {
        attendanceModel.setMinutesMissed(0);
        ArgumentCaptor<String> capturedField = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> capturedErrorCode = ArgumentCaptor.forClass(String.class);
        
        rosterFormValidator.validate(rosterForm, errors);
        
        verify(errors, times(1)).rejectValue(capturedField.capture(), capturedErrorCode.capture());
        assertEquals(expectedMinutesMissedField, capturedField.getValue());
        assertEquals(RosterFormValidator.MINIMUM_MINUTES_MISSED_ERROR_CODE, capturedErrorCode.getValue());
    }
    
    @Test
    public void validate_rejectNoMinutesWhenTardy() {
        attendanceModel.setStatus(Status.TARDY);
        ArgumentCaptor<String> capturedField = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> capturedErrorCode = ArgumentCaptor.forClass(String.class);
        
        rosterFormValidator.validate(rosterForm, errors);
        verify(errors, times(1)).rejectValue(capturedField.capture(), capturedErrorCode.capture());
        assertEquals(expectedMinutesMissedField, capturedField.getValue());
        assertEquals(RosterFormValidator.SELECTIVELY_REQUIRED_MINUTES_MISSED_ERROR_CODE, capturedErrorCode.getValue());
    }
    
}
