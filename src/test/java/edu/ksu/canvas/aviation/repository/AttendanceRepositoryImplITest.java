package edu.ksu.canvas.aviation.repository;

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

import edu.ksu.canvas.aviation.config.TestDatabaseConfig;
import edu.ksu.canvas.aviation.entity.Attendance;
import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.enums.Status;

import static org.junit.Assert.*;


@Transactional
@ActiveProfiles("test")
@ContextConfiguration(classes={TestDatabaseConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class AttendanceRepositoryImplITest {

    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private AviationStudentRepository studentRepository;
    
    private Attendance existingAttendance; 
    
    
    @Before
    public void setup() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        
        AviationStudent student = new AviationStudent();
        student.setSisUserId("1001");
        student.setCanvasCourseId(1000);
        student.setSectionId(500L);
        student.setName("Zoglmann, Kurt");
        studentRepository.save(student);
        
        Attendance attendance = new Attendance();
        attendance.setAviationStudent(student);
        attendance.setDateOfClass(sdf.parse("5/21/2016"));
        attendance.setMinutesMissed(5);
        attendance.setStatus(Status.TARDY);
        existingAttendance = attendanceRepository.save(attendance);
    }
    
    @Test
    public void getAttendanceByCourseByDayOfClass_findExistingOneAttendance() {
        int existingCanvasCourseId = existingAttendance.getAviationStudent().getCanvasCourseId();
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
