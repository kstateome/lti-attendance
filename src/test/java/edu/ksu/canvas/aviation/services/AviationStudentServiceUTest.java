package edu.ksu.canvas.aviation.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class AviationStudentServiceUTest {

    private AviationStudentService studentService;
    
    @Mock
    private AviationStudentRepository mockStudentRepository;
    
    
    @Before
    public void setup() {
        studentService = new AviationStudentService();
        Whitebox.setInternalState(studentService, mockStudentRepository);
    }
    
    
    @Test
    public void getStudentByStudentID_HappyPath() {
        long studentId = 500;
        AviationStudent expectedStudent = new AviationStudent();
        expectedStudent.setStudentId(50L);
        
       when(mockStudentRepository.findByStudentId(studentId)).thenReturn(expectedStudent);
       AviationStudent actualStudent = studentService.getStudent(studentId);
       
       assertEquals(expectedStudent, actualStudent);
    }

    @Test
    public void getStudentBySISID_HappyPath() {
        String sisID = "sisId";
        AviationStudent expectedStudent = new AviationStudent();
        expectedStudent.setSisUserId(sisID);

        when(mockStudentRepository.findBySisUserId(sisID)).thenReturn(expectedStudent);
        AviationStudent actualStudent = studentService.getStudent(sisID);

        assertEquals(expectedStudent, actualStudent);
    }
}
