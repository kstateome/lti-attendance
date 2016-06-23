package edu.ksu.canvas.attendance.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import edu.ksu.canvas.attendance.repository.AttendanceStudentRepository;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class AttendanceStudentServiceUTest {

    private AviationStudentService studentService;
    
    @Mock
    private AttendanceStudentRepository mockStudentRepository;
    
    
    @Before
    public void setup() {
        studentService = new AviationStudentService();
        Whitebox.setInternalState(studentService, mockStudentRepository);
    }
    
    
    @Test
    public void getStudentByStudentID_HappyPath() {
        long studentId = 500;
        AttendanceStudent expectedStudent = new AttendanceStudent();
        expectedStudent.setStudentId(50L);
        
       when(mockStudentRepository.findByStudentId(studentId)).thenReturn(expectedStudent);
       AttendanceStudent actualStudent = studentService.getStudent(studentId);
       
       assertEquals(expectedStudent, actualStudent);
    }

    @Test
    public void getStudentBySISID_HappyPath() {
        String sisID = "sisId";
        AttendanceStudent expectedStudent = new AttendanceStudent();
        expectedStudent.setSisUserId(sisID);

        when(mockStudentRepository.findBySisUserId(sisID)).thenReturn(expectedStudent);
        AttendanceStudent actualStudent = studentService.getStudent(sisID);

        assertEquals(expectedStudent, actualStudent);
    }
}
