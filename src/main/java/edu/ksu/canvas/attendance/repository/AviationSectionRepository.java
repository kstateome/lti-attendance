package edu.ksu.canvas.attendance.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.ksu.canvas.attendance.entity.AttendanceSection;


@Repository
public interface AviationSectionRepository extends CrudRepository<AttendanceSection, Long> {

    AttendanceSection findBySectionId(Long sectionId);

    AttendanceSection findByCanvasSectionId(Long canvasSectionId);

    List<AttendanceSection> findByCanvasCourseId(Long canvasCourseId);

}
