package edu.ksu.canvas.attendance.controller;

import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.enums.AttendanceType;
import edu.ksu.canvas.attendance.form.CourseConfigurationForm;
import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.services.AttendanceCourseService;
import edu.ksu.canvas.attendance.services.AttendanceSectionService;
import edu.ksu.canvas.attendance.services.ReportService;
import edu.ksu.canvas.attendance.util.DropDownOrganizer;
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
import java.util.Comparator;
import java.util.List;


@Controller
@Scope("session")
@RequestMapping("/attendanceSummary")
public class AttendanceSummaryController extends AttendanceBaseController {

    private static final Logger LOG = Logger.getLogger(AttendanceSummaryController.class);

    @Autowired
    private ReportService reportService;

    @Autowired
    private AttendanceSectionService sectionService;

    @Autowired
    private AttendanceCourseService courseService;

    @RequestMapping()
    public ModelAndView attendanceSummary() throws NoLtiSessionException {
        return attendanceSummary(null);
    }

    @RequestMapping("/{sectionId}")
    public ModelAndView attendanceSummary(@PathVariable String sectionId) throws NoLtiSessionException {
        LOG.info("eid: " + canvasService.getEid() + " is viewing the attendance summary report.");

        Long validatedSectionId = LongValidator.getInstance().validate(sectionId);
        if (validatedSectionId == null) {
            return new ModelAndView("forward:roster");
        }

        AttendanceSection selectedSection = getSelectedSection(validatedSectionId);
        List<AttendanceSection> sections = selectedSection == null ? new ArrayList<>() : sectionService.getSectionsByCourse(selectedSection.getCanvasCourseId());

        //Checking if Attendance Summary is Simple or Minuted
        CourseConfigurationForm courseConfigurationForm = new CourseConfigurationForm();
        boolean isSimpleAttendance = false;
        if (selectedSection != null){
            courseService.loadIntoForm(courseConfigurationForm, selectedSection.getCanvasCourseId());
            isSimpleAttendance = courseConfigurationForm.getSimpleAttendance();
        }

        ModelAndView page = !isSimpleAttendance ?
            new ModelAndView("attendanceSummary") : new ModelAndView("simpleAttendanceSummary");

        //Add the course name to the page for report printing purposes
        page.addObject("courseName", canvasService.getCourseName());

        page.addObject("selectedSectionId", validatedSectionId);
        List<AttendanceSummaryModel> summaryForSections = !isSimpleAttendance ?
                reportService.getMinutedAttendanceSummaryReport(validatedSectionId)
                : reportService.getSimpleAttendanceSummaryReport(validatedSectionId);

        //Sorts students list so the dropped students are at the bottom of the list with the name crossed off.
        for (AttendanceSummaryModel model : summaryForSections){
            model.getEntries().sort(Comparator.comparing(AttendanceSummaryModel.Entry::isDropped));
        }

        page.addObject("attendanceSummaryForSections", summaryForSections);
        page.addObject("sectionList", DropDownOrganizer.sortWithSelectedSectionFirst(sections, sectionId));

        return page;
    }

}
