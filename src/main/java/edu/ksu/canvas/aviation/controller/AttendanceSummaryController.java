package edu.ksu.canvas.aviation.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.LongValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.model.AttendanceSummaryModel;
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
        LOG.info("eid: " + ltiSession.getEid() + " is viewing the attendance summary report.");

        Long validatedSectionId = LongValidator.getInstance().validate(sectionId);
        if(validatedSectionId == null) {
            return new ModelAndView("forward:roster");
        }

        AviationSection selectedSection = getSelectedSection(validatedSectionId);
        List<AviationSection> sections = selectedSection == null ? new ArrayList<>() : sectionService.getSectionsByCourse(selectedSection.getCanvasCourseId());

        ModelAndView page = new ModelAndView("attendanceSummary");
        page.addObject("selectedSectionId", validatedSectionId);
        List<AttendanceSummaryModel> summaryForSections = reportService.getAttendanceSummaryReport(validatedSectionId);
        page.addObject("attendanceSummaryForSections", summaryForSections);
        page.addObject("sectionList", DropDownOrganizer.sortWithSelectedSectionFirst(sections, sectionId));

        return page;
    }

}
