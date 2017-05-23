package edu.ksu.canvas.attendance.form;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CourseConfigurationValidator implements Validator {

    private static final Logger LOG = Logger.getLogger(CourseConfigurationValidator.class);

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

        if(courseConfigurationForm.getGradingOn()) {
            if (courseConfigurationForm.getAssignmentName() == null || courseConfigurationForm.getAssignmentName().length() <= 1 || courseConfigurationForm.getAssignmentName().trim().isEmpty()) {
                errors.rejectValue("assignmentName", "Assignment Name is required.");
            }

            if (courseConfigurationForm.getAssignmentPoints() == null || courseConfigurationForm.getAssignmentPoints() <= 0) {
                errors.rejectValue("assignmentPoints", "Total Points is a required field and must be greater than 0.");
            }

            if (courseConfigurationForm.getPresentPoints() == null || courseConfigurationForm.getTardyPoints() == null || courseConfigurationForm.getExcusedPoints() == null
                    || courseConfigurationForm.getAbsentPoints() == null) {
                errors.rejectValue("presentPoints", "All status point fields are required.");
                return;
            }

            if ((courseConfigurationForm.getPresentPoints() > 100 || courseConfigurationForm.getPresentPoints() < 0) || (courseConfigurationForm.getTardyPoints() > 100 || courseConfigurationForm.getTardyPoints() < 0)
                    || (courseConfigurationForm.getExcusedPoints() > 100 || courseConfigurationForm.getExcusedPoints() < 0) || (courseConfigurationForm.getAbsentPoints() > 100 || courseConfigurationForm.getAbsentPoints() < 0)) {
                errors.rejectValue("presentPoints", "Point values must be set between 0 and 100.");
            }
        }
    }

}
