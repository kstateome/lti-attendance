package edu.ksu.canvas.aviation.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.entity.Makeup;
import edu.ksu.canvas.aviation.form.MakeupForm;
import edu.ksu.canvas.aviation.model.MakeupModel;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;
import edu.ksu.canvas.aviation.repository.MakeupRepository;


@Component
public class MakeupService {

    
    @Autowired
    private MakeupRepository makeupRepository;
    
    @Autowired
    private AviationStudentRepository aviationStudentRepository;
    
    
    public MakeupForm createMakeupForm(long studentId, long sectionId, boolean addEmptyEntry) {
        AviationStudent student = aviationStudentRepository.findByStudentId(new Long(studentId));
        List<Makeup> makeups = makeupRepository.findByAviationStudentOrderByDateOfClassAsc(student);
        if(addEmptyEntry) {
            makeups.add(new Makeup());
        }
        
        MakeupForm makeupForm = new MakeupForm();
        makeupForm.setEntriesFromMakeEntities(makeups);
        makeupForm.setSectionId(Long.valueOf(sectionId));
        makeupForm.setStudentId(Long.valueOf(studentId));
        
        return makeupForm;
    }
    
    public void save(MakeupForm form) {
        deleteFlaggedMakeups(form);
        createOrUpdate(form);
    }
    
    private void createOrUpdate(MakeupForm form) {
        if(form.getEntries() == null) {
            return;
        }
        
        for(MakeupModel makeupModel: form.getEntries()) {
            if(makeupModel.isToBeDeletedFlag()) {
                continue;
            }
            if(makeupModel.getMakeupId() == null) {
                AviationStudent student = aviationStudentRepository.findByStudentId(form.getStudentId());
                
                Makeup makeup = new Makeup();
                makeup.setDateOfClass(makeupModel.getDateOfClass());
                makeup.setDateMadeUp(makeupModel.getDateMadeUp());
                makeup.setProjectDescription(makeupModel.getProjectDescription());
                makeup.setMinutesMadeUp(makeupModel.getMinutesMadeUp());
                makeup.setAviationStudent(student);
                
                makeupRepository.save(makeup);
            } else {
                Makeup tracker = makeupRepository.findByMakeupId(makeupModel.getMakeupId());
                tracker.setDateMadeUp(makeupModel.getDateMadeUp());
                tracker.setDateOfClass(makeupModel.getDateOfClass());
                tracker.setProjectDescription(makeupModel.getProjectDescription());
                tracker.setMinutesMadeUp(makeupModel.getMinutesMadeUp());
                
                makeupRepository.save(tracker);
            }

        }
    }

    private void deleteFlaggedMakeups(MakeupForm form) {
        if(form.getEntries() == null){
            return;
        }
        for(MakeupModel model : form.getEntries()){
            if(model.getMakeupId() != null && model.isToBeDeletedFlag()) {
                makeupRepository.delete(model.getMakeupId());
            }
        }

    }
    
}
