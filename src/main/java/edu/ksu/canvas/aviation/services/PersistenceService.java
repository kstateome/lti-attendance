package edu.ksu.canvas.aviation.services;

import edu.ksu.canvas.aviation.entity.Attendance;
import edu.ksu.canvas.aviation.entity.AviationCourse;
import edu.ksu.canvas.aviation.form.RosterForm;
import edu.ksu.canvas.aviation.repository.AttendanceRepository;
import edu.ksu.canvas.aviation.repository.AviationCourseRepository;
import edu.ksu.lti.model.LtiSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.collection.generic.BitOperations;

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
    private AttendanceRepository attendanceRepository;

    public void saveClassTotalMinutes(RosterForm rosterForm, LtiSession ltiSession) {
        AviationCourse aviationCourse = aviationCourseRepository.findByCourseId(Long.parseLong(ltiSession.getCanvasCourseId()));
        aviationCourse.setTotalMinutes(rosterForm.getClassTotalMinutes());
        aviationCourseRepository.save(aviationCourse);
    }

}
