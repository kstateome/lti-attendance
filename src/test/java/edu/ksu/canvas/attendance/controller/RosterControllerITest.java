package edu.ksu.canvas.attendance.controller;

import edu.ksu.canvas.attendance.entity.Attendance;
import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import edu.ksu.canvas.attendance.enums.Status;
import edu.ksu.canvas.attendance.form.RosterForm;
import edu.ksu.canvas.attendance.model.SectionModelFactory;
import edu.ksu.canvas.attendance.repository.*;
import edu.ksu.canvas.attendance.services.AttendanceCourseService;
import edu.ksu.canvas.attendance.services.AttendanceService;
import edu.ksu.canvas.attendance.services.CanvasApiWrapperService;
import edu.ksu.canvas.attendance.services.SynchronizationService;
import edu.ksu.lti.launch.exception.NoLtiSessionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(SpringJUnit4ClassRunner.class)
public class RosterControllerITest extends BaseControllerITest {

    private AttendanceCourse existingCourse;
    private AttendanceSection existingSection;
    private AttendanceStudent existingStudent;
    
    @Autowired
    private AttendanceCourseRepository courseRepository;
    
    @Autowired
    private AttendanceSectionRepository sectionRepository;
    
    @Autowired
    private AttendanceStudentRepository studentRepository;
    
    @Autowired
    private AttendanceCourseService courseService;
    
    @Autowired
    private AttendanceService attendanceService;
    
    @Autowired
    private CanvasApiWrapperService canvasService;

    @Autowired
    private AttendanceRepositoryCustom attendanceRepository;


    @Before
    public void additionalSetup() throws NoLtiSessionException {
        existingCourse = new AttendanceCourse();
        existingCourse.setCanvasCourseId(2000L);
        existingCourse.setDefaultMinutesPerSession(10);
        existingCourse.setTotalMinutes(SynchronizationService.DEFAULT_TOTAL_CLASS_MINUTES);
        existingCourse = courseRepository.save(existingCourse);
        
        existingSection = new AttendanceSection();
        existingSection.setCanvasCourseId(existingCourse.getCanvasCourseId());
        existingSection.setCanvasSectionId(1000L);
        existingSection = sectionRepository.save(existingSection);
        
        existingStudent = new AttendanceStudent();
        existingStudent.setCanvasCourseId(existingCourse.getCanvasCourseId());
        existingStudent.setName("Zoglmann, Brian");
        existingStudent.setCanvasSectionId(existingSection.getCanvasSectionId());
        existingStudent.setSisUserId("SisId");
        existingStudent = studentRepository.save(existingStudent);
        
        when(mockLtiSession.getCanvasCourseId()).thenReturn(String.valueOf(existingCourse.getCanvasCourseId()));
        when(canvasService.getCourseId()).thenReturn(existingCourse.getCanvasCourseId().intValue());
    }


    @Test
    public void roster_noParametersPresent() throws Exception {
        mockMvc.perform(get("/roster"))
            .andExpect(status().isOk())
            .andExpect(view().name("roster"));
    }
    
    @Test
    public void roster_NonExistantSectionId() throws Exception {
        Long nonExistantSectionId = -1L;
        
        mockMvc.perform(get("/roster/"+nonExistantSectionId))
            .andExpect(status().isOk())
            .andExpect(view().name("roster"));
    }
    
