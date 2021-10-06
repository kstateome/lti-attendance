package edu.ksu.canvas.attendance.services;

import org.junit.Before;
import org.mockito.Mock;

import edu.ksu.canvas.attendance.repository.AttendanceStudentRepository;
import edu.ksu.canvas.attendance.repository.MakeupRepository;
import org.springframework.test.util.ReflectionTestUtils;


public class BaseMakeupServiceUTest {
    
    protected MakeupService makeupService;
    
    @Mock
    protected MakeupRepository mockMakeupRepository;
    
    @Mock
    protected AttendanceStudentRepository mockStudentRepository;
    
    
    @Before
    public void setup() {
        makeupService = new MakeupService();
        ReflectionTestUtils.setField(makeupService, "makeupRepository", mockMakeupRepository);
        ReflectionTestUtils.setField(makeupService, "attendanceStudentRepository", mockStudentRepository);
    }
    
}
