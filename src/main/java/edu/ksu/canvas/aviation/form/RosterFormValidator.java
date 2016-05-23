package edu.ksu.canvas.aviation.form;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.ksu.canvas.aviation.enums.Status;
import edu.ksu.canvas.aviation.model.AttendanceModel;
import edu.ksu.canvas.aviation.model.SectionModel;


@Component
public class RosterFormValidator implements Validator {


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
                    errors.rejectValue(minutesMissedField, "Min.rosterForm.sectionModels.attendances.minutesMissed");
                }

                if (attendance.getStatus() == Status.TARDY && attendance.getMinutesMissed() == null) {
                    errors.rejectValue(minutesMissedField, "SelectivelyRequired.rosterForm.sectionModels.attendances.minutesMissed");
                }
                attendanceIndex++;
            }

            sectionIndex++;
        }
    }

}
