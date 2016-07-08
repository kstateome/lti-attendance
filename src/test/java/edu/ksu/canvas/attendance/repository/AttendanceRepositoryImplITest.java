package edu.ksu.canvas.attendance.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.StreamSupport;

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
import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import edu.ksu.canvas.attendance.enums.Status;

import static org.junit.Assert.*;


@Transactional
@ActiveProfiles("test")
@ContextConfiguration(classes={TestDatabaseConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class AttendanceRepositoryImplITest {

    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private AttendanceStudentRepository studentRepository;
    
    private Attendance existingAttendance; 
    
    
    @Before
    public void setup() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        
        AttendanceStudent student = new AttendanceStudent();
        student.setSisUserId("1001");
        student.setCanvasCourseId(1000L);
        student.setCanvasSectionId(500L);
        student.setName("Zoglmann, Kurt");
        studentRepository.save(student);
        
        Attendance attendance = new Attendance();
        attendance.setAttendanceStudent(student);
        attendance.setDateOfClass(sdf.parse("5/21/2016"));
        attendance.setMinutesMissed(5);
        attendance.setStatus(Status.TARDY);
        existingAttendance = attendanceRepository.save(attendance);
    }
    
    @Test
    public void getAttendanceByCourseAndDayOfClass_findExistingOneAttendance() {
        long existingCanvasCourseId = existingAttendance.getAttendanceStudent().getCanvasCourseId();
        Date existingDateOfClass = existingAttendance.getDateOfClass();
        int expectedNumberOfAttendances = 1;
        
        List<Attendance> actualAttendances = attendanceRepository.getAttendanceByCourseAndDayOfClass(existingCanvasCourseId, existingDateOfClass);
        
        assertEquals(expectedNumberOfAttendances, actualAttendances.size());
        assertEquals(existingAttendance, actualAttendances.get(0));
    }
    
    
    @Test
    public void saveInBatches_saveNewAttendance() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Attendance attendance = new Attendance();
        attendance.setDateOfClass(sdf.parse("01/01/2016"));
        attendance.setStatus(Status.ABSENT);
        List<Attendance> attendances = new ArrayList<>();
        attendances.add(attendance);
        int expectedNumberOfAttendancesInDb = 2;
        
        attendanceRepository.saveInBatches(attendances);
        int actualAttendancesInDb = (int) StreamSupport.stream(attendanceRepository.findAll().spliterator(), false).count();
        
        assertEquals(expectedNumberOfAttendancesInDb, actualAttendancesInDb);
    }
    
    
    @Test
    public void saveInBatches_updateExistingAttendance() {
        List<Attendance> attendances = new ArrayList<>();
        Attendance expectedAttendance = existingAttendance;
        int existingAttendanceMinutesMissed = expectedAttendance.getMinutesMissed();
        int updatedAttendanceMinutesMissed = existingAttendanceMinutesMissed * 2;
        expectedAttendance.setMinutesMissed(updatedAttendanceMinutesMissed);
        attendances.add(expectedAttendance);
        
        attendanceRepository.saveInBatches(attendances);
        Attendance actualAttendance = attendanceRepository.findByAttendanceId(existingAttendance.getAttendanceId());
        
        assertEquals(expectedAttendance, actualAttendance);
    }
    

}
