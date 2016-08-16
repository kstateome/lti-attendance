package edu.ksu.canvas.attendance.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import edu.ksu.canvas.attendance.entity.Makeup;
import edu.ksu.canvas.attendance.form.MakeupForm;
import edu.ksu.canvas.attendance.repository.AttendanceCourseRepository;
import edu.ksu.canvas.attendance.repository.AttendanceSectionRepository;
import edu.ksu.canvas.attendance.repository.AttendanceStudentRepository;
import edu.ksu.canvas.attendance.repository.MakeupRepository;
import edu.ksu.canvas.attendance.services.MakeupService;
import edu.ksu.canvas.attendance.services.SynchronizationService;
import edu.ksu.lti.launch.exception.NoLtiSessionException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringJUnit4ClassRunner.class)
public class MakeupControllerITest extends BaseControllerITest {

    private AttendanceCourse existingCourse;
    private AttendanceSection existingSection;
    private AttendanceStudent existingStudent;
    private Makeup existingMakeup;
    
    @Autowired
    private MakeupService makeupService;
    
    @Autowired
    private AttendanceCourseRepository courseRepository;
    
    @Autowired
    private AttendanceSectionRepository sectionRepository;
    
    @Autowired
    private AttendanceStudentRepository studentRepository;
    
    @Autowired
    private MakeupRepository makeupRepository;
    
    
    @Before
    public void additionalSetup() throws NoLtiSessionException, ParseException {
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
        existingStudent.setCanvasSectionId(existingSection.getSectionId());
        existingStudent.setSisUserId("SisId");
        existingStudent = studentRepository.save(existingStudent);
    }
    
