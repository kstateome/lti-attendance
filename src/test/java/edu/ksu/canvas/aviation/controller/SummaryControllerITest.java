package edu.ksu.canvas.aviation.controller;

import edu.ksu.canvas.aviation.entity.*;
import edu.ksu.canvas.aviation.enums.Status;
import edu.ksu.canvas.aviation.model.AttendanceSummaryModel;
import edu.ksu.canvas.aviation.repository.*;
import edu.ksu.canvas.aviation.services.SynchronizationService;
import edu.ksu.canvas.error.NoLtiSessionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.NestedServletException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(SpringJUnit4ClassRunner.class)
public class SummaryControllerITest extends BaseControllerITest {

    private AviationCourse existingCourse;
    private AviationSection existingSection;
    private AviationStudent existingStudent;
    private Attendance existingAttendance;
    private Makeup existingMakeup;


    @Autowired
    private AviationCourseRepository courseRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private AviationSectionRepository sectionRepository;

    @Autowired
    private AviationStudentRepository studentRepository;

    @Autowired
    private MakeupRepository makeupRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;


    @Before
    public void additionalSetup() throws NoLtiSessionException, ParseException {
        existingCourse = new AviationCourse();
        existingCourse.setCanvasCourseId(2000L);
        existingCourse.setDefaultMinutesPerSession(10);
        existingCourse.setTotalMinutes(SynchronizationService.DEFAULT_TOTAL_CLASS_MINUTES);
        existingCourse = courseRepository.save(existingCourse);

        existingSection = new AviationSection();
        existingSection.setCanvasCourseId(existingCourse.getCanvasCourseId());
        existingSection.setCanvasSectionId(1000L);
        existingSection = sectionRepository.save(existingSection);

        existingStudent = new AviationStudent();
        existingStudent.setCanvasCourseId(existingCourse.getCanvasCourseId());
        existingStudent.setName("Zoglmann, Brian");
        existingStudent.setCanvasSectionId(existingSection.getSectionId());
        existingStudent.setSisUserId("SisId");
        existingStudent.setDeleted(false);
        existingStudent = studentRepository.save(existingStudent);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        existingAttendance = new Attendance();
        existingAttendance.setAviationStudent(existingStudent);
        existingAttendance.setDateOfClass(sdf.parse("5/21/2016"));
        existingAttendance.getDateOfClass();
        existingAttendance.setMinutesMissed(5);
        existingAttendance.setStatus(Status.TARDY);
        existingAttendance = attendanceRepository.save(existingAttendance);

        existingMakeup = new Makeup();
        existingMakeup.setAviationStudent(existingStudent);
        existingMakeup.setDateOfClass(sdf.parse("05/01/2016"));
        existingMakeup.setDateMadeUp(sdf.parse("05/20/2016"));
        existingMakeup.setMinutesMadeUp(10);
        existingMakeup.setProjectDescription("some important project");
        existingMakeup = makeupRepository.save(existingMakeup);

        when(mockLtiSession.getCanvasCourseId()).thenReturn(String.valueOf(existingCourse.getCanvasCourseId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void studentSummary_NonNumberSectionId() throws Throwable {
        Long studentId = existingStudent.getStudentId();
        String nonNumberSectionId = "Numbers";
        try {
            mockMvc.perform(get("/studentSummary/" + nonNumberSectionId + "/" + studentId));
        } catch (NestedServletException ne) {
            throw ne.getCause();
        }
    }


    @Test(expected = IllegalArgumentException.class)
    public void studentSummary_NonNumberForStudentId() throws Throwable {
        Long sectionId = existingSection.getCanvasSectionId();
        String nonNumberstudentId = "L33t";
        try {
            mockMvc.perform(get("/studentSummary/" + sectionId + "/" + nonNumberstudentId));
        } catch (NestedServletException ne) {
            throw ne.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void studentSummary_NonExistantStudentId() throws Throwable {
        Long sectionId = existingSection.getCanvasSectionId();
        Long nonExistStudentId = -1L;
        try {
            mockMvc.perform(get("/studentSummary/" + sectionId + "/" + nonExistStudentId));
        } catch (NestedServletException ne) {
            throw ne.getCause();
        }
    }



    @Test
    @SuppressWarnings("unchecked")
    public void summaryReport_HappyPath() throws Exception {
        Long sectionId = existingSection.getCanvasSectionId();
        Long studentId = existingStudent.getStudentId();

        List<AttendanceSummaryModel> attendanceSummaryModelList = reportRepository.getAttendanceSummary(sectionId);

        for(AttendanceSummaryModel attendanceSummaryModel: attendanceSummaryModelList) {
            for(AttendanceSummaryModel.Entry entry : attendanceSummaryModel.getEntries()) {
                if(entry.getStudentId() == existingStudent.getStudentId()) {
                    AttendanceSummaryModel.Entry studentSummary = new AttendanceSummaryModel.Entry(existingCourse.getCourseId(),existingSection.getSectionId(),studentId,existingStudent.getName(),existingStudent.getDeleted(),entry.getSumMinutesMadeup(),entry.getRemainingMinutesMadeup(),entry.getSumMinutesMissed(),entry.getPercentCourseMissed());
                    mockMvc.perform(get("/studentSummary/"+sectionId+"/"+studentId))
                            .andExpect(status().isOk())
                            .andExpect(view().name("studentSummary"))
                            .andExpect(model().attribute("sectionId", is(sectionId.toString())))
                            .andExpect(model().attribute("student", hasProperty("studentId", is(studentId))))
                            .andExpect(model().attribute("attendanceSummaryEntry",
                                    allOf(
                                            hasProperty("sumMinutesMissed", is(studentSummary.getSumMinutesMissed())),
                                            hasProperty("sumMinutesMadeup", is(studentSummary.getSumMinutesMadeup())),
                                            hasProperty("remainingMinutesMadeup", is(studentSummary.getRemainingMinutesMadeup())),
                                            hasProperty("percentCourseMissed", is(studentSummary.getPercentCourseMissed()))
                                    )))
                            .andExpect(model().attribute("summaryForm",
                                    allOf(
                                            hasProperty("sectionId", is(sectionId)),
                                            hasProperty("studentId", is(studentId)),
                                            hasProperty("entries", hasSize(1)),
                                            hasProperty("entries",
                                                    containsInAnyOrder(
                                                            allOf(
                                                                    hasProperty("makeupId", is(existingMakeup.getMakeupId())),
                                                                    hasProperty("dateOfClass", is(existingMakeup.getDateOfClass())),
                                                                    hasProperty("dateMadeUp", is(existingMakeup.getDateMadeUp())),
                                                                    hasProperty("projectDescription", is(existingMakeup.getProjectDescription())),
                                                                    hasProperty("minutesMadeUp", is(existingMakeup.getMinutesMadeUp())),
                                                                    hasProperty("toBeDeletedFlag", is(false))
                                                            )
                                                    )
                                            ))));
                }
            }
        }

    }

}
