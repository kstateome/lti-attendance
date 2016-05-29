package edu.ksu.canvas.aviation.form;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import edu.ksu.canvas.aviation.model.MakeupModel;

import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class MakeupValidatorUTest {

    @Mock
    private Errors errors;
    
    private MakeupValidator makeupValidator;
    private MakeupForm makeupForm;
    private MakeupModel makeupModel;
    
    
    @Before
    public void setup() throws ParseException {
        makeupValidator = new MakeupValidator();
        
        int validMinutesMadeUp = 10;
        Date validDateOfClass = new SimpleDateFormat("MM/dd/yyyy").parse("05/01/2016");
        String validProjectDescription = "something worthwhile";
        
        makeupForm = new MakeupForm();
        List<MakeupModel> makeupModels = new ArrayList<>();
        makeupModel = new MakeupModel();
        makeupModel.setMinutesMadeUp(validMinutesMadeUp);
        makeupModel.setDateOfClass(validDateOfClass);
        makeupModel.setProjectDescription(validProjectDescription);
        
        makeupModels.add(makeupModel);
        makeupForm.setEntries(makeupModels);
    }
    
    @Test
    public void valiate_rejectZeroMinutesMadeup() {
        String expectedMinutesMadeupField = "entries[0].minutesMadeUp";
        makeupModel.setMinutesMadeUp(0);
        ArgumentCaptor<String> capturedField = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> capturedErrorCode = ArgumentCaptor.forClass(String.class);
        
        makeupValidator.validate(makeupForm, errors);
        verify(errors, times(1)).rejectValue(capturedField.capture(), capturedErrorCode.capture());
        assertEquals(expectedMinutesMadeupField, capturedField.getValue());
        assertEquals(MakeupValidator.MINIMUM_MINUTES_MADEUP_ERROR_CODE, capturedErrorCode.getValue());
    }
    
    @Test
    public void validate_rejectNoDateOfClass() {
        String expectedDateOfClassField = "entries[0].dateOfClass";
        makeupModel.setDateOfClass(null);
        ArgumentCaptor<String> capturedField = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> capturedErrorCode = ArgumentCaptor.forClass(String.class);
        
        makeupValidator.validate(makeupForm, errors);
        verify(errors, times(1)).rejectValue(capturedField.capture(), capturedErrorCode.capture());
        assertEquals(expectedDateOfClassField, capturedField.getValue());
        assertEquals(MakeupValidator.REQUIRED_DATE_OF_CLASS_ERROR_CODE, capturedErrorCode.getValue());
    }
    
    @Test
    public void validate_rejectTooLargeProjectDescription() {
        String expectedProjectDescriptionField = "entries[0].projectDescription";
        makeupModel.setProjectDescription("sdfjlkdsfjklfdsjsdfsdjlksdfjlksdfjlksdfjlksdfjlksdfjlksdfjlksdfjlksfdjlksfdjlksdfjlskdfjsdlkfsjldfkjsdlkjdsflkjlsdkfdsfjklsdfjkldfsjlkfsdjlksfdjkljklsdkldfsjklfdsjklfsdjlksfjflsdkjfdsljlsdjkdsfjklsdjlkfsdjlkfsdjlkfdsjlsfdjsdfjsdfkjsdfjlsdfjklsdjlsdjkjslkafkdjfadjfkla;dsfjkl;asdfjk;lasdfjkl;asdfjkl;adsfjkl;asdfjkl;adsfjkl;");
        ArgumentCaptor<String> capturedField = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> capturedErrorCode = ArgumentCaptor.forClass(String.class);
        
        makeupValidator.validate(makeupForm, errors);
        verify(errors, times(1)).rejectValue(capturedField.capture(), capturedErrorCode.capture());
        assertEquals(expectedProjectDescriptionField, capturedField.getValue());
        assertEquals(MakeupValidator.MAXIMUM_PROJECT_DESC_ERROR_CODE, capturedErrorCode.getValue());        
    }
    
    @Test
    public void validate_ignoreInvalidMakeupMarkedForDeletion() {
        makeupModel.setDateOfClass(null);
        makeupModel.setToBeDeletedFlag(true);
        
        makeupValidator.validate(makeupForm, errors);
        verify(errors, times(0)).rejectValue(any(String.class), any(String.class));
    }
    
}
