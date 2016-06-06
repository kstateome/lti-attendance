package edu.ksu.canvas.aviation.form;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CourseConfigurationValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return CourseConfigurationForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CourseConfigurationForm courseConfigurationForm = (CourseConfigurationForm) target;
        if (courseConfigurationForm.getTotalClassMinutes() < courseConfigurationForm.getDefaultMinutesPerSession() && !errors.hasFieldErrors("totalClassMinutes")) {
            errors.rejectValue("defaultMinutesPerSession", "ExceedTotal.courseConfigurationForm.defaultMinutesPerSession");
        }
    }

}
