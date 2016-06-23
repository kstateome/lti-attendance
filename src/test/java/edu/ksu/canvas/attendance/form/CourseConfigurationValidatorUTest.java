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

}
