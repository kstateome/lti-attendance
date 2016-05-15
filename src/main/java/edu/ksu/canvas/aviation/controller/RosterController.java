package edu.ksu.canvas.aviation.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import edu.ksu.canvas.aviation.factory.SectionInfoFactory;
import edu.ksu.canvas.aviation.form.RosterForm;
import edu.ksu.canvas.aviation.util.DropDownOrganizer;
import edu.ksu.canvas.error.InvalidInstanceException;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.canvas.error.OauthTokenRequiredException;
import edu.ksu.lti.model.LtiSession;


@Controller
@Scope("session")
@SessionAttributes("rosterForm")
@RequestMapping("/roster")
public class RosterController extends AviationBaseController {
    
    private static final Logger LOG = Logger.getLogger(RosterController.class);
    
    @Autowired
    private SectionInfoFactory sectionInfoFactory;

    
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
        SectionState sectionState = getSectionState(sectionId);
        sectionId = sectionState.selectedSection.getCanvasSectionId().toString();
        
        //Sets the date to today if not already set
        if (date == null){
            date = new Date();
        }
        RosterForm rosterForm = new RosterForm();
        rosterForm.setCurrentDate(date);
        rosterForm.setSectionInfoList(sectionInfoFactory.getSectionInfos(sectionState.sections));
        persistenceService.loadCourseInfoIntoForm(rosterForm, sectionState.selectedSection.getCanvasCourseId());
        persistenceService.loadAttendanceIntoRoster(rosterForm, date);
        
        ModelAndView page = new ModelAndView("roster");
        page.addObject("rosterForm", rosterForm);
        page.addObject("sectionList", DropDownOrganizer.sortWithSelectedSectionFirst(sectionState.sections, sectionId));
        page.addObject("selectedSectionId", sectionId);
        
        return page;
    }
    
    @RequestMapping(value= "/save", params = "changeDate", method = RequestMethod.POST)
    public ModelAndView changeDate(@ModelAttribute("rosterForm") RosterForm rosterForm, @ModelAttribute("sectionId") String sectionId) throws IOException, NoLtiSessionException {
        return roster(rosterForm.getCurrentDate(), sectionId);
    }
    
    @RequestMapping(value = "/save", params = "saveAttendance", method = RequestMethod.POST)
    public ModelAndView saveAttendance(@ModelAttribute("rosterForm") RosterForm rosterForm, @ModelAttribute("sectionId") String sectionId) throws IOException, NoLtiSessionException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info("Attempting to save section attendance for section : " + sectionId + " User: " + ltiSession.getEid());

        persistenceService.saveClassAttendance(rosterForm);
        return roster(rosterForm.getCurrentDate(), sectionId);
    }
    
}
