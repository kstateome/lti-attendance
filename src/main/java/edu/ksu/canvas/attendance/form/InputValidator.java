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
            boolean typeValid = (assignmentPoints == null || assignmentPoints.matches("[0-9.]+"))
                    && (presentPoints == null || presentPoints.matches("[0-9.]+"))
                    && (tardyPoints == null || tardyPoints.matches("[0-9.]+"))
                    && (absentPoints == null || absentPoints.matches("[0-9.]+"))
                    && (excusedPoints == null || excusedPoints.matches("[0-9.]+"));

            if(!typeValid){
                if(assignmentPoints != null && !assignmentPoints.matches("[0-9.]+")) {
                    errors.rejectValue("assignmentPoints", "The Total Points field contained an incorrect value. Please enter a valid number.");
                } else if (presentPoints != null && !presentPoints.matches("[0-9.]+")) {
                    errors.rejectValue("presentPoints", "The Present field contained an incorrect value. Please enter a valid number between 0 and 100.");
                } else if (tardyPoints != null && !tardyPoints.matches("[0-9.]+")) {
                    errors.rejectValue("tardyPoints", "The Tardy field contained an incorrect value. Please enter a valid number between 0 and 100.");
                } else if (absentPoints != null && !absentPoints.matches("[0-9.]+")) {
                    errors.rejectValue("absentPoints", "The Absent field contained an incorrect value. Please enter a valid number between 0 and 100.");
                } else if (excusedPoints != null && !excusedPoints.matches("[0-9.]+")) {
                    errors.rejectValue("excusedPoints", "The Excused field contained an incorrect value. Please enter a valid number between 0 and 100.");
              }
            }
        }
    }
}
