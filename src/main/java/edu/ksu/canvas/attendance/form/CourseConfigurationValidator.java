package edu.ksu.canvas.attendance.form;

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

        String assignmentPoints = courseConfigurationForm.getAssignmentPoints();
        String presentPoints = courseConfigurationForm.getPresentPoints();
        String tardyPoints = courseConfigurationForm.getTardyPoints();
        String absentPoints = courseConfigurationForm.getAbsentPoints();
        String excusedPoints = courseConfigurationForm.getExcusedPoints();


        if (courseConfigurationForm.getTotalClassMinutes() < courseConfigurationForm.getDefaultMinutesPerSession() && !errors.hasFieldErrors("totalClassMinutes")) {
            errors.rejectValue("defaultMinutesPerSession", "ExceedTotal.courseConfigurationForm.defaultMinutesPerSession");
        }

        if(courseConfigurationForm.getGradingOn()) {
            if (courseConfigurationForm.getAssignmentName() == null || courseConfigurationForm.getAssignmentName().length() <= 1 || courseConfigurationForm.getAssignmentName().trim().isEmpty()) {
                errors.rejectValue("assignmentName", "Assignment Name is required.");
                return;
            }

            if (assignmentPoints == null || (Double.parseDouble(assignmentPoints) < 0)) {
                errors.rejectValue("assignmentPoints", "Total Points is a required field and must be greater than 0.");
                return;
            }

            if (presentPoints == null || tardyPoints == null || absentPoints == null || excusedPoints == null) {
                errors.rejectValue("presentPoints", "All status point fields are required.");
                return;
            }

            if (isValueOutOfBounds(presentPoints) || isValueOutOfBounds(tardyPoints) || isValueOutOfBounds(absentPoints) || isValueOutOfBounds(excusedPoints)) {
                errors.rejectValue("presentPoints", "Status percentages must be set between 0 and 100.");
            }
        }
    }
    private boolean isValueOutOfBounds(String value) {
        Double val = Double.parseDouble(value);
        return val > 100 || val < 0;
    }

}
