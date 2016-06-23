package edu.ksu.canvas.attendance.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ksu.canvas.attendance.entity.AviationStudent;
import edu.ksu.canvas.attendance.repository.AviationStudentRepository;


@Component
public class AviationStudentService {

    @Autowired
    private AviationStudentRepository studentRepository;
    
    
    public AviationStudent getStudent(long studentId) {
        return studentRepository.findByStudentId(studentId);
    }

    public AviationStudent getStudent(String sisId) { return studentRepository.findBySisUserId(sisId) ;}

}
