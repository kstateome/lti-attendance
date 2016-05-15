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
import edu.ksu.canvas.aviation.entity.MakeupTracker;
import edu.ksu.canvas.aviation.form.MakeupTrackerForm;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;
import edu.ksu.canvas.aviation.repository.MakeupTrackerRepository;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.lti.model.LtiSession;


@Controller
@Scope("session")
@RequestMapping("/studentMakeup")
public class MakeupController extends AviationBaseController {
    
    private static final Logger LOG = Logger.getLogger(MakeupController.class);
    
    @Autowired
    private AviationStudentRepository studentRepository;
    
    @Autowired
    private MakeupTrackerRepository makeupTrackerRepository;
    
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
    

    @RequestMapping("/{sectionId}/{studentId}")
    public ModelAndView studentMakeup(@PathVariable String sectionId, @PathVariable String studentId) {
        return studentMakeup(sectionId, studentId, false);
    }

    private ModelAndView studentMakeup(String sectionId, String studentId, boolean addEmptyEntry) {
        AviationStudent student = studentRepository.findByStudentId(new Long(studentId));
        List<MakeupTracker> makeupTrackers = makeupTrackerRepository.findByAviationStudent(student);
        if(addEmptyEntry) {
            makeupTrackers.add(new MakeupTracker());
        }
        
        MakeupTrackerForm makeupTrackerForm = new MakeupTrackerForm();
        makeupTrackerForm.setEntries(makeupTrackers);
        makeupTrackerForm.setSectionId(Long.valueOf(sectionId));
        makeupTrackerForm.setStudentId(Long.valueOf(studentId));
        
        ModelAndView page = new ModelAndView("studentMakeup");
        page.addObject("sectionId", sectionId);
        page.addObject("student", student);
        page.addObject("makeupTrackerForm", makeupTrackerForm);
        
        return page;
    }
    
    @RequestMapping(value = "/deleteMakeup/{sectionId}/{studentId}/{makeupTrackerId}")
    public ModelAndView deleteMakeup(@PathVariable String sectionId, @PathVariable String studentId, @PathVariable String makeupTrackerId) throws NoLtiSessionException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info("Attempting to delete makeup data... User: " + ltiSession.getEid());
        
        persistenceService.deleteMakeup(makeupTrackerId);
        return studentMakeup(sectionId, studentId);
    }
    
    
    @RequestMapping(value = "/save", params = "saveMakeup", method = RequestMethod.POST)
    public ModelAndView saveMakeup(@ModelAttribute MakeupTrackerForm makeupTrackerForm, BindingResult bindingResult) throws NoLtiSessionException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info("Attempting to save makeup data... User: " + ltiSession.getEid());
        
        //FIXME: This is not appropriate!
        if (bindingResult.hasErrors()) {
            LOG.info("There were errors saving the Makeup form"+ bindingResult.getAllErrors());
            String errorMessage = "Invalid user input...";
            
            ModelAndView page = new ModelAndView("studentMakeup");
            AviationStudent student = studentRepository.findByStudentId(makeupTrackerForm.getStudentId());
            page.addObject("sectionId", String.valueOf(makeupTrackerForm.getSectionId()));
            page.addObject("student", student);
            page.addObject("makeupTrackerForm", makeupTrackerForm);
            page.addObject("error", errorMessage);
            
            return page;
        } else {
            persistenceService.saveMakeups(makeupTrackerForm);    
        }
        
        return studentMakeup(String.valueOf(makeupTrackerForm.getSectionId()), String.valueOf(makeupTrackerForm.getStudentId()), false);
    }
    
    @RequestMapping(value = "/save", params = "addMakeup", method = RequestMethod.POST)
    public ModelAndView addMakeup(@ModelAttribute MakeupTrackerForm makeupTrackerForm, BindingResult bindingResult) throws NoLtiSessionException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info("Attempting to save makeup data and add new entry... User: " + ltiSession.getEid());
        
        //FIXME: This is not appropriate!
        if (bindingResult.hasErrors()){
            LOG.info("There were errors saving the Makeup form"+ bindingResult.getAllErrors());
            String errorMessage = "Invalid user input...";
            
            ModelAndView page = new ModelAndView("studentMakeup");
            AviationStudent student = studentRepository.findByStudentId(makeupTrackerForm.getStudentId());
            page.addObject("sectionId", String.valueOf(makeupTrackerForm.getSectionId()));
            page.addObject("student", student);
            page.addObject("makeupTrackerForm", makeupTrackerForm);
            page.addObject("error", errorMessage);
            
            return page;
        } else {
            persistenceService.saveMakeups(makeupTrackerForm);
            makeupTrackerForm.getEntries().add(new MakeupTracker());
        }
        
        return studentMakeup(String.valueOf(makeupTrackerForm.getSectionId()), String.valueOf(makeupTrackerForm.getStudentId()), true);
    }
    
}
