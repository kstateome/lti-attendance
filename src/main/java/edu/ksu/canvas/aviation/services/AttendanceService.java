package edu.ksu.canvas.aviation.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ksu.canvas.aviation.entity.Attendance;
import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.enums.Status;
import edu.ksu.canvas.aviation.form.RosterForm;
import edu.ksu.canvas.aviation.model.AttendanceModel;
import edu.ksu.canvas.aviation.model.SectionModel;
import edu.ksu.canvas.aviation.repository.AttendanceRepository;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;


@Component
public class AttendanceService {

    private static final Logger LOG = Logger.getLogger(AttendanceService.class);

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private AviationStudentRepository studentRepository;


    /**
     * This method is tuned to save as fast as possible. Data is loaded and saved in batches.
     */
    public void save(RosterForm rosterForm) {
        long begin = System.currentTimeMillis();

        List<Attendance> saveToDb = new ArrayList<>();
        int defaultMinutesMissedPerSession = rosterForm.getDefaultMinutesPerSession();

        List<Attendance> attendancesInDBForCourse = null;
        for (SectionModel sectionModel : rosterForm.getSectionModels()) {
            List<AviationStudent> aviationStudents = null;

            for (AttendanceModel attendanceModel : sectionModel.getAttendances()) {
                if (attendanceModel.getAttendanceId() == null) {
                    if (aviationStudents == null) {
                        long beginLoad = System.currentTimeMillis();
                        aviationStudents = studentRepository.findBySectionIdOrderByNameAsc(sectionModel.getSectionId());
                        long endLoad = System.currentTimeMillis();
                        LOG.debug("loaded " + aviationStudents.size() + " students by section in " + (endLoad - beginLoad) + " millis..");
                    }

                    Attendance attendance = new Attendance();
                    AviationStudent aviationStudent = aviationStudents.stream().filter(s -> s.getStudentId().equals(attendanceModel.getAviationStudentId())).findFirst().get();
                    attendance.setAviationStudent(aviationStudent);
                    attendance.setDateOfClass(attendanceModel.getDateOfClass());
                    attendance.setMinutesMissed(attendanceModel.getMinutesMissed());
                    attendance.setStatus(attendanceModel.getStatus());
                    adjustMinutesMissedBasedOnAttendnaceStatus(attendance, defaultMinutesMissedPerSession);

                    saveToDb.add(attendance);
                } else {
                    if (attendancesInDBForCourse == null) {
                        long beginLoad = System.currentTimeMillis();
                        attendancesInDBForCourse = attendanceRepository.getAttendanceByCourseAndDayOfClass(sectionModel.getCanvasCourseId(), rosterForm.getCurrentDate());
                        long endLoad = System.currentTimeMillis();
                        LOG.debug("loaded " + attendancesInDBForCourse.size() + " attendance entries for course in " + (endLoad - beginLoad) + " millis..");
                    }

                    Attendance attendance = attendancesInDBForCourse.stream().filter(a -> a.getAttendanceId().equals(attendanceModel.getAttendanceId())).findFirst().get();
                    attendance.setMinutesMissed(attendanceModel.getMinutesMissed());
                    attendance.setStatus(attendanceModel.getStatus());
                    adjustMinutesMissedBasedOnAttendnaceStatus(attendance, defaultMinutesMissedPerSession);

                    saveToDb.add(attendance);
                }
            }
        }

        long beginSave = System.currentTimeMillis();
        attendanceRepository.saveInBatches(saveToDb);
        long endSave = System.currentTimeMillis();

        long end = System.currentTimeMillis();
        LOG.debug("saving in batches took " + (endSave - beginSave) + " millis");
        LOG.info("Saving attendances took " + (end - begin) + " millis");
    }

    private void adjustMinutesMissedBasedOnAttendnaceStatus(Attendance attendance, int defaultMinutesMissedPerSession) {
        if (attendance.getStatus() == Status.PRESENT) {
            attendance.setMinutesMissed(null);
        } else if (attendance.getStatus() == Status.ABSENT) {
            attendance.setMinutesMissed(defaultMinutesMissedPerSession);
        }
    }


    public void loadIntoForm(RosterForm rosterForm, Date date) {
        long begin = System.currentTimeMillis();

        Integer canvaseCourseId = rosterForm.getSectionModels().get(0).getCanvasCourseId();
        List<Attendance> attendancesInDb = attendanceRepository.getAttendanceByCourseAndDayOfClass(canvaseCourseId, date);
        LOG.debug("attendances found for a given couse and a given day of class: " + attendancesInDb.size());


        for (SectionModel sectionModel : rosterForm.getSectionModels()) {
            List<AttendanceModel> sectionAttendances = new ArrayList<>();
            List<AviationStudent> aviationStudents = studentRepository.findBySectionIdOrderByNameAsc(sectionModel.getSectionId());

            for (AviationStudent student : aviationStudents) {
                Attendance foundAttendance = findAttendanceFrom(attendancesInDb, student);
                if (foundAttendance == null) {
                    sectionAttendances.add(new AttendanceModel(student, Status.PRESENT, date));
                } else {
                    sectionAttendances.add(new AttendanceModel(foundAttendance));
                }
            }

            sectionModel.setAttendances(sectionAttendances);
        }

        long end = System.currentTimeMillis();
        LOG.info("loadAttendanceForDateIntoRoster took: " + (end - begin) + " millis");
    }

    private Attendance findAttendanceFrom(List<Attendance> attendances, AviationStudent student) {
        List<Attendance> matchingAttendances =
                attendances.stream()
                        .filter(attendance -> attendance.getAviationStudent().getStudentId().equals(student.getStudentId()))
                        .collect(Collectors.toList());

        // by definition of the data there should only be one...
        return matchingAttendances.isEmpty() ? null : matchingAttendances.get(0);

    }

}
