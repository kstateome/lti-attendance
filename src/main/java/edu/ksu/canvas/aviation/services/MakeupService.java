package edu.ksu.canvas.aviation.services;

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
    
    
    public void delete(String makeupId) {
        makeupRepository.delete(Long.valueOf(makeupId));
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
