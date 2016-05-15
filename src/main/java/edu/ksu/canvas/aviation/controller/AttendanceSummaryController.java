package edu.ksu.canvas.aviation.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import edu.ksu.canvas.aviation.repository.ReportRepository;
import edu.ksu.canvas.aviation.repository.ReportRepository.AttendanceSummaryForSection;
import edu.ksu.canvas.aviation.util.DropDownOrganizer;
import edu.ksu.canvas.error.InvalidInstanceException;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.canvas.error.OauthTokenRequiredException;


@Controller
@Scope("session")
@RequestMapping("/attendanceSummary")
public class AttendanceSummaryController extends AviationBaseController {

    @Autowired
    private ReportRepository reportRepository;
    
    
    @RequestMapping()
    public ModelAndView attendanceSummary() throws NoLtiSessionException, OauthTokenRequiredException, InvalidInstanceException, IOException {            
            return attendanceSummary(null);
    }

    @RequestMapping("/{sectionId}")
    public ModelAndView attendanceSummary(@PathVariable String sectionId) throws NoLtiSessionException, OauthTokenRequiredException, InvalidInstanceException, IOException {
        ModelAndView page = new ModelAndView("attendanceSummary");

        SectionState sectionState = getSectionState(sectionId);
        page.addObject("selectedSectionId", sectionState.selectedSection.getCanvasSectionId());
        List<AttendanceSummaryForSection> summaryForSections = reportRepository.getAttendanceSummary(new Long(sectionId));
        page.addObject("attendanceSummaryForSections", summaryForSections);
        page.addObject("sectionList", DropDownOrganizer.sortWithSelectedSectionFirst(sectionState.sections, sectionId));

        return page;
    }
    
}
