package edu.ksu.canvas.attendance.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.ksu.canvas.attendance.config.TestDatabaseConfig;
import edu.ksu.canvas.attendance.entity.Attendance;
import edu.ksu.canvas.attendance.entity.AviationCourse;
import edu.ksu.canvas.attendance.entity.AviationStudent;
import edu.ksu.canvas.attendance.entity.Makeup;
import edu.ksu.canvas.attendance.enums.Status;
import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.services.SynchronizationService;

import static org.junit.Assert.*;


@Transactional
@ActiveProfiles("test")
@ContextConfiguration(classes={TestDatabaseConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ReportRepositoryITest {
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private AviationStudentRepository studentRepository;
    
    @Autowired
    private MakeupRepository makeupRepository;
    
    @Autowired
    private AviationCourseRepository courseRepository;
    
    @Autowired
    private ReportRepository reportRepository;
    
    private AviationCourse existingCourse;
    private AviationStudent existingStudent;
    private List<Attendance> existingAttendances;
    private List<Makeup> existingMakeups;
    
    private final int courseTotalMinutes = SynchronizationService.DEFAULT_TOTAL_CLASS_MINUTES;
    private final int expectedNumberOfAttendanceSummaries = 1;
    private final int expectedNumberOfStudentsInSection = 1;
    private final long expectedSectionId = 500L;
    private final int expectedSumMinutesMadeUp = 5;
    private final int expectedSumMinutesMissed = 11;
    private final int expectedRemainingMinutesMadeup = expectedSumMinutesMissed - expectedSumMinutesMadeUp;
    private final float expectedPercentCourseMissed = Math.round(expectedSumMinutesMissed * 1.0 /courseTotalMinutes * 100) / 2;


    @Before
    public void setup() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        
        AviationCourse course = new AviationCourse();
        course.setCanvasCourseId(2000L);
        course.setDefaultMinutesPerSession(courseTotalMinutes);
        course.setTotalMinutes(SynchronizationService.DEFAULT_TOTAL_CLASS_MINUTES);
        existingCourse = courseRepository.save(course);
        
        AviationStudent student = new AviationStudent();
        student.setSisUserId("1001");
        student.setCanvasCourseId(existingCourse.getCanvasCourseId());
        student.setCanvasSectionId(expectedSectionId);
        student.setName("Zoglmann, Kurt");
        student.setDeleted(false);
        existingStudent = studentRepository.save(student);
        
        Attendance attendance = new Attendance();
        attendance.setAviationStudent(existingStudent);
        attendance.setDateOfClass(sdf.parse("5/21/2016"));
        attendance.setMinutesMissed(5);
        attendance.setStatus(Status.TARDY);
        attendance = attendanceRepository.save(attendance);
        existingAttendances = new ArrayList<>();
        existingAttendances.add(attendance);
        
        attendance = new Attendance();
        attendance.setAviationStudent(existingStudent);
        attendance.setDateOfClass(sdf.parse("5/22/2016"));
        attendance.setMinutesMissed(6);
        attendance.setStatus(Status.TARDY);
        attendance = attendanceRepository.save(attendance);
        existingAttendances.add(attendance);
        
        Makeup makeup = new Makeup();
        makeup.setAviationStudent(existingStudent);
        makeup.setDateMadeUp(sdf.parse("6/1/16"));
        makeup.setMinutesMadeUp(2);
        makeup.setProjectDescription("random project");
        makeup = makeupRepository.save(makeup);
        existingMakeups = new ArrayList<>();
        existingMakeups.add(makeup);
        
        makeup = new Makeup();
        makeup.setAviationStudent(existingStudent);
        makeup.setDateMadeUp(sdf.parse("6/2/16"));
        makeup.setMinutesMadeUp(3);
        makeup.setProjectDescription("random project");
        makeup = makeupRepository.save(makeup);
        existingMakeups.add(makeup);
    }
    
    
    @Test
    public void getAttendanceSummary_HappyPath() {
        long existingSectionId = existingStudent.getCanvasSectionId();
        
        List<AttendanceSummaryModel> actualAttendanceSummaries = reportRepository.getAttendanceSummary(existingSectionId);
        
        assertEquals(expectedNumberOfAttendanceSummaries, actualAttendanceSummaries.size());
        AttendanceSummaryModel actualAttendanceSummary = actualAttendanceSummaries.get(0);
        assertEquals(expectedSectionId, actualAttendanceSummary.getSectionId());
        assertEquals(expectedNumberOfStudentsInSection, actualAttendanceSummary.getEntries().size());
        AttendanceSummaryModel.Entry actualEntry = actualAttendanceSummary.getEntries().get(0);
        assertEquals(expectedSumMinutesMadeUp, actualEntry.getSumMinutesMadeup());
        assertEquals(expectedRemainingMinutesMadeup, actualEntry.getRemainingMinutesMadeup());
        assertEquals(expectedSumMinutesMissed, actualEntry.getSumMinutesMissed());
        assertEquals(expectedPercentCourseMissed, actualEntry.getPercentCourseMissed(), 1.0);
    }
    
}
