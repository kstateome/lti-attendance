package edu.ksu.canvas.attendance.form;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.ksu.canvas.attendance.model.MakeupModel;


@Component
public class MakeupValidator implements Validator {
    
    public static final String MINIMUM_MINUTES_MADEUP_ERROR_CODE = "Min.makeupForm.entries.minutesMadeUp";
    public static final String REQUIRED_DATE_OF_CLASS_ERROR_CODE = "Required.makeupForm.entries.dateOfClass";
    public static final String MAXIMUM_PROJECT_DESC_ERROR_CODE = "Max.makeupForm.entries.projectDescription";


    @Override
    public boolean supports(Class<?> clazz) {
        return MakeupForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MakeupForm makeupForm = (MakeupForm) target;

        if (makeupForm.getEntries() == null) {
            return;
        }
        int makeupIndex = 0;
        for (MakeupModel makeupModel : makeupForm.getEntries()) {

            if (makeupModel.isToBeDeletedFlag()) {
                makeupIndex++;
                continue;
            }

            validateMinutesMadeUp(errors, makeupIndex, makeupModel);
            validateDateOfClass(errors, makeupIndex, makeupModel);
            validateProjectDescription(errors, makeupIndex, makeupModel);

            makeupIndex++;
        }
    }

    private void validateProjectDescription(Errors errors, int makeupIndex, MakeupModel makeupModel) {
        if (makeupModel.getProjectDescription() != null && makeupModel.getProjectDescription().length() >= 255) {
            errors.rejectValue("entries[" + makeupIndex + "].projectDescription", MAXIMUM_PROJECT_DESC_ERROR_CODE);
        }
    }

    private void validateDateOfClass(Errors errors, int makeupIndex, MakeupModel makeupModel) {
        if (makeupModel.getDateOfClass() == null) {
            errors.rejectValue("entries[" + makeupIndex + "].dateOfClass", REQUIRED_DATE_OF_CLASS_ERROR_CODE);
        }
    }

    private void validateMinutesMadeUp(Errors errors, int makeupIndex, MakeupModel makeupModel) {
        if (!errors.hasFieldErrors("entries[" + makeupIndex + "].minutesMadeUp") && 
            makeupModel.getMinutesMadeUp() != null && 
            makeupModel.getMinutesMadeUp() < 1) {

            errors.rejectValue("entries[" + makeupIndex + "].minutesMadeUp", MINIMUM_MINUTES_MADEUP_ERROR_CODE);
        }
    }

}
