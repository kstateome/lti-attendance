package edu.ksu.canvas.aviation.form;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.ksu.canvas.aviation.model.MakeupModel;


@Component
public class MakeupValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return MakeupForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MakeupForm makeupForm = (MakeupForm) target;
        
        int makeupIndex = 0;
        for (MakeupModel makeupModel : makeupForm.getEntries()) {
            
            if(makeupModel.isToBeDeletedFlag()) {
                continue;
            }
            
            if(!errors.hasFieldErrors("entries["+makeupIndex+"].minutesMadeUp")) {
                if(makeupModel.getMinutesMadeUp() == null) {
                    errors.rejectValue("entries["+makeupIndex+"].minutesMadeUp", "Required.makeupForm.entries.minutesMadeUp");
                } else if(makeupModel.getMinutesMadeUp() < 1) {
                    errors.rejectValue("entries["+makeupIndex+"].minutesMadeUp", "Min.makeupForm.entries.minutesMadeUp");
                }
            }
            
            if(makeupModel.getDateMadeUp() == null) {
                errors.rejectValue("entries["+makeupIndex+"].dateMadeUp", "Required.makeupForm.entries.dateMadeUp");
            }
            
            if(makeupModel.getDateOfClass() == null) {
                errors.rejectValue("entries["+makeupIndex+"].dateOfClass", "Required.makeupForm.entries.dateOfClass");
            }
            
        }
    }

}
