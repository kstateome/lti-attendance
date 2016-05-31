package edu.ksu.canvas.aviation.controller;

import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.form.RosterForm;
import edu.ksu.canvas.aviation.form.RosterFormValidator;
import edu.ksu.canvas.aviation.model.SectionModelFactory;
import edu.ksu.canvas.aviation.services.AttendanceService;
import edu.ksu.canvas.aviation.services.AviationCourseService;
import edu.ksu.canvas.aviation.services.AviationSectionService;
import edu.ksu.canvas.aviation.util.DropDownOrganizer;
import edu.ksu.canvas.error.InvalidInstanceException;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.canvas.error.OauthTokenRequiredException;
import edu.ksu.lti.model.LtiSession;

import org.apache.commons.validator.routines.LongValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Controller
@Scope("session")
@SessionAttributes("rosterForm")
@RequestMapping("/roster")
public class RosterController extends AviationBaseController {

    private static final Logger LOG = Logger.getLogger(RosterController.class);

    @Autowired
    private SectionModelFactory sectionModelFactory;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AviationCourseService courseService;

    @Autowired
    private AviationSectionService sectionService;

    @Autowired
    private RosterFormValidator validator;


    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping()
    public ModelAndView roster(@RequestParam(required = false) Date date) throws NoLtiSessionException, OauthTokenRequiredException, InvalidInstanceException, IOException {
        return roster(date, null);
    }

    @RequestMapping("/{sectionId}")
    public ModelAndView roster(@RequestParam(required = false) Date date, @PathVariable String sectionId) throws NoLtiSessionException, OauthTokenRequiredException, InvalidInstanceException, IOException {
        ltiLaunch.ensureApiTokenPresent(getApplicationName()); //should be present on each call

        Long validatedSectionId = LongValidator.getInstance().validate(sectionId);
        AviationSection selectedSection = getSelectedSection(validatedSectionId);
        if(validatedSectionId == null || selectedSection == null) {
            long canvasCourseId = Long.valueOf(ltiLaunch.getLtiSession().getCanvasCourseId()).longValue();
            selectedSection = sectionService.getFirstSectionOfCourse(canvasCourseId);
            validatedSectionId = selectedSection.getSectionId();
        }

        List<AviationSection> sections = sectionService.getSectionsByCourse(selectedSection.getCanvasCourseId());
        sectionId = selectedSection.getCanvasSectionId().toString();

        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info("eid: " + ltiSession.getEid() + " is viewing the roster.");

        //Sets the date to today if not already set
        if (date == null) {
            date = new Date();
        }
        RosterForm rosterForm = new RosterForm();
        rosterForm.setCurrentDate(date);
        rosterForm.setSectionId(selectedSection.getSectionId());
        rosterForm.setSectionModels(sectionModelFactory.createSectionModels(sections));
        courseService.loadIntoForm(rosterForm, selectedSection.getCanvasCourseId());
        attendanceService.loadIntoForm(rosterForm, date);

        ModelAndView page = new ModelAndView("roster");
        page.addObject("rosterForm", rosterForm);
        page.addObject("sectionList", DropDownOrganizer.sortWithSelectedSectionFirst(sections, sectionId));
        page.addObject("selectedSectionId", sectionId);

        return page;
    }

    @RequestMapping(value = "/{sectionId}/save", params = "changeDate", method = RequestMethod.POST)
    public ModelAndView changeDate(@PathVariable String sectionId, @ModelAttribute("rosterForm") RosterForm rosterForm) throws IOException, NoLtiSessionException {
        return roster(rosterForm.getCurrentDate(), sectionId);
    }

    @RequestMapping(value = "/{sectionId}/save", params = "saveAttendance", method = RequestMethod.POST)
    public ModelAndView saveAttendance(@PathVariable String sectionId, @ModelAttribute("rosterForm") @Valid RosterForm rosterForm, BindingResult bindingResult) throws IOException, NoLtiSessionException {
        validator.validate(rosterForm, bindingResult);

        Long validatedSectionId = LongValidator.getInstance().validate(sectionId);
        if(validatedSectionId == null) {
            return new ModelAndView("forward:roster");
        }

        if (bindingResult.hasErrors()) {
            ModelAndView page = new ModelAndView("roster");
            page.addObject("error", "Please check all sections when correcting user input. Then try saving again.");

            AviationSection selectedSection = getSelectedSection(validatedSectionId);
            List<AviationSection> sections = sectionService.getSectionsByCourse(selectedSection.getCanvasCourseId());
            page.addObject("sectionList", DropDownOrganizer.sortWithSelectedSectionFirst(sections, sectionId));

            page.addObject("selectedSectionId", sectionId);
            return page;
        } else {
            LtiSession ltiSession = ltiLaunch.getLtiSession();
            LOG.info("eid: " + ltiSession.getEid() + " is attempting to save section attendance for section : " + sectionId);

            attendanceService.save(rosterForm);
            ModelAndView page = roster(rosterForm.getCurrentDate(), sectionId);
            page.addObject("saveSuccess", true);
            return page;
        }
    }

}
