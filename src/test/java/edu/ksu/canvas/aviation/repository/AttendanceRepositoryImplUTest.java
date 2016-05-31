package edu.ksu.canvas.aviation.repository;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import edu.ksu.canvas.aviation.entity.Attendance;


@RunWith(MockitoJUnitRunner.class)
public class AttendanceRepositoryImplUTest {

    private AttendanceRepositoryImpl attendanceRepository;
    
    
    @Before
    public void setup() {
        attendanceRepository = new AttendanceRepositoryImpl();
    }
    
    
    @Test(expected=NullPointerException.class)
    public void getAttendanceByCourseAndDayOfClass_NullDateOfClass() {
        int irrelevantCourseId = 2;
        Date nullDateOfClass = null;
        
        attendanceRepository.getAttendanceByCourseAndDayOfClass(irrelevantCourseId, nullDateOfClass);
    }
    
    
    @Test
    public void saveInBatches_tollerateNullAttendances() {
        List<Attendance> nullAttendances = null;
        
        attendanceRepository.saveInBatches(nullAttendances);
    }

}
