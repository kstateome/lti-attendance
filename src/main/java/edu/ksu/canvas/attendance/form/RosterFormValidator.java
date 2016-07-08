package edu.ksu.canvas.attendance.form;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.ksu.canvas.attendance.enums.Status;
import edu.ksu.canvas.attendance.model.AttendanceModel;
import edu.ksu.canvas.attendance.model.SectionModel;


@Component
public class RosterFormValidator implements Validator {

    public static final String MINIMUM_MINUTES_MISSED_ERROR_CODE = "Min.rosterForm.sectionModels.attendances.minutesMissed";
    public static final String SELECTIVELY_REQUIRED_MINUTES_MISSED_ERROR_CODE = "SelectivelyRequired.rosterForm.sectionModels.attendances.minutesMissed";

    
    @Override
    public boolean supports(Class<?> clazz) {
        return RosterForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RosterForm rosterForm = (RosterForm) target;

        int sectionIndex = 0;
        for (SectionModel sectionModel : rosterForm.getSectionModels()) {

            int attendanceIndex = 0;
            for (AttendanceModel attendance : sectionModel.getAttendances()) {
                String minutesMissedField = "sectionModels[" + sectionIndex + "].attendances[" + attendanceIndex + "].minutesMissed";
                if (attendance.getMinutesMissed() != null && attendance.getMinutesMissed() <= 0) {
                    errors.rejectValue(minutesMissedField, MINIMUM_MINUTES_MISSED_ERROR_CODE);
                }

                if (attendance.getStatus() == Status.TARDY && attendance.getMinutesMissed() == null && !rosterForm.getSimpleAttendance()) {
                    errors.rejectValue(minutesMissedField, SELECTIVELY_REQUIRED_MINUTES_MISSED_ERROR_CODE);
                }
                attendanceIndex++;
            }

            sectionIndex++;
        }
    }

}
