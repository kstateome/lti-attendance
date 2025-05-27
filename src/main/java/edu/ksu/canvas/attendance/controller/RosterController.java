package edu.ksu.canvas.attendance.controller;

import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.form.RosterForm;
import edu.ksu.canvas.attendance.form.RosterFormValidator;
import edu.ksu.canvas.attendance.model.SectionModelFactory;
import edu.ksu.canvas.attendance.services.AttendanceCourseService;
import edu.ksu.canvas.attendance.services.AttendanceSectionService;
import edu.ksu.canvas.attendance.services.AttendanceService;
import edu.ksu.canvas.attendance.util.DropDownOrganizer;
import edu.ksu.lti.launch.exception.NoLtiSessionException;
import org.apache.commons.validator.routines.LongValidator;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Controller
@Scope("session")
@SessionAttributes("rosterForm")
@RequestMapping("/roster")
public class RosterController extends AttendanceBaseController {

    private static final Logger LOG = LogManager.getLogger(RosterController.class);

    @Autowired
    private SectionModelFactory sectionModelFactory;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AttendanceCourseService courseService;

    @Autowired
    private AttendanceSectionService sectionService;

    @Autowired
    private RosterFormValidator validator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping()
    public ModelAndView roster(@RequestParam(required = false) Date date) throws NoLtiSessionException {
        return roster(date, null);
    }

    @RequestMapping("/roster/{sectionId}")
    public ModelAndView roster(@RequestParam(required = false) Date date, @PathVariable String sectionId) throws NoLtiSessionException {
        ensureCanvasApiTokenPresent();

        Long validatedSectionId = LongValidator.getInstance().validate(sectionId);
        AttendanceSection selectedSection = getSelectedSection(validatedSectionId);
        if(validatedSectionId == null || selectedSection == null) {
            long canvasCourseId = canvasService.getCourseId();
            selectedSection = sectionService.getFirstSectionOfCourse(canvasCourseId);
            validatedSectionId = selectedSection.getSectionId();
        }

        List<AttendanceSection> sections = sectionService.getSectionsByCourse(selectedSection.getCanvasCourseId());
        sectionId = selectedSection.getCanvasSectionId().toString();

        LOG.info("eid: " + canvasService.getEid() + " is viewing the roster.");

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

    @RequestMapping(value = "/roster/{sectionId}/save", params = "changeDate", method = RequestMethod.POST)
    public ModelAndView changeDate(@PathVariable String sectionId, @ModelAttribute("rosterForm") RosterForm rosterForm) throws NoLtiSessionException {
        return roster(rosterForm.getCurrentDate(), sectionId);
    }

    @RequestMapping(value = "/roster/{sectionId}/save", params = "saveAttendance", method = RequestMethod.POST)
    public ModelAndView saveAttendance(@PathVariable String sectionId, @ModelAttribute("rosterForm") @Valid RosterForm rosterForm, BindingResult bindingResult, HttpServletResponse response) throws NoLtiSessionException {
        validator.validate(rosterForm, bindingResult);

        Long validatedSectionId = LongValidator.getInstance().validate(sectionId);
        if(validatedSectionId == null) {
            return new ModelAndView("forward:roster");
        }

        if (bindingResult.hasErrors()) {
            LOG.warn(bindingResult.getAllErrors().toString());
            ModelAndView page = new ModelAndView("roster");
            page.addObject("error", "Please check all sections when correcting user input. Then try saving again.");

            AttendanceSection selectedSection = getSelectedSection(validatedSectionId);
            List<AttendanceSection> sections = sectionService.getSectionsByCourse(selectedSection.getCanvasCourseId());
            page.addObject("sectionList", DropDownOrganizer.sortWithSelectedSectionFirst(sections, sectionId));

            page.addObject("selectedSectionId", sectionId);
            return page;
        } else {
            LOG.info("eid: " + canvasService.getEid() + " is attempting to save section attendance for section : " + sectionId);

            attendanceService.save(rosterForm);
            ModelAndView page = roster(rosterForm.getCurrentDate(), sectionId);
            page.addObject("saveSuccess", true);
            return page;
        }
    }

    @RequestMapping("/roster/{sectionId}/delete")
    public ModelAndView deleteAttendance(@PathVariable String sectionId, @ModelAttribute("rosterForm") @Valid RosterForm rosterForm) throws NoLtiSessionException {

        Long validatedSectionId = LongValidator.getInstance().validate(sectionId);
        if(validatedSectionId == null) {
            return new ModelAndView("redirect:/roster");
        }

        LOG.info("eid: " + canvasService.getEid() + " is attempting to delete section attendance for section : " + sectionId);

        boolean deleted = attendanceService.delete(rosterForm);
        ModelAndView page = roster(rosterForm.getCurrentDate(), sectionId);
        if(deleted) {
            page.addObject("deleteSuccess", true);
        } else {
            page.addObject("noAttendanceToDelete", true);
        }
        return page;
    }
}
