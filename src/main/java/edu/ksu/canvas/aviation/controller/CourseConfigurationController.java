package edu.ksu.canvas.aviation.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
        return classSetup(null, false);
    }

    @RequestMapping("/{sectionId}")
    public ModelAndView classSetup(@PathVariable String sectionId,
                                   @RequestParam(defaultValue = "false", value = "updateSuccessful") boolean successful) throws OauthTokenRequiredException, NoLtiSessionException, NumberFormatException, IOException {
        ModelAndView page = new ModelAndView("courseConfiguration");

        CourseConfigurationForm courseConfigurationForm = new CourseConfigurationForm();
        SectionState sectionState = getSectionState(sectionId);
        persistenceService.loadCourseInfoIntoForm(courseConfigurationForm, sectionState.selectedSection.getCanvasCourseId());
        page.addObject("courseConfigurationForm", courseConfigurationForm);
        page.addObject("selectedSectionId", sectionState.selectedSection.getCanvasSectionId());
        page.addObject("updateSuccessful", successful);
        return page;
    }

    @RequestMapping(value = "/{sectionId}/save", params ="saveCourseConfiguration", method = RequestMethod.POST)
    public ModelAndView saveTotalClassMinutes(@PathVariable String sectionId, @ModelAttribute("courseConfigurationForm") @Valid CourseConfigurationForm classSetupForm, BindingResult bindingResult) throws IOException, NoLtiSessionException {
        if (bindingResult.hasErrors()){
            ModelAndView page = new ModelAndView("/courseConfiguration");
            page.addObject("error", "Please correct user input and try saving again.");
            page.addObject("selectedSectionId", sectionId);
            return page;
        } else {
            LtiSession ltiSession = ltiLaunch.getLtiSession();
            LOG.info(ltiSession.getEid() + " saving course settings for " + ltiSession.getCanvasCourseId() + ", minutes: "
                 + classSetupForm.getTotalClassMinutes() + ", per session: " + classSetupForm.getDefaultMinutesPerSession());

            persistenceService.saveCourseMinutes(classSetupForm, ltiSession.getCanvasCourseId());
            return new ModelAndView("forward:/courseConfiguration/" + sectionId + "?updateSuccessful=true");
        }

    }
    
}
