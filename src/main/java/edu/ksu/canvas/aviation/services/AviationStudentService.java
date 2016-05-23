package edu.ksu.canvas.aviation.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;


@Component
public class AviationStudentService {

    @Autowired
    private AviationStudentRepository studentRepository;
    
    
    public AviationStudent getStudent(long studentId) {
        return studentRepository.findByStudentId(studentId);
    }

}
