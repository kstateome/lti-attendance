package edu.ksu.canvas.aviation.controller;

import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.model.AttendanceSummaryModel;
import edu.ksu.canvas.aviation.services.AviationSectionService;
import edu.ksu.canvas.aviation.services.CanvasApiWrapperService;
import edu.ksu.canvas.aviation.services.ReportService;
import edu.ksu.canvas.aviation.util.DropDownOrganizer;
import edu.ksu.canvas.error.NoLtiSessionException;
import org.apache.commons.validator.routines.LongValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;


@Controller
@Scope("session")
@RequestMapping("/attendanceSummary")
public class AttendanceSummaryController extends AviationBaseController {

    private static final Logger LOG = Logger.getLogger(AttendanceSummaryController.class);

    @Autowired
    private ReportService reportService;

    @Autowired
    private AviationSectionService sectionService;

    @Autowired
    protected CanvasApiWrapperService canvasService;


    @RequestMapping()
    public ModelAndView attendanceSummary() throws NoLtiSessionException {
        return attendanceSummary(null);
    }

    @RequestMapping("/{sectionId}")
    public ModelAndView attendanceSummary(@PathVariable String sectionId) throws NoLtiSessionException {
        LOG.info("eid: " + canvasService.getEid() + " is viewing the attendance summary report.");

        Long validatedSectionId = LongValidator.getInstance().validate(sectionId);
        if(validatedSectionId == null) {
            return new ModelAndView("forward:roster");
        }

        AviationSection selectedSection = getSelectedSection(validatedSectionId);
        List<AviationSection> sections = selectedSection == null ? new ArrayList<>() : sectionService.getSectionsByCourse(selectedSection.getCanvasCourseId());

        ModelAndView page = new ModelAndView("attendanceSummary");

        //Add the course name to the page for report printing purposes
        page.addObject("courseName", canvasService.getCourseName());

        page.addObject("selectedSectionId", validatedSectionId);
        List<AttendanceSummaryModel> summaryForSections = reportService.getAttendanceSummaryReport(validatedSectionId);
        page.addObject("attendanceSummaryForSections", summaryForSections);
        page.addObject("sectionList", DropDownOrganizer.sortWithSelectedSectionFirst(sections, sectionId));

        return page;
    }

}