    private void initalizeExistingMakeup() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        existingMakeup = new Makeup();
        existingMakeup.setAttendanceStudent(existingStudent);
        existingMakeup.setDateOfClass(sdf.parse("05/01/2016"));
        existingMakeup.setDateMadeUp(sdf.parse("05/20/2016"));
        existingMakeup.setMinutesMadeUp(10);
        existingMakeup.setProjectDescription("some important project");
    }
    
    private void initializeAndSaveExistingMakeup() throws ParseException {
        initalizeExistingMakeup();
        existingMakeup = makeupRepository.save(existingMakeup);
    }
    
    
    @Test
    public void studentMakeup_NonNumberForSectionId() throws Exception {
        String nonNumberSectionId = "hackers_Rules";
        Long studentId = existingStudent.getStudentId();
        
        mockMvc.perform(get("/studentMakeup/"+nonNumberSectionId+"/"+studentId))
            .andExpect(status().isOk())
            .andExpect(view().name("forward:roster"));
    }
    
    @Test
    public void studentMakeup_NonExistantSectionId() throws Exception {
        Long nonExistantSectionId = -1L;
        Long studentId = existingStudent.getStudentId();
        
        mockMvc.perform(get("/studentMakeup/"+nonExistantSectionId+"/"+studentId))
            .andExpect(status().isOk())
            .andExpect(view().name("forward:roster"));
    }
    
    @Test
    public void studentMakeup_NonNumberForStudentId() throws Exception {
        Long sectionId = existingSection.getCanvasSectionId();
        String nonNumberstudentId = "L33t";
        
        mockMvc.perform(get("/studentMakeup/"+sectionId+"/"+nonNumberstudentId))
            .andExpect(status().isOk())
            .andExpect(view().name("forward:roster/"+sectionId));
    }
    
    @Test
    public void studentMakeup_NonExistantStudentId() throws Exception {
        Long sectionId = existingSection.getCanvasSectionId();
        Long nonExistStudentId = -1L;
        
        mockMvc.perform(get("/studentMakeup/"+sectionId+"/"+nonExistStudentId))
            .andExpect(status().isOk())
            .andExpect(view().name("forward:roster/"+sectionId));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void studentMakeup_HappyPath() throws Exception {
        Long sectionId = existingSection.getCanvasSectionId();
        Long studentId = existingStudent.getStudentId();
        
        initializeAndSaveExistingMakeup();
        
        mockMvc.perform(get("/studentMakeup/"+sectionId+"/"+studentId))
            .andExpect(status().isOk())
            .andExpect(view().name("studentMakeup"))
            .andExpect(model().attribute("sectionId", is(sectionId.toString())))
            .andExpect(model().attribute("student", hasProperty("studentId", is(studentId))))
            .andExpect(model().attribute("makeupForm",
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
    
    @Test
    @SuppressWarnings("unchecked")
    public void saveMakeup_CreateNewMakeup() throws Exception {
        initalizeExistingMakeup();
        Makeup newMakeup = existingMakeup; //not saved to DB;
        int expectedMakeupsSavedInDB = 1;
        
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        
        boolean addEmptyEntry = false;
        MakeupForm makeupForm = makeupService.createMakeupForm(existingStudent.getStudentId(), existingSection.getCanvasSectionId(), addEmptyEntry);
        
        mockMvc.perform(post("/studentMakeup/save")
                .param("saveMakeup", "Save Makeups")
                .param("sectionId", existingSection.getCanvasSectionId().toString())
                .param("studentId", existingStudent.getStudentId().toString())
                .param("entries[0].makeupId", "")
                .param("entries[0].dateMadeUp", sdf.format(newMakeup.getDateMadeUp()))
                .param("entries[0].dateOfClass", sdf.format(newMakeup.getDateOfClass()))
                .param("entries[0].minutesMadeUp", newMakeup.getMinutesMadeUp().toString())
                .param("entries[0].projectDescription", newMakeup.getProjectDescription())
                .param("entries[0].toBeDeletedFlag", "false")
                .sessionAttr("makeupForm", makeupForm))
                .andExpect(status().isOk())
                .andExpect(view().name("studentMakeup"))
                .andExpect(model().attribute("sectionId", is(existingSection.getCanvasSectionId().toString())))
                .andExpect(model().attribute("student", hasProperty("studentId", is(existingStudent.getStudentId()))))
                .andExpect(model().attribute("makeupForm",
                        allOf(
                                hasProperty("sectionId", is(existingSection.getCanvasSectionId())),
                                hasProperty("studentId", is(existingStudent.getStudentId())),
                                hasProperty("entries", hasSize(1)),
                                hasProperty("entries",
                                    containsInAnyOrder(
                                            allOf(
                                                    hasProperty("makeupId", notNullValue()),
                                                    hasProperty("dateOfClass", is(newMakeup.getDateOfClass())),
                                                    hasProperty("dateMadeUp", is(newMakeup.getDateMadeUp())),
                                                    hasProperty("projectDescription", is(newMakeup.getProjectDescription())),
                                                    hasProperty("minutesMadeUp", is(newMakeup.getMinutesMadeUp())),
                                                    hasProperty("toBeDeletedFlag", is(false))
                                            )
                                    )
                                ))))
                .andExpect(model().attribute("updateSuccessful", is(true)));
        
        List<Makeup> savedMakeups = makeupRepository.findByAttendanceStudentOrderByDateOfClassAsc(existingStudent);
        assertEquals(expectedMakeupsSavedInDB, savedMakeups.size());
        Makeup savedMakeupInDB = savedMakeups.get(0);
        assertEquals(newMakeup.getDateMadeUp(), savedMakeupInDB.getDateMadeUp());
        assertEquals(newMakeup.getDateOfClass(), savedMakeupInDB.getDateOfClass());
        assertEquals(newMakeup.getMinutesMadeUp(), savedMakeupInDB.getMinutesMadeUp());
        assertEquals(newMakeup.getProjectDescription(), savedMakeupInDB.getProjectDescription());
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void saveMakeup_UpdateExistingMakeup() throws Exception {
        initializeAndSaveExistingMakeup();
        int expectedMakeupsSavedInDB = 1;
        
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date expectedDateMadeup = sdf.parse("06/20/2016");
        Date expectedDateOfClass = sdf.parse("06/01/2016");
        Integer expectedMinutesMadeUp = 20;
        String expectedProjectDescription = "something more epic";
        
        boolean addEmptyEntry = false;
        MakeupForm makeupForm = makeupService.createMakeupForm(existingStudent.getStudentId(), existingSection.getCanvasSectionId(), addEmptyEntry);
        
        mockMvc.perform(post("/studentMakeup/save")
                .param("saveMakeup", "Save Makeups")
                .param("sectionId", existingSection.getCanvasSectionId().toString())
                .param("studentId", existingStudent.getStudentId().toString())
                .param("entries[0].makeupId", existingMakeup.getMakeupId().toString())
                .param("entries[0].dateMadeUp", sdf.format(expectedDateMadeup))
                .param("entries[0].dateOfClass", sdf.format(expectedDateOfClass))
                .param("entries[0].minutesMadeUp", expectedMinutesMadeUp.toString())
                .param("entries[0].projectDescription", expectedProjectDescription)
                .param("entries[0].toBeDeletedFlag", "false")
                .sessionAttr("makeupForm", makeupForm))
                .andExpect(status().isOk())
                .andExpect(view().name("studentMakeup"))
                .andExpect(model().attribute("sectionId", is(existingSection.getCanvasSectionId().toString())))
                .andExpect(model().attribute("student", hasProperty("studentId", is(existingStudent.getStudentId()))))
                .andExpect(model().attribute("makeupForm",
                        allOf(
                                hasProperty("sectionId", is(existingSection.getCanvasSectionId())),
                                hasProperty("studentId", is(existingStudent.getStudentId())),
                                hasProperty("entries", hasSize(1)),
                                hasProperty("entries",
                                    containsInAnyOrder(
                                            allOf(
                                                    hasProperty("makeupId", notNullValue()),
                                                    hasProperty("dateOfClass", is(expectedDateOfClass)),
                                                    hasProperty("dateMadeUp", is(expectedDateMadeup)),
                                                    hasProperty("projectDescription", is(expectedProjectDescription)),
                                                    hasProperty("minutesMadeUp", is(expectedMinutesMadeUp)),
                                                    hasProperty("toBeDeletedFlag", is(false))
                                            )
                                    )
                                ))))
                .andExpect(model().attribute("updateSuccessful", is(true)));
        
        List<Makeup> savedMakeups = makeupRepository.findByAttendanceStudentOrderByDateOfClassAsc(existingStudent);
        assertEquals(expectedMakeupsSavedInDB, savedMakeups.size());
        Makeup savedMakeupInDB = savedMakeups.get(0);
        assertEquals(expectedDateMadeup, savedMakeupInDB.getDateMadeUp());
        assertEquals(expectedDateOfClass, savedMakeupInDB.getDateOfClass());
        assertEquals(expectedMinutesMadeUp, savedMakeupInDB.getMinutesMadeUp());
        assertEquals(expectedProjectDescription, savedMakeupInDB.getProjectDescription());
    }
    
    
    @Test
    public void saveMakeup_DeleteExistingMakeup() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        initializeAndSaveExistingMakeup();
        int expectedMakeupsSavedInDB = 0; 
        
        boolean addEmptyEntry = false;
        MakeupForm makeupForm = makeupService.createMakeupForm(existingStudent.getStudentId(), existingSection.getCanvasSectionId(), addEmptyEntry);
        
        mockMvc.perform(post("/studentMakeup/save")
                .param("saveMakeup", "Save Makeups")
                .param("sectionId", existingSection.getCanvasSectionId().toString())
                .param("studentId", existingStudent.getStudentId().toString())
                .param("entries[0].makeupId", existingMakeup.getMakeupId().toString())
                .param("entries[0].dateMadeUp", sdf.format(existingMakeup.getDateMadeUp()))
                .param("entries[0].dateOfClass", sdf.format(existingMakeup.getDateOfClass()))
                .param("entries[0].minutesMadeUp", existingMakeup.getMinutesMadeUp().toString())
                .param("entries[0].projectDescription", existingMakeup.getProjectDescription())
                .param("entries[0].toBeDeletedFlag", "true")
                .sessionAttr("makeupForm", makeupForm))
                .andExpect(status().isOk())
                .andExpect(view().name("studentMakeup"))
                .andExpect(model().attribute("sectionId", is(existingSection.getCanvasSectionId().toString())))
                .andExpect(model().attribute("student", hasProperty("studentId", is(existingStudent.getStudentId()))))
                .andExpect(model().attribute("makeupForm",
                        allOf(
                                hasProperty("sectionId", is(existingSection.getCanvasSectionId())),
                                hasProperty("studentId", is(existingStudent.getStudentId())),
                                hasProperty("entries", hasSize(0))
                             )))
                .andExpect(model().attribute("updateSuccessful", is(true)));
        
        List<Makeup> savedMakeups = makeupRepository.findByAttendanceStudentOrderByDateOfClassAsc(existingStudent);
        assertEquals(expectedMakeupsSavedInDB, savedMakeups.size());
    }
    
    @Test
    public void saveMakeup_DeleteUnsavedMakeup() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        initalizeExistingMakeup();
        Makeup unsavedMakeup = existingMakeup;
        int expectedMakeupsSavedInDB = 0; 
        
        boolean addEmptyEntry = false;
        MakeupForm makeupForm = makeupService.createMakeupForm(existingStudent.getStudentId(), existingSection.getCanvasSectionId(), addEmptyEntry);
        
        mockMvc.perform(post("/studentMakeup/save")
                .param("saveMakeup", "Save Makeups")
                .param("sectionId", existingSection.getCanvasSectionId().toString())
                .param("studentId", existingStudent.getStudentId().toString())
                .param("entries[0].makeupId", "")
                .param("entries[0].dateMadeUp", sdf.format(unsavedMakeup.getDateMadeUp()))
                .param("entries[0].dateOfClass", sdf.format(unsavedMakeup.getDateOfClass()))
                .param("entries[0].minutesMadeUp", unsavedMakeup.getMinutesMadeUp().toString())
                .param("entries[0].projectDescription", unsavedMakeup.getProjectDescription())
                .param("entries[0].toBeDeletedFlag", "true")
                .sessionAttr("makeupForm", makeupForm))
                .andExpect(status().isOk())
                .andExpect(view().name("studentMakeup"))
                .andExpect(model().attribute("sectionId", is(existingSection.getCanvasSectionId().toString())))
                .andExpect(model().attribute("student", hasProperty("studentId", is(existingStudent.getStudentId()))))
                .andExpect(model().attribute("makeupForm",
                        allOf(
                                hasProperty("sectionId", is(existingSection.getCanvasSectionId())),
                                hasProperty("studentId", is(existingStudent.getStudentId())),
                                hasProperty("entries", hasSize(0))
                             )));
        
        List<Makeup> savedMakeups = makeupRepository.findByAttendanceStudentOrderByDateOfClassAsc(existingStudent);
        assertEquals(expectedMakeupsSavedInDB, savedMakeups.size());
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void saveMakeup_SaveInvalidMakeupData() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        initalizeExistingMakeup();
        Makeup unsavedMakeup = existingMakeup;
        int invalidMinutesMadeUp = -1;
        unsavedMakeup.setMinutesMadeUp(invalidMinutesMadeUp);
        
        int expectedMakeupsSavedInDB = 0; 
        
        boolean addEmptyEntry = false;
        MakeupForm makeupForm = makeupService.createMakeupForm(existingStudent.getStudentId(), existingSection.getCanvasSectionId(), addEmptyEntry);
        
        mockMvc.perform(post("/studentMakeup/save")
                .param("saveMakeup", "Save Makeups")
                .param("sectionId", existingSection.getCanvasSectionId().toString())
                .param("studentId", existingStudent.getStudentId().toString())
                .param("entries[0].makeupId", "")
                .param("entries[0].dateMadeUp", sdf.format(unsavedMakeup.getDateMadeUp()))
                .param("entries[0].dateOfClass", sdf.format(unsavedMakeup.getDateOfClass()))
                .param("entries[0].minutesMadeUp", unsavedMakeup.getMinutesMadeUp().toString())
                .param("entries[0].projectDescription", unsavedMakeup.getProjectDescription())
                .param("entries[0].toBeDeletedFlag", "false")
                .sessionAttr("makeupForm", makeupForm))
                .andExpect(status().isOk())
                .andExpect(view().name("studentMakeup"))
                .andExpect(model().attribute("sectionId", is(existingSection.getCanvasSectionId().toString())))
                .andExpect(model().attribute("student", hasProperty("studentId", is(existingStudent.getStudentId()))))
                .andExpect(model().attribute("makeupForm",
                        allOf(
                                hasProperty("sectionId", is(existingSection.getCanvasSectionId())),
                                hasProperty("studentId", is(existingStudent.getStudentId())),
                                hasProperty("entries", hasSize(1)),
                                hasProperty("entries",
                                    containsInAnyOrder(
                                            allOf(
                                                    hasProperty("makeupId", nullValue()),
                                                    hasProperty("dateOfClass", is(unsavedMakeup.getDateOfClass())),
                                                    hasProperty("dateMadeUp", is(unsavedMakeup.getDateMadeUp())),
                                                    hasProperty("projectDescription", is(unsavedMakeup.getProjectDescription())),
                                                    hasProperty("minutesMadeUp", is(unsavedMakeup.getMinutesMadeUp())),
                                                    hasProperty("toBeDeletedFlag", is(false))
                                            )
                                    )
                                ))))
                .andExpect(model().attribute("error", notNullValue()));
        
        List<Makeup> savedMakeups = makeupRepository.findByAttendanceStudentOrderByDateOfClassAsc(existingStudent);
        assertEquals(expectedMakeupsSavedInDB, savedMakeups.size());
    }
    
}
