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
                    errors.rejectValue("assignmentPoints", "The Total Points field contains an incorrect value. Please enter a valid number with no decimals or fractions.");
                }
                if (!isInputValid(presentPoints)) {
                    errors.rejectValue("presentPoints", "The Present field contains an incorrect value. Please enter a whole number between 0 and 100.");
                }
                if (!isInputValid(tardyPoints)) {
                    errors.rejectValue("tardyPoints", "The Tardy field contains an incorrect value. Please enter a whole number between 0 and 100.");
                }
                if (!isInputValid(absentPoints)) {
                    errors.rejectValue("absentPoints", "The Absent field contains an incorrect value. Please enter a whole number between 0 and 100.");
                }
                if (!isInputValid(excusedPoints)) {
                    errors.rejectValue("excusedPoints", "The Excused field contains an incorrect value. Please enter a whole number between 0 and 100.");
                }
            }
        }
    }
    private boolean isInputValid(String val) {
        if (val != null){
            return val.matches("[0-9.]+") && !val.contains(".") && !val.contains("/");
        }
        else{
            return false;
        }
    }
}
