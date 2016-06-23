package edu.ksu.canvas.attendance.services;

import org.junit.Before;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;

import edu.ksu.canvas.attendance.repository.AviationStudentRepository;
import edu.ksu.canvas.attendance.repository.MakeupRepository;


public class BaseMakeupServiceUTest {
    
    protected MakeupService makeupService;
    
    @Mock
    protected MakeupRepository mockMakeupRepository;
    
    @Mock
    protected AviationStudentRepository mockStudentRepository;
    
    
    @Before
    public void setup() {
        makeupService = new MakeupService();
        Whitebox.setInternalState(makeupService, mockMakeupRepository);
        Whitebox.setInternalState(makeupService, mockStudentRepository);
    }
    
}
