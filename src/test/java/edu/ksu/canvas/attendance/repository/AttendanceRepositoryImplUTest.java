package edu.ksu.canvas.attendance.repository;

import edu.ksu.canvas.attendance.entity.Attendance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;


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

    @Test(expected=NullPointerException.class)
    public void deleteAttendanceByCourseAndDayOfClass_NullDateOfClass() {
        int irrelevantCourseId = 2;
        Date nullDateOfClass = null;
        long irrelevantSectionId = 2;

        attendanceRepository.deleteAttendanceByCourseAndDayOfClass(irrelevantCourseId, nullDateOfClass, irrelevantSectionId);
    }

}
