package edu.ksu.canvas.aviation.controller;

import javax.validation.Valid;

import edu.ksu.canvas.aviation.form.CourseConfigurationValidator;

import org.apache.commons.validator.routines.LongValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.form.CourseConfigurationForm;
import edu.ksu.canvas.aviation.services.AviationCourseService;
import edu.ksu.canvas.aviation.services.CanvasApiWrapperService;
import edu.ksu.canvas.aviation.services.SynchronizationService;
import edu.ksu.canvas.error.NoLtiSessionException;


@Controller
@Scope("session")
@RequestMapping("/courseConfiguration")
public class CourseConfigurationController extends AviationBaseController {

    private static final Logger LOG = Logger.getLogger(CourseConfigurationController.class);

    @Autowired
    private SynchronizationService synchronizationService;

    @Autowired
    private AviationCourseService courseService;

    @Autowired
    private CourseConfigurationValidator validator;

    @Autowired
    protected CanvasApiWrapperService canvasService;


    @RequestMapping()
    public ModelAndView classSetup() throws NoLtiSessionException, NumberFormatException {
        return classSetup(null, false);
    }

    @RequestMapping("/{sectionId}")
    public ModelAndView classSetup(@PathVariable String sectionId,
                                   @RequestParam(defaultValue = "false", value = "updateSuccessful") boolean successful) throws NoLtiSessionException {

        LOG.info("eid: " + canvasService.getEid() + " is viewing course configuration...");

        Long validatedSectionId = LongValidator.getInstance().validate(sectionId);
        AviationSection selectedSection = validatedSectionId == null ? null : getSelectedSection(validatedSectionId);
        if(validatedSectionId == null || selectedSection == null) {
            return new ModelAndView("forward:roster");
        }

        ModelAndView page = new ModelAndView("courseConfiguration");

        CourseConfigurationForm courseConfigurationForm = new CourseConfigurationForm();
        courseService.loadIntoForm(courseConfigurationForm, selectedSection.getCanvasCourseId());
        page.addObject("courseConfigurationForm", courseConfigurationForm);
        page.addObject("selectedSectionId", selectedSection.getCanvasSectionId());
        page.addObject("updateSuccessful", successful);
        return page;
    }

    @RequestMapping(value = "/{sectionId}/save", params = "saveCourseConfiguration", method = RequestMethod.POST)
    public ModelAndView saveCourseConfiguration(@PathVariable String sectionId, @ModelAttribute("courseConfigurationForm") @Valid CourseConfigurationForm classSetupForm, BindingResult bindingResult) throws NoLtiSessionException {
        validator.validate(classSetupForm, bindingResult);
        if (bindingResult.hasErrors()) {
            ModelAndView page = new ModelAndView("/courseConfiguration");
            page.addObject("error", "Please correct user input and try saving again.");
            page.addObject("selectedSectionId", sectionId);
            return page;
        } else {
            LOG.info("eid: " + canvasService.getEid() + " is saving course settings for " + canvasService.getCourseId() + ", minutes: "
                    + classSetupForm.getTotalClassMinutes() + ", per session: " + classSetupForm.getDefaultMinutesPerSession());

            courseService.save(classSetupForm, Long.valueOf(canvasService.getCourseId()));
            return new ModelAndView("forward:/courseConfiguration/" + sectionId + "?updateSuccessful=true");
        }

    }

    @RequestMapping(value = "/{sectionId}/save", params = "synchronizeWithCanvas", method = RequestMethod.POST)
    public ModelAndView synchronizeWithCanvas(@PathVariable String sectionId) throws NoLtiSessionException, NumberFormatException {
        LOG.info("eid: " + canvasService.getEid() + " is forcing a syncrhonization with Canvas for Canvas Course ID: " + canvasService.getCourseId());
        synchronizationService.synchronize(canvasService.getCourseId());

        return new ModelAndView("forward:/courseConfiguration/" + sectionId);
    }

}
