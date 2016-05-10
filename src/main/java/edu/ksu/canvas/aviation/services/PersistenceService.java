package edu.ksu.canvas.aviation.services;

import edu.ksu.canvas.aviation.entity.AviationCourse;
import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.form.RosterForm;
import edu.ksu.canvas.aviation.model.SectionInfo;
import edu.ksu.canvas.aviation.repository.AttendanceRepository;
import edu.ksu.canvas.aviation.repository.AviationCourseRepository;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jesusorr
 * 5/6/16
 */
@Component
public class PersistenceService {
    private static final Logger LOG = Logger.getLogger(PersistenceService.class);

    @Autowired
    private AviationCourseRepository aviationCourseRepository;

    @Autowired
    private AviationStudentRepository aviationStudentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    public void saveCourseMinutes(RosterForm rosterForm, String courseId) {

        Long canvasCourseId = Long.parseLong(courseId);
        AviationCourse aviationCourse = aviationCourseRepository.findByCanvasCourseId(canvasCourseId);
        if(aviationCourse == null){
            aviationCourse = new AviationCourse(canvasCourseId, rosterForm.getTotalClassMinutes(), rosterForm.getDefaultMinutesPerSession());
        }
        else{
            aviationCourse.setDefaultMinutesPerSession(rosterForm.getDefaultMinutesPerSession());
            aviationCourse.setTotalMinutes(rosterForm.getTotalClassMinutes());
        }

        aviationCourseRepository.save(aviationCourse);
    }
    
    public void saveClassAttendance(SectionInfo sectionInfo) {
        LOG.info("Saving section: " + sectionInfo);
        for(AviationStudent aviationStudent : sectionInfo.getStudents()) {
            if(aviationStudentRepository.findBysisUserId(aviationStudent.getSisUserId()) == null) {
                LOG.debug("Saving student: " + aviationStudent);
                aviationStudentRepository.save(aviationStudent);
            }
        }
    }
    
    public RosterForm getCourseMinutes(RosterForm rosterForm, String courseId) {
        Long canvasCourseId = Long.parseLong(courseId);
        AviationCourse aviationCourse = aviationCourseRepository.findByCanvasCourseId(canvasCourseId);
        if(aviationCourse != null){
            rosterForm.setTotalClassMinutes(aviationCourse.getTotalMinutes());
            rosterForm.setDefaultMinutesPerSession(aviationCourse.getDefaultMinutesPerSession());
        }
        return rosterForm;
    }

}
