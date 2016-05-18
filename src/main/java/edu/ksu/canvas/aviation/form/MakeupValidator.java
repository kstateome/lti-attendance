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
                makeupIndex++;
                continue;
            }
            
            if(!errors.hasFieldErrors("entries["+makeupIndex+"].minutesMadeUp") && makeupModel.getMinutesMadeUp() != null && makeupModel.getMinutesMadeUp() < 1) {
                errors.rejectValue("entries["+makeupIndex+"].minutesMadeUp", "Min.makeupForm.entries.minutesMadeUp");
            }
            
            if(makeupModel.getDateOfClass() == null) {
                errors.rejectValue("entries["+makeupIndex+"].dateOfClass", "Required.makeupForm.entries.dateOfClass");
            }
            
            if(makeupModel.getProjectDescription() != null && makeupModel.getProjectDescription().length() >= 255) {
                errors.rejectValue("entries["+makeupIndex+"].projectDescription", "Max.makeupForm.entries.projectDescription");
            }
            
            makeupIndex++;
        }
    }

}
