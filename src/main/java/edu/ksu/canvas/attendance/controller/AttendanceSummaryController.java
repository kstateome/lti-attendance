package edu.ksu.canvas.attendance.controller;

import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.form.CourseConfigurationForm;
import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.services.AttendanceCourseService;
import edu.ksu.canvas.attendance.services.AttendanceSectionService;
import edu.ksu.canvas.attendance.services.ReportService;
import edu.ksu.canvas.attendance.util.DropDownOrganizer;
import edu.ksu.lti.launch.exception.NoLtiSessionException;
import org.apache.commons.validator.routines.LongValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

        //Checking if Attendance Summary is Simple or Aviation
        CourseConfigurationForm courseConfigurationForm = new CourseConfigurationForm();
        boolean isSimpleAttendance = false;
        if (selectedSection != null){
            courseService.loadIntoForm(courseConfigurationForm, selectedSection.getCanvasCourseId());
            isSimpleAttendance = courseConfigurationForm.getSimpleAttendance();
        }

        ModelAndView page = isSimpleAttendance ?
            new ModelAndView("simpleAttendanceSummary") : new ModelAndView("attendanceSummary");

        //Add the course name to the page for report printing purposes
        page.addObject("courseName", canvasService.getCourseName());

        page.addObject("selectedSectionId", validatedSectionId);
        List<AttendanceSummaryModel> summaryForSections = isSimpleAttendance ?
                reportService.getSimpleAttendanceSummaryReport(validatedSectionId)
                : reportService.getAviationAttendanceSummaryReport(validatedSectionId);

        //Sorts students list so the dropped students are at the bottom of the list with the name crossed off.
        for (AttendanceSummaryModel model : summaryForSections){
            model.getEntries().sort(Comparator.comparing(AttendanceSummaryModel.Entry::isDropped));
        }

        page.addObject("attendanceSummaryForSections", summaryForSections);
        page.addObject("sectionList", DropDownOrganizer.sortWithSelectedSectionFirst(sections, sectionId));

        return page;
    }

    @RequestMapping("/{sectionId}/csv")
    public void exportSummaryCVS(@PathVariable("sectionId") String sectionId, HttpServletResponse response) throws IOException, NoLtiSessionException {

        LOG.info("eid:"  + canvasService.getEid() + " has requested a CSV export of the Attendance Summary.");
        StringBuffer csvStringBuffer = new StringBuffer();

        Long validatedSectionId = LongValidator.getInstance().validate(sectionId);
        if (validatedSectionId == null) {
            return;
        }
        AttendanceSection selectedSection = getSelectedSection(validatedSectionId);

        //Checking if Attendance Summary is Simple or Aviation
        CourseConfigurationForm courseConfigurationForm = new CourseConfigurationForm();
        boolean isSimpleAttendance = false;
        if (selectedSection != null){
            courseService.loadIntoForm(courseConfigurationForm, selectedSection.getCanvasCourseId());
            isSimpleAttendance = courseConfigurationForm.getSimpleAttendance();
        }

        //Add headers to the cvs file
        LOG.debug("Creating CSV export for section " + sectionId);
        List<String> headers = isSimpleAttendance ? Arrays.asList("Name", "Total Classes Absent", "Total Classes Tardy")
                                                  : Arrays.asList("Name", "Total Minutes Missed", "Minutes Made Up", "Minutes To Be Made Up", "% of Course Missed");
        writeLine(csvStringBuffer, headers);

        List<AttendanceSummaryModel> summaryForSections = isSimpleAttendance ?
                reportService.getSimpleAttendanceSummaryReport(validatedSectionId)
                : reportService.getAviationAttendanceSummaryReport(validatedSectionId);

        List<String> row;
        //Adds entries as rows to the cvs
        for (AttendanceSummaryModel model : summaryForSections) {
            for (AttendanceSummaryModel.Entry entry: model.getEntries()) {
                if (!entry.isDropped()) {
                    row = isSimpleAttendance ? Arrays.asList(entry.getStudentName(), entry.getTotalClassesMissed() + "", entry.getTotalClassesTardy() + "")
                            : Arrays.asList(entry.getStudentName(), entry.getSumMinutesMissed() + "", entry.getSumMinutesMadeup() + "", entry.getRemainingMinutesMadeup() + "", entry.getPercentCourseMissed() + "");
                    writeLine(csvStringBuffer, row);
                }
            }
        }

        LOG.debug("Exporting created CSV");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=attendance_csv_export.csv.csv");
        ServletOutputStream out = response.getOutputStream();
        InputStream in = new ByteArrayInputStream(csvStringBuffer.toString().getBytes("UTF-8"));

        byte[] outputByte = new byte[4096];
        //copy binary contect to output stream
        while(in.read(outputByte, 0, 4096) != -1)
        {
            out.write(outputByte, 0, 4096);
        }
        in.close();
        out.flush();
        out.close();
    }

    public static void writeLine(StringBuffer sb, List<String> values) throws IOException {

        char separators = ';';
        boolean first = true;


        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            sb.append('"');
            sb.append(value);
            sb.append('"');
            first = false;
        }
        sb.append("\n");
    }






}
