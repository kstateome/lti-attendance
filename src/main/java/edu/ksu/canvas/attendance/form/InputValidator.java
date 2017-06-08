package edu.ksu.canvas.attendance.form;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class InputValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CourseConfigurationForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        CourseConfigurationForm classSetupForm = (CourseConfigurationForm) target;

        String assignmentPoints = classSetupForm.getAssignmentPoints();
        String presentPoints = classSetupForm.getPresentPoints();
        String tardyPoints = classSetupForm.getTardyPoints();
        String absentPoints = classSetupForm.getAbsentPoints();
        String excusedPoints = classSetupForm.getExcusedPoints();

        if (classSetupForm.getGradingOn()){
            boolean typeValid = isInputValid(assignmentPoints) && isInputValid(presentPoints) && isInputValid(tardyPoints) && isInputValid(absentPoints) && isInputValid(excusedPoints);

            if(!typeValid){
                if(!isInputValid(assignmentPoints)) {
                    errors.rejectValue("assignmentPoints", "The Total Points field contained an incorrect value. Please enter a valid number.");
                } else if (!isInputValid(presentPoints)) {
                    errors.rejectValue("presentPoints", "The Present field contained an incorrect value. Please enter a valid number between 0 and 100.");
                } else if (!isInputValid(tardyPoints)) {
                    errors.rejectValue("tardyPoints", "The Tardy field contained an incorrect value. Please enter a valid number between 0 and 100.");
                } else if (!isInputValid(absentPoints)) {
                    errors.rejectValue("absentPoints", "The Absent field contained an incorrect value. Please enter a valid number between 0 and 100.");
                } else if (!isInputValid(excusedPoints)) {
                    errors.rejectValue("excusedPoints", "The Excused field contained an incorrect value. Please enter a valid number between 0 and 100.");
              }
            }
        }
    }
    private boolean isInputValid(String val) {
        return val == null || val.matches("[0-9.]+");
    }
}
