package edu.ksu.canvas.aviation.controller;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.model.AttendanceSummaryForSectionModel;
import edu.ksu.canvas.aviation.services.AviationSectionService;
import edu.ksu.canvas.aviation.services.ReportService;
import edu.ksu.canvas.aviation.util.DropDownOrganizer;
import edu.ksu.canvas.error.InvalidInstanceException;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.canvas.error.OauthTokenRequiredException;
import edu.ksu.lti.model.LtiSession;


@Controller
@Scope("session")
@RequestMapping("/attendanceSummary")
public class AttendanceSummaryController extends AviationBaseController {
    
    private static final Logger LOG = Logger.getLogger(AttendanceSummaryController.class);
    

    @Autowired
    private ReportService reportService;
    
    @Autowired 
    private AviationSectionService sectionService;
    
    
    @RequestMapping()
    public ModelAndView attendanceSummary() throws NoLtiSessionException, OauthTokenRequiredException, InvalidInstanceException, IOException {            
            return attendanceSummary(null);
    }

    @RequestMapping("/{sectionId}")
    public ModelAndView attendanceSummary(@PathVariable String sectionId) throws NoLtiSessionException, OauthTokenRequiredException, InvalidInstanceException, IOException {
        LtiSession ltiSession = ltiLaunch.getLtiSession();
        LOG.info("eid: "+ltiSession.getEid()+" is viewing the attendance summary report.");
        
        AviationSection selectedSection = getSelectedSection(sectionId);
        List<AviationSection> sections = sectionService.getSectionsByCourse(selectedSection.getCanvasCourseId());
        
        ModelAndView page = new ModelAndView("attendanceSummary");        
        page.addObject("selectedSectionId", selectedSection.getCanvasSectionId());
        List<AttendanceSummaryForSectionModel> summaryForSections = reportService.getAttendanceSummaryReport(new Long(sectionId));
        page.addObject("attendanceSummaryForSections", summaryForSections);
        page.addObject("sectionList", DropDownOrganizer.sortWithSelectedSectionFirst(sections, sectionId));

        return page;
    }
    
}
