package edu.ksu.canvas.aviation.controller;

import java.io.IOException;

import javax.validation.Valid;

import edu.ksu.canvas.aviation.form.CourseConfigurationValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import edu.ksu.canvas.aviation.form.CourseConfigurationForm;
import edu.ksu.canvas.aviation.services.AviationCourseService;
import edu.ksu.canvas.aviation.services.CanvasSynchronizationService;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.canvas.error.OauthTokenRequiredException;
import edu.ksu.lti.model.LtiSession;


@Controller
@Scope("session")
@RequestMapping("/courseConfiguration")
public class CourseConfigurationController extends AviationBaseController {
    
    private static final Logger LOG = Logger.getLogger(CourseConfigurationController.class);
    
    @Autowired
    private CanvasSynchronizationService synchronizationService;
    
    @Autowired
    private AviationCourseService courseService;

    @Autowired
    private CourseConfigurationValidator validator;

    
    @RequestMapping()
    public ModelAndView classSetup() throws OauthTokenRequiredException, NoLtiSessionException, NumberFormatException, IOException {
        return classSetup(null, false);
    }

    @RequestMapping("/{sectionId}")
    public ModelAndView classSetup(@PathVariable String sectionId,
                                   @RequestParam(defaultValue = "false", value = "updateSuccessful") boolean successful) throws OauthTokenRequiredException, NoLtiSessionException, NumberFormatException, IOException {
        
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info("eid: "+ltiSession.getEid()+" is viewing course configuration...");
        
        ModelAndView page = new ModelAndView("courseConfiguration");

        CourseConfigurationForm courseConfigurationForm = new CourseConfigurationForm();
        SectionState sectionState = getSectionState(sectionId);
        courseService.loadCourseInfoIntoForm(courseConfigurationForm, sectionState.selectedSection.getCanvasCourseId());
        page.addObject("courseConfigurationForm", courseConfigurationForm);
        page.addObject("selectedSectionId", sectionState.selectedSection.getCanvasSectionId());
        page.addObject("updateSuccessful", successful);
        return page;
    }

    @RequestMapping(value = "/{sectionId}/save", params ="saveCourseConfiguration", method = RequestMethod.POST)
    public ModelAndView saveTotalClassMinutes(@PathVariable String sectionId, @ModelAttribute("courseConfigurationForm") @Valid CourseConfigurationForm classSetupForm, BindingResult bindingResult) throws IOException, NoLtiSessionException {
        validator.validate(classSetupForm, bindingResult);
        if (bindingResult.hasErrors()){
            ModelAndView page = new ModelAndView("/courseConfiguration");
            page.addObject("error", "Please correct user input and try saving again.");
            page.addObject("selectedSectionId", sectionId);
            return page;
        } else {
            LtiSession ltiSession = ltiLaunch.getLtiSession();
            LOG.info("eid: "+ltiSession.getEid() + " is saving course settings for " + ltiSession.getCanvasCourseId() + ", minutes: "
                 + classSetupForm.getTotalClassMinutes() + ", per session: " + classSetupForm.getDefaultMinutesPerSession());

            courseService.saveCourseMinutes(classSetupForm, ltiSession.getCanvasCourseId());
            return new ModelAndView("forward:/courseConfiguration/" + sectionId + "?updateSuccessful=true");
        }

    }
    
    @RequestMapping(value = "/{sectionId}/save", params = "synchronizeWithCanvas", method = RequestMethod.POST)
    public ModelAndView synchronizeWithCanvas(@PathVariable String sectionId) throws NoLtiSessionException, NumberFormatException, IOException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info("eid: "+ltiSession.getEid()+" is forcing a syncrhonization with Canvas for Canvas Course ID: "+ltiSession.getCanvasCourseId());
        
        synchronizationService.synchronizeWithCanvas(ltiSession, Long.valueOf(ltiSession.getCanvasCourseId()));
        
        return new ModelAndView("forward:/courseConfiguration/"+sectionId);
    }
    
}
