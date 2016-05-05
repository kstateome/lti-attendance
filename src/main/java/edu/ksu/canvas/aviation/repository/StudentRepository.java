package edu.ksu.canvas.aviation.repository;

import edu.ksu.canvas.aviation.entity.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long> {
    List<Student> findBySectionId(long sectionId);
    Student findById(String id);
}
