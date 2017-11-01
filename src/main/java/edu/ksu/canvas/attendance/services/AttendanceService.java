package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.attendance.entity.Attendance;
import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import edu.ksu.canvas.attendance.enums.Status;
import edu.ksu.canvas.attendance.form.RosterForm;
import edu.ksu.canvas.attendance.model.AttendanceModel;
import edu.ksu.canvas.attendance.model.SectionModel;
import edu.ksu.canvas.attendance.repository.AttendanceRepository;
import edu.ksu.canvas.attendance.repository.AttendanceStudentRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class AttendanceService {

    private static final Logger LOG = Logger.getLogger(AttendanceService.class);

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private AttendanceStudentRepository studentRepository;


    /**
     * This method is tuned to save as fast as possible. Data is loaded and saved in batches.
     */
    @Transactional
    public void save(RosterForm rosterForm) {
        long begin = System.currentTimeMillis();

        List<Attendance> saveToDb = new ArrayList<>();

        List<Attendance> attendancesInDBForCourse = null;
        for (SectionModel sectionModel : rosterForm.getSectionModels()) {
            List<AttendanceStudent> attendanceStudents = null;
            if (sectionModel.getCanvasSectionId() == rosterForm.getSectionId()) {
                for (AttendanceModel attendanceModel : sectionModel.getAttendances()) {
                    if (attendanceModel.getAttendanceId() == null) {
                        if (attendanceStudents == null) {
                            long beginLoad = System.currentTimeMillis();
                            attendanceStudents = studentRepository.findByCanvasSectionIdOrderByNameAsc(sectionModel.getCanvasSectionId());
                            long endLoad = System.currentTimeMillis();
                            LOG.debug("loaded " + attendanceStudents.size() + " students by section in " + (endLoad - beginLoad) + " millis..");
                        }

                        Attendance attendance = new Attendance();
                        AttendanceStudent attendanceStudent = attendanceStudents.stream().filter(s -> s.getStudentId().equals(attendanceModel.getAttendanceStudentId())).findFirst().get();
                        attendance.setAttendanceStudent(attendanceStudent);
                        attendance.setDateOfClass(attendanceModel.getDateOfClass());
                        attendance.setMinutesMissed(attendanceModel.getMinutesMissed());
                        attendance.setStatus(attendanceModel.getStatus());
                        attendance.setNotes(attendanceModel.getNotes());
                        adjustMinutesMissedBasedOnAttendanceStatus(attendance);

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
                        attendance.setNotes(attendanceModel.getNotes());
                        adjustMinutesMissedBasedOnAttendanceStatus(attendance);

                        saveToDb.add(attendance);
                    }
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

    private void adjustMinutesMissedBasedOnAttendanceStatus(Attendance attendance) {
        if (attendance.getStatus() == Status.PRESENT) {
            attendance.setMinutesMissed(null);
        }
    }


    public void loadIntoForm(RosterForm rosterForm, Date date) {
        long begin = System.currentTimeMillis();

        Long canvaseCourseId = rosterForm.getSectionModels().get(0).getCanvasCourseId();
        List<Attendance> attendancesInDb = attendanceRepository.getAttendanceByCourseAndDayOfClass(canvaseCourseId, date);
        LOG.debug("attendances found for a given couse and a given day of class: " + attendancesInDb.size());

        for (SectionModel sectionModel : rosterForm.getSectionModels()) {
            List<AttendanceModel> sectionAttendances = new ArrayList<>();
            List<AttendanceStudent> attendanceStudents = studentRepository.findByCanvasSectionIdOrderByNameAsc(sectionModel.getCanvasSectionId());

            attendanceStudents.sort(Comparator.comparing(AttendanceStudent::getDeleted));
            
            for (AttendanceStudent student : attendanceStudents) {
                Attendance foundAttendance = findAttendanceFrom(attendancesInDb, student);
                if (foundAttendance == null) {
                    Status status = student.getDeleted() ? Status.ABSENT : Status.NA;
                    sectionAttendances.add(new AttendanceModel(student, status, date));
                } else {
                    sectionAttendances.add(new AttendanceModel(foundAttendance));
                }
            }

            sectionModel.setAttendances(sectionAttendances);
        }

        long end = System.currentTimeMillis();
        LOG.info("loadAttendanceForDateIntoRoster took: " + (end - begin) + " millis");
    }

    private Attendance findAttendanceFrom(List<Attendance> attendances, AttendanceStudent student) {
        List<Attendance> matchingAttendances =
                attendances.stream()
                        .filter(attendance -> attendance.getAttendanceStudent().getStudentId().equals(student.getStudentId()))
                        .collect(Collectors.toList());

        // by definition of the data there should only be one...
        return matchingAttendances.isEmpty() ? null : matchingAttendances.get(0);

    }

    @Transactional
    public boolean delete(RosterForm rosterForm) {
        List<Attendance> attendancesInDBForCourse = null;
        boolean sectionHasAttendancesForDate = false;
        for (SectionModel sectionModel : rosterForm.getSectionModels()) {
            if (sectionModel.getSectionId() != null && sectionModel.getSectionId().equals(rosterForm.getSectionId())) {
                attendancesInDBForCourse = attendanceRepository.getAttendanceByCourseAndDayOfClass(sectionModel.getCanvasCourseId(), rosterForm.getCurrentDate());
                if (!attendancesInDBForCourse.isEmpty()) {
                    sectionHasAttendancesForDate = true;
                    attendanceRepository.deleteAttendanceByCourseAndDayOfClass(sectionModel.getCanvasCourseId(), rosterForm.getCurrentDate(), rosterForm.getSectionId());
                }
            }
        }
        return sectionHasAttendancesForDate;
    }

    public Map<Long,String> getAttendanceCommentsBySectionId(long sectionId) {
        return attendanceRepository.getAttendanceCommentsBySectionId(sectionId);
    }

}
