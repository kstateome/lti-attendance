package edu.ksu.canvas.attendance.form;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class CourseConfigurationValidatorUTest {

    @Mock
    private Errors errors;

    private CourseConfigurationValidator courseConfigurationValidator;
    private CourseConfigurationForm courseConfigurationForm;


    @Before
    public void setup() throws ParseException {
        courseConfigurationValidator = new CourseConfigurationValidator();

        int validTotalClassMinutes = 2000;
        int validDefaultMinutesPerSession = 50;

        courseConfigurationForm = new CourseConfigurationForm();
        courseConfigurationForm.setTotalClassMinutes(validTotalClassMinutes);
        courseConfigurationForm.setDefaultMinutesPerSession(validDefaultMinutesPerSession);
        courseConfigurationForm.setGradingOn(false);

    }

    @Test
    public void validate_rejectMinutesPerSessionGreaterThanTotalMinutes() {
        courseConfigurationForm.setTotalClassMinutes(0);

        ArgumentCaptor<String> capturedField = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> capturedErrorCode = ArgumentCaptor.forClass(String.class);

        courseConfigurationValidator.validate(courseConfigurationForm,errors);

        verify(errors, times(1)).rejectValue(capturedField.capture(), capturedErrorCode.capture());
        assertEquals(1, capturedErrorCode.getAllValues().size());
        assertEquals("ExceedTotal.courseConfigurationForm.defaultMinutesPerSession", capturedErrorCode.getValue());
    }

    @Test
    public void validate_rejectWrongPoints() {
        courseConfigurationForm.setAssignmentName("Attendance Assignment");
        courseConfigurationForm.setGradingOn(true);
        courseConfigurationForm.setAssignmentPoints("100.0");
        courseConfigurationForm.setPresentPoints("100.0");
        courseConfigurationForm.setAbsentPoints("120.0");
        courseConfigurationForm.setExcusedPoints("0.0");
        courseConfigurationForm.setTardyPoints("0.0");

        ArgumentCaptor<String> capturedField = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> capturedErrorCode = ArgumentCaptor.forClass(String.class);

        courseConfigurationValidator.validate(courseConfigurationForm,errors);

        verify(errors, times(1)).rejectValue(capturedField.capture(), capturedErrorCode.capture());
        assertEquals(1, capturedErrorCode.getAllValues().size());
        assertEquals("Status percentages must be set between 0 and 100.", capturedErrorCode.getValue());
    }

    @Test
    public void validate_rejectNullName() {
        courseConfigurationForm.setAssignmentName(null);
        courseConfigurationForm.setGradingOn(true);
        courseConfigurationForm.setAssignmentPoints("100");
        courseConfigurationForm.setPresentPoints("100");
        courseConfigurationForm.setAbsentPoints("100");
        courseConfigurationForm.setExcusedPoints("0");
        courseConfigurationForm.setTardyPoints("0");

        ArgumentCaptor<String> capturedField = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> capturedErrorCode = ArgumentCaptor.forClass(String.class);

        courseConfigurationValidator.validate(courseConfigurationForm,errors);

        verify(errors, times(1)).rejectValue(capturedField.capture(), capturedErrorCode.capture());
        assertEquals(1, capturedErrorCode.getAllValues().size());
        assertEquals("Assignment Name is required.", capturedErrorCode.getValue());
    }

    @Test
    public void validate_rejectBadAssignmentPoints() {
        courseConfigurationForm.setAssignmentName("Attendance Assignment");
        courseConfigurationForm.setGradingOn(true);
        courseConfigurationForm.setAssignmentPoints("-100");
        courseConfigurationForm.setPresentPoints("100");
        courseConfigurationForm.setAbsentPoints("100");
        courseConfigurationForm.setExcusedPoints("0");
        courseConfigurationForm.setTardyPoints("0");

        ArgumentCaptor<String> capturedField = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> capturedErrorCode = ArgumentCaptor.forClass(String.class);

        courseConfigurationValidator.validate(courseConfigurationForm,errors);

        verify(errors, times(1)).rejectValue(capturedField.capture(), capturedErrorCode.capture());
        assertEquals(1, capturedErrorCode.getAllValues().size());
        assertEquals("Total Points is a required field and must be between 0 and 1000.", capturedErrorCode.getValue());
    }

    @Test
    public void validate_rejectNullPoints() {
        courseConfigurationForm.setAssignmentName("Attendance Assignment");
        courseConfigurationForm.setGradingOn(true);
        courseConfigurationForm.setAssignmentPoints("100");
        courseConfigurationForm.setPresentPoints("100");
        courseConfigurationForm.setAbsentPoints(null);
        courseConfigurationForm.setExcusedPoints("0");
        courseConfigurationForm.setTardyPoints("0");

        ArgumentCaptor<String> capturedField = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> capturedErrorCode = ArgumentCaptor.forClass(String.class);

        courseConfigurationValidator.validate(courseConfigurationForm,errors);

        verify(errors, times(1)).rejectValue(capturedField.capture(), capturedErrorCode.capture());
        assertEquals(1, capturedErrorCode.getAllValues().size());
        assertEquals("All status point fields are required.", capturedErrorCode.getValue());
    }

    @Test
    public void validate_rejectInvalidInput() {
        InputValidator inputValidator;

        inputValidator = new InputValidator();

        courseConfigurationForm.setAssignmentName("Attendance Assignment");
        courseConfigurationForm.setGradingOn(true);
        courseConfigurationForm.setAssignmentPoints("100");
        courseConfigurationForm.setPresentPoints("100");
        courseConfigurationForm.setAbsentPoints("abcd");
        courseConfigurationForm.setExcusedPoints("0");
        courseConfigurationForm.setTardyPoints("0");

        ArgumentCaptor<String> capturedField = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> capturedErrorCode = ArgumentCaptor.forClass(String.class);

        inputValidator.validate(courseConfigurationForm,errors);

        verify(errors, times(1)).rejectValue(capturedField.capture(), capturedErrorCode.capture());
        assertEquals(1, capturedErrorCode.getAllValues().size());
        assertEquals("The Absent field contains an incorrect value. Please enter a whole number between 0 and 100.", capturedErrorCode.getValue());
    }

}
