package edu.ksu.canvas.attendance.services;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ksu.canvas.attendance.entity.AviationStudent;
import edu.ksu.canvas.attendance.entity.Makeup;
import edu.ksu.canvas.attendance.form.MakeupForm;
import edu.ksu.canvas.attendance.model.MakeupModel;
import edu.ksu.canvas.attendance.repository.AviationStudentRepository;
import edu.ksu.canvas.attendance.repository.MakeupRepository;


@Component
public class MakeupService {


    @Autowired
    private MakeupRepository makeupRepository;

    @Autowired
    private AviationStudentRepository aviationStudentRepository;


    /**
     * @throws IllegalArgumentException when a student cannot be found in the database for the given studentId
     */
    public MakeupForm createMakeupForm(long studentId, long sectionId, boolean addEmptyEntry) {
        AviationStudent student = aviationStudentRepository.findByStudentId(new Long(studentId));
        if(student == null) {
            RuntimeException e = new IllegalArgumentException("student does not exist in the database");
            throw new ContextedRuntimeException(e).addContextValue("studentId", studentId);
        }
        
        List<Makeup> makeups = makeupRepository.findByAviationStudentOrderByDateOfClassAsc(student);
        if (addEmptyEntry) {
            makeups.add(new Makeup());
        }

        MakeupForm makeupForm = new MakeupForm();
        makeupForm.setEntriesFromMakeEntities(makeups);
        makeupForm.setSectionId(sectionId);
        makeupForm.setStudentId(studentId);

        return makeupForm;
    }

    /**
     * @throws NullPointerException when form is null
     */
    public void save(MakeupForm form) {
        Validate.notNull(form, "The form parameter must not be null");
        
        deleteFlaggedMakeups(form);
        createOrUpdate(form);
    }

    private void createOrUpdate(MakeupForm form) {
        if (form.getEntries() == null) {
            return;
        }

        for (MakeupModel makeupModel : form.getEntries()) {
            if (makeupModel.isToBeDeletedFlag()) {
                continue;
            }
            if (makeupModel.getMakeupId() == null) {
                AviationStudent student = aviationStudentRepository.findByStudentId(form.getStudentId());

                Makeup makeup = new Makeup();
                makeup.setDateOfClass(makeupModel.getDateOfClass());
                makeup.setDateMadeUp(makeupModel.getDateMadeUp());
                makeup.setProjectDescription(makeupModel.getProjectDescription());
                makeup.setMinutesMadeUp(makeupModel.getMinutesMadeUp());
                makeup.setAviationStudent(student);

                makeupRepository.save(makeup);
            } else {
                Makeup makeup = makeupRepository.findByMakeupId(makeupModel.getMakeupId());
                makeup.setDateMadeUp(makeupModel.getDateMadeUp());
                makeup.setDateOfClass(makeupModel.getDateOfClass());
                makeup.setProjectDescription(makeupModel.getProjectDescription());
                makeup.setMinutesMadeUp(makeupModel.getMinutesMadeUp());

                makeupRepository.save(makeup);
            }

        }
    }

    private void deleteFlaggedMakeups(MakeupForm form) {
        Validate.notNull(form, "The form parameter must not be null");

        if (form.getEntries() == null) {
            return;
        }
        for (MakeupModel model : form.getEntries()) {
            if (model.getMakeupId() != null && model.isToBeDeletedFlag()) {
                makeupRepository.delete(model.getMakeupId());
            }
        }

    }

}
