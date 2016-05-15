package edu.ksu.canvas.aviation.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.ksu.canvas.aviation.form.CourseConfigurationForm;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.canvas.error.OauthTokenRequiredException;
import edu.ksu.lti.model.LtiSession;


@Controller
@Scope("session")
@RequestMapping("/courseConfiguration")
public class CourseConfigurationController extends AviationBaseController {
    
    private static final Logger LOG = Logger.getLogger(CourseConfigurationController.class);

    
    @RequestMapping()
    public ModelAndView classSetup() throws OauthTokenRequiredException, NoLtiSessionException, NumberFormatException, IOException {
        return classSetup(null);
    }

    @RequestMapping("/{sectionId}")
    public ModelAndView classSetup(@PathVariable String sectionId) throws OauthTokenRequiredException, NoLtiSessionException, NumberFormatException, IOException {
        ModelAndView page = new ModelAndView("courseConfiguration");

        CourseConfigurationForm courseConfigurationForm = new CourseConfigurationForm();
        SectionState sectionState = getSectionState(sectionId);
        persistenceService.loadCourseInfoIntoForm(courseConfigurationForm, sectionState.selectedSection.getCanvasCourseId());
        page.addObject("courseConfigurationForm", courseConfigurationForm);
        page.addObject("selectedSectionId", sectionState.selectedSection.getCanvasSectionId());
        
        return page;
    }

    @RequestMapping(value = "/save", params ="saveCourseConfiguration", method = RequestMethod.POST)
    public ModelAndView saveTotalClassMinutes(@ModelAttribute("courseConfigurationForm") @Valid CourseConfigurationForm classSetupForm, BindingResult bindingResult) throws IOException, NoLtiSessionException {
        ModelAndView page = new ModelAndView("forward:/courseConfiguration");
        if (bindingResult.hasErrors()){
            LOG.info("There were errors submitting the minutes form"+ bindingResult.getAllErrors());
            page.addObject("error", "Invalid input for minutes, please enter valid minutes");
        }
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info(ltiSession.getEid() + " saving course settings for " + ltiSession.getCanvasCourseId() + ", minutes: "
                 + classSetupForm.getTotalClassMinutes() + ", per session: " + classSetupForm.getDefaultMinutesPerSession());

        persistenceService.saveCourseMinutes(classSetupForm, ltiSession.getCanvasCourseId());
        return page;
    }
    
}
