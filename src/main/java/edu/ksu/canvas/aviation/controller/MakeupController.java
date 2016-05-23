package edu.ksu.canvas.aviation.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.entity.Makeup;
import edu.ksu.canvas.aviation.form.MakeupForm;
import edu.ksu.canvas.aviation.form.MakeupValidator;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;
import edu.ksu.canvas.aviation.repository.MakeupRepository;
import edu.ksu.canvas.aviation.services.MakeupService;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.lti.model.LtiSession;


@Controller
@Scope("session")
@RequestMapping("/studentMakeup")
public class MakeupController extends AviationBaseController {
    
    private static final Logger LOG = Logger.getLogger(MakeupController.class);
    
    @Autowired
    private MakeupService makeupService;
    
    @Autowired
    private AviationStudentRepository studentRepository;
    
    @Autowired
    private MakeupRepository makeupRepository;
    
    @Autowired
    private MakeupValidator validator;
    
    
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    @RequestMapping("/{sectionId}/{studentId}")
    public ModelAndView studentMakeup(@PathVariable String sectionId, @PathVariable String studentId) throws NoLtiSessionException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info("eid: "+ltiSession.getEid()+" is viewing makeup data");
        
        return studentMakeup(sectionId, studentId, false);
    }

    private ModelAndView studentMakeup(String sectionId, String studentId, boolean addEmptyEntry) {
        AviationStudent student = studentRepository.findByStudentId(new Long(studentId));
        List<Makeup> makeups = makeupRepository.findByAviationStudentOrderByDateOfClassAsc(student);
        if(addEmptyEntry) {
            makeups.add(new Makeup());
        }
        
        MakeupForm makeupForm = new MakeupForm();
        makeupForm.setEntriesFromMakeEntities(makeups);
        makeupForm.setSectionId(Long.valueOf(sectionId));
        makeupForm.setStudentId(Long.valueOf(studentId));
        
        ModelAndView page = new ModelAndView("studentMakeup");
        page.addObject("sectionId", sectionId);
        page.addObject("student", student);
        page.addObject("makeupForm", makeupForm);
        
        return page;
    }    
    
    @RequestMapping(value = "/save", params = "saveMakeup", method = RequestMethod.POST)
    public ModelAndView saveMakeup(@ModelAttribute MakeupForm makeupForm, BindingResult bindingResult) throws NoLtiSessionException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info("eid: " + ltiSession.getEid() + " is saving makeup data.");
        validator.validate(makeupForm, bindingResult);

        boolean allUnsavedAndToBeDeleted = false;
        if(makeupForm.getEntries()!= null){
            long count = makeupForm.getEntries().stream()
                    .filter(entry -> (entry.getMakeupId() == null && entry.isToBeDeletedFlag()))
                    .count();
            allUnsavedAndToBeDeleted = makeupForm.getEntries().size() == count;
        }

        if (bindingResult.hasErrors()) {
            LOG.info("There were errors saving the Makeup form"+ bindingResult.getAllErrors());
            String errorMessage = "Please correct user input and try saving again.";
            
            ModelAndView page = new ModelAndView("studentMakeup");
            AviationStudent student = studentRepository.findByStudentId(makeupForm.getStudentId());
            page.addObject("sectionId", String.valueOf(makeupForm.getSectionId()));
            page.addObject("student", student);
            page.addObject("makeupForm", makeupForm);
            page.addObject("error", errorMessage);
            
            return page;
        } else {
            makeupService.save(makeupForm);
        }
        
        ModelAndView page = studentMakeup(String.valueOf(makeupForm.getSectionId()), String.valueOf(makeupForm.getStudentId()), false);
        if(makeupForm.getEntries() == null || allUnsavedAndToBeDeleted){
            page.addObject("nullEntry", true);
        } else {
            page.addObject("updateSuccessful", true);
        }
        return page; 
                
    }

}