    @Test
    public void roster_NonNumberSectionId() throws Exception {
        String nonNumberSectionId = "Hacker's Delight";
        
        mockMvc.perform(get("/roster/"+nonNumberSectionId))
            .andExpect(status().isOk())
            .andExpect(view().name("roster"));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void roster_HappyPath() throws Exception {

        mockMvc.perform(get("/roster/"+existingSection.getCanvasSectionId()))
            .andExpect(status().isOk())
            .andExpect(view().name("roster"))
            .andExpect(model().attribute("selectedSectionId", is(String.valueOf(existingSection.getCanvasSectionId()))))
            .andExpect(model().attribute("sectionList", hasSize(1)))
            .andExpect(model().attribute("sectionList",
                    containsInAnyOrder(
                            allOf(
                                    hasProperty("sectionId", is(existingSection.getSectionId()))
                            )
                    )))
            .andExpect(model().attribute("rosterForm",
                    allOf(
                            hasProperty("sectionId", is(existingSection.getSectionId())),
                            hasProperty("sectionModels", hasSize(1)),
                            hasProperty("sectionModels",
                                    containsInAnyOrder(
                                            allOf(
                                                    hasProperty("canvasSectionId", is(existingSection.getCanvasSectionId())),
                                                    hasProperty("canvasCourseId", is(existingCourse.getCanvasCourseId())),
                                                    hasProperty("attendances", hasSize(1)),
                                                    hasProperty("attendances",
                                                            containsInAnyOrder(
                                                                    allOf(
                                                                            hasProperty("attendanceStudentId", is(existingStudent.getStudentId()))
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )));
    }

    @Test
    public void defaultToSimpleAttendance() throws Exception {
        mockMvc.perform(get("/roster/"+existingSection.getCanvasSectionId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("rosterForm", hasProperty("simpleAttendance", is(Boolean.TRUE))));
    }
    
    @Test
    public void changeDate_HappyPath() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String dateOfAttendanceAsString = "05/18/2016";
        Date dateOfAttendance = sdf.parse(dateOfAttendanceAsString);
        Long sectionOfExistingCourse = existingSection.getSectionId();
        List<AttendanceSection> sections = new ArrayList<>();
        sections.add(existingSection);
        RosterForm rosterForm = new RosterForm();
        rosterForm.setCurrentDate(dateOfAttendance);
        rosterForm.setSectionId(sectionOfExistingCourse);
        rosterForm.setSectionModels(new SectionModelFactory().createSectionModels(sections));
        courseService.loadIntoForm(rosterForm, existingSection.getCanvasCourseId());
        attendanceService.loadIntoForm(rosterForm, dateOfAttendance);
        
        String expectedDateOfAttendanceAsString = "05/20/2016";
        Date expectedDateOfAttendance = sdf.parse(expectedDateOfAttendanceAsString);
        
        mockMvc.perform(post("/roster/"+existingSection.getCanvasSectionId()+"/save")
                .param("changeDate", "")
                .param("currentDate", expectedDateOfAttendanceAsString)
                .param("sectionId", sectionOfExistingCourse.toString())
                .sessionAttr("rosterForm", rosterForm)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("roster"))
                .andExpect(model().attribute("rosterForm", hasProperty("currentDate", is(expectedDateOfAttendance))));
    }
    
    @Test
    public void saveAttendance_NonNumberSectionId() throws Exception {
        String nonNumberSectionId = "Hacker's Delight";
        
        mockMvc.perform(post("/roster/"+nonNumberSectionId)
            .param("saveAttendance", "Save Attendance"))
            .andExpect(status().isOk())
            .andExpect(view().name("roster"));
    }
    
    @Test
    public void saveAttendance_BadData_NegativeMinutesMissed() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String dateOfAttendanceAsString = "05/18/2016";
        Date dateOfAttendance = sdf.parse(dateOfAttendanceAsString);
        Long sectionOfExistingCourse = existingSection.getSectionId();
        
        Status status = Status.TARDY;
        Integer badMinutesMissed = -1;
        
        List<AttendanceSection> sections = new ArrayList<>();
        sections.add(existingSection);
        RosterForm rosterForm = new RosterForm();
        rosterForm.setCurrentDate(dateOfAttendance);
        rosterForm.setSectionId(sectionOfExistingCourse);
        rosterForm.setSectionModels(new SectionModelFactory().createSectionModels(sections));
        courseService.loadIntoForm(rosterForm, existingSection.getCanvasCourseId());
        attendanceService.loadIntoForm(rosterForm, dateOfAttendance);
        
        mockMvc.perform(post("/roster/"+existingSection.getCanvasSectionId()+"/save")
                .param("saveAttendance", "Save Attendance")
                .param("currentDate", dateOfAttendanceAsString)
                .param("sectionId", sectionOfExistingCourse.toString())
                .param("sectionModels[0].attendances[0].attendanceId", "")
                .param("sectionModels[0].attendances[0].attendanceStudentId", existingStudent.getStudentId().toString())
                .param("sectionModels[0].attendances[0].status", status.toString())
                .param("sectionModels[0].attendances[0].minutesMissed",  badMinutesMissed.toString())
                .sessionAttr("rosterForm", rosterForm)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("roster"))
                .andExpect(model().attribute("error", notNullValue()));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void saveAttendance_HappyPath() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String dateOfAttendanceAsString = "05/18/2016";
        Date dateOfAttendance = sdf.parse(dateOfAttendanceAsString);
        
        Status expectedStatus = Status.TARDY;
        Integer expectedMinutesMissed = 10;
        
        List<AttendanceSection> sections = new ArrayList<>();
        sections.add(existingSection);
        RosterForm rosterForm = new RosterForm();
        rosterForm.setCurrentDate(dateOfAttendance);
        rosterForm.setSectionId(existingSection.getSectionId());
        rosterForm.setSectionModels(new SectionModelFactory().createSectionModels(sections));
        courseService.loadIntoForm(rosterForm, existingSection.getCanvasCourseId());
        attendanceService.loadIntoForm(rosterForm, dateOfAttendance);
        
        mockMvc.perform(post("/roster/"+existingSection.getCanvasSectionId()+"/save")
                .param("saveAttendance", "Save Attendance")
                .param("currentDate", dateOfAttendanceAsString)
                .param("sectionId", existingSection.getSectionId().toString())
                .param("sectionModels[0].attendances[0].attendanceId", "")
                .param("sectionModels[0].attendances[0].attendanceStudentId", existingStudent.getStudentId().toString())
                .param("sectionModels[0].attendances[0].status", expectedStatus.toString())
                .param("sectionModels[0].attendances[0].minutesMissed",  expectedMinutesMissed.toString())
                .sessionAttr("rosterForm", rosterForm)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("roster"))
                .andExpect(model().attribute("saveSuccess", is(true)))
                .andExpect(model().attribute("sectionList", 
                        containsInAnyOrder(
                                allOf(
                                        hasProperty("sectionId", is(existingSection.getSectionId()))
                                     )
                                )))
                .andExpect(model().attribute("rosterForm",
                        allOf(
                                hasProperty("sectionId", is(existingSection.getSectionId())),
                                hasProperty("sectionModels", hasSize(1)),
                                hasProperty("sectionModels",
                                    containsInAnyOrder(
                                        allOf(
                                                hasProperty("canvasSectionId", is(existingSection.getCanvasSectionId())),
                                                hasProperty("canvasCourseId", is(existingCourse.getCanvasCourseId())),
                                                hasProperty("attendances", hasSize(1)),
                                                hasProperty("attendances",
                                                    containsInAnyOrder(
                                                        allOf(
                                                                hasProperty("attendanceStudentId", is(existingStudent.getStudentId())),
                                                                hasProperty("status", is(expectedStatus)),
                                                                hasProperty("minutesMissed", is(expectedMinutesMissed))
                                                        )
                                                    )
                                                )
                                             )
                                        )
                                    )
                        )));
    }

    @Test
    public void deleteAttendance_NoAttendancesToDelete() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String dateOfAttendanceAsString = "05/18/2016";
        Date dateOfAttendance = sdf.parse(dateOfAttendanceAsString);
        Long sectionOfExistingCourse = existingSection.getSectionId();

        List<AttendanceSection> sections = new ArrayList<>();
        sections.add(existingSection);
        RosterForm rosterForm = new RosterForm();
        rosterForm.setCurrentDate(dateOfAttendance);
        rosterForm.setSectionId(sectionOfExistingCourse);
        rosterForm.setSectionModels(new SectionModelFactory().createSectionModels(sections));
        courseService.loadIntoForm(rosterForm, existingSection.getCanvasCourseId());
        attendanceService.loadIntoForm(rosterForm, dateOfAttendance);

        mockMvc.perform(post("/roster/"+existingSection.getCanvasSectionId()+"/delete")
                        .param("deleteAttendance", "Delete Attendance")
                        .param("currentDate", dateOfAttendanceAsString)
                        .param("sectionId", sectionOfExistingCourse.toString())
                        .sessionAttr("rosterForm", rosterForm)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("roster"))
                .andExpect(model().attribute("noAttendanceToDelete", is(true)));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteAttendance_HappyPath() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String dateOfAttendanceAsString = "05/18/2016";
        Date dateOfAttendance = sdf.parse(dateOfAttendanceAsString);

        Status expectedStatus = Status.TARDY;
        Integer expectedMinutesMissed = 10;

        List<AttendanceSection> sections = new ArrayList<>();
        sections.add(existingSection);
        RosterForm rosterForm = new RosterForm();
        rosterForm.setCurrentDate(dateOfAttendance);
        rosterForm.setSectionId(existingSection.getSectionId());
        rosterForm.setSectionModels(new SectionModelFactory().createSectionModels(sections));
        courseService.loadIntoForm(rosterForm, existingSection.getCanvasCourseId());
        attendanceService.loadIntoForm(rosterForm, dateOfAttendance);

        // TODO: This way is not working. Mockito is throwing a NullPointerException whe using any() and not returning the expected attendanceList otherwise.
        //List<Attendance> attendanceList = new ArrayList<Attendance>();
        //attendanceList.add(new Attendance());
        //attendanceRepository = mock(AttendanceRepositoryCustom.class);
        //when(attendanceRepository.getAttendanceByCourseAndDayOfClass(any(), any())).thenReturn(attendanceList);

        mockMvc.perform(post("/roster/"+existingSection.getCanvasSectionId()+"/save")
                        .param("saveAttendance", "Save Attendance")
                        .param("currentDate", dateOfAttendanceAsString)
                        .param("sectionId", existingSection.getSectionId().toString())
                        .param("sectionModels[0].attendances[0].attendanceId", "")
                        .param("sectionModels[0].attendances[0].attendanceStudentId", existingStudent.getStudentId().toString())
                        .param("sectionModels[0].attendances[0].status", expectedStatus.toString())
                        .param("sectionModels[0].attendances[0].minutesMissed",  expectedMinutesMissed.toString())
                        .sessionAttr("rosterForm", rosterForm)
        );

        mockMvc.perform(post("/roster/"+existingSection.getCanvasSectionId()+"/delete")
                        .param("deleteAttendance", "Delete Attendance")
                        .param("currentDate", dateOfAttendanceAsString)
                        .param("sectionId", existingSection.getSectionId().toString())
                        .sessionAttr("rosterForm", rosterForm)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("roster"))
                .andExpect(model().attribute("deleteSuccess", is(true)))
                .andExpect(model().attribute("sectionList",
                        containsInAnyOrder(
                                allOf(
                                        hasProperty("sectionId", is(existingSection.getSectionId()))
                                )
                        )))
                .andExpect(model().attribute("rosterForm",
                        allOf(
                                hasProperty("sectionId", is(existingSection.getSectionId())),
                                hasProperty("sectionModels", hasSize(1)),
                                hasProperty("sectionModels",
                                        containsInAnyOrder(
                                                allOf(
                                                        hasProperty("canvasSectionId", is(existingSection.getCanvasSectionId())),
                                                        hasProperty("canvasCourseId", is(existingCourse.getCanvasCourseId()))
                                                )
                                        )
                               )
                        )));
    }

}
