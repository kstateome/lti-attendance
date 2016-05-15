package edu.ksu.canvas.aviation.services;

import edu.ksu.canvas.aviation.entity.Attendance;
import edu.ksu.canvas.aviation.entity.AviationCourse;
import edu.ksu.canvas.aviation.entity.AviationSection;
import edu.ksu.canvas.aviation.entity.AviationStudent;
import edu.ksu.canvas.aviation.entity.MakeupTracker;
import edu.ksu.canvas.aviation.enums.Status;
import edu.ksu.canvas.aviation.form.MakeupTrackerForm;
import edu.ksu.canvas.aviation.form.RosterForm;
import edu.ksu.canvas.aviation.form.CourseConfigurationForm;
import edu.ksu.canvas.aviation.model.AttendanceModel;
import edu.ksu.canvas.aviation.model.SectionInfo;
import edu.ksu.canvas.aviation.repository.AttendanceRepository;
import edu.ksu.canvas.aviation.repository.AviationCourseRepository;
import edu.ksu.canvas.aviation.repository.AviationSectionRepository;
import edu.ksu.canvas.aviation.repository.AviationStudentRepository;
import edu.ksu.canvas.aviation.repository.MakeupTrackerRepository;
import edu.ksu.canvas.enums.EnrollmentType;
import edu.ksu.canvas.exception.InvalidOauthTokenException;
import edu.ksu.canvas.interfaces.EnrollmentsReader;
import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Section;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class PersistenceService {
    
    private static final Logger LOG = Logger.getLogger(PersistenceService.class);    
    private static final int DEFAULT_TOTAL_CLASS_MINUTES = 2160; //DEFAULT_MINUTES_PER_CLASS * 3 days a week * 16 weeks
    private static final int DEFAULT_MINUTES_PER_CLASS = 45;

    
    @Autowired
    private AviationCourseRepository aviationCourseRepository;

    @Autowired
    private AviationStudentRepository aviationStudentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private MakeupTrackerRepository makeupTrackerRepository;
    
    @Autowired
    private AviationStudentRepository studentRepository;
    
    @Autowired
    private AviationSectionRepository sectionRepository;
    
    
    public void deleteMakeup(String makeupTrackerId) {
        makeupTrackerRepository.delete(Long.valueOf(makeupTrackerId));
    }

    public void saveCourseMinutes(CourseConfigurationForm classSetupForm, String courseId) {

        Long canvasCourseId = Long.parseLong(courseId);
        AviationCourse aviationCourse = aviationCourseRepository.findByCanvasCourseId(canvasCourseId);
        if(aviationCourse == null){
            aviationCourse = new AviationCourse(canvasCourseId, classSetupForm.getTotalClassMinutes(), classSetupForm.getDefaultMinutesPerSession());
        }
        else{
            aviationCourse.setDefaultMinutesPerSession(classSetupForm.getDefaultMinutesPerSession());
            aviationCourse.setTotalMinutes(classSetupForm.getTotalClassMinutes());
        }

        aviationCourseRepository.save(aviationCourse);
    }
    
    public void saveMakeups(MakeupTrackerForm form) {
        
        for(MakeupTracker makeup: form.getEntries()) {
            if(makeup.getMakeupTrackerId() == null) {
                AviationStudent student = aviationStudentRepository.findByStudentId(form.getStudentId());
                makeup.setAviationStudent(student);
                makeupTrackerRepository.save(makeup);
            } else {
                MakeupTracker tracker = makeupTrackerRepository.findByMakeupTrackerId(makeup.getMakeupTrackerId());
                tracker.setDateMadeUp(makeup.getDateMadeUp());
                tracker.setDateOfClass(makeup.getDateOfClass());
                tracker.setMinutesMadeUp(makeup.getMinutesMadeUp());
                tracker.setProjectDescription(makeup.getProjectDescription());
                makeupTrackerRepository.save(tracker);
            }

        }
    }

    /**
     * This method is tuned to save as fast as possible. Data is loaded and saved in batches.
     */
    public void saveClassAttendance(RosterForm rosterForm) {
        long begin = System.currentTimeMillis();
       
        List<Attendance> saveToDb = new ArrayList<>();
        int defaultMinutesMissedPerSession = rosterForm.getDefaultMinutesPerSession();
        
        List<Attendance> attendancesInDBForCourse = null;
        for (SectionInfo sectionInfo: rosterForm.getSectionInfoList()) {
            List<AviationStudent> aviationStudents = null;
           
            for(AttendanceModel attendanceModel : sectionInfo.getAttendances()) {
                if(attendanceModel.getAttendanceId() == null) {
                    if(aviationStudents == null) {
                        long beginLoad = System.currentTimeMillis();
                        aviationStudents = studentRepository.findBySectionIdOrderByNameAsc(sectionInfo.getSectionId());
                        long endLoad = System.currentTimeMillis();
                        LOG.debug("loaded "+aviationStudents.size()+" students by section in "+(endLoad-beginLoad)+" millis..");
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
                    if(attendancesInDBForCourse == null) {
                        long beginLoad = System.currentTimeMillis();
                        attendancesInDBForCourse = attendanceRepository.getAttendanceByCourseByDayOfClass(sectionInfo.getCanvasCourseId(), rosterForm.getCurrentDate());
                        long endLoad = System.currentTimeMillis();
                        LOG.debug("loaded "+attendancesInDBForCourse.size()+" attendance entries for course in "+(endLoad-beginLoad)+" millis..");
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
        LOG.debug("saving in batches took "+(endSave-beginSave)+" millis");
        LOG.info("Saving attendances took "+(end-begin)+" millis");
    }

    private void adjustMinutesMissedBasedOnAttendnaceStatus(Attendance attendance, int defaultMinutesMissedPerSession) {
        if(attendance.getStatus() == Status.PRESENT) {
            attendance.setMinutesMissed(null);
        } else if(attendance.getStatus() == Status.ABSENT) {
            attendance.setMinutesMissed(defaultMinutesMissedPerSession);
        }
    }
    
    public AviationCourse synchronizeCourseFromCanvasToDb(long canvasCourseId) {
        AviationCourse aviationCourse = aviationCourseRepository.findByCanvasCourseId(canvasCourseId);
        
        if(aviationCourse == null) {
            aviationCourse = new AviationCourse();
            aviationCourse.setTotalMinutes(DEFAULT_TOTAL_CLASS_MINUTES);
            aviationCourse.setDefaultMinutesPerSession(DEFAULT_MINUTES_PER_CLASS);
            aviationCourse.setCanvasCourseId(canvasCourseId);
        }
        
        return aviationCourseRepository.save(aviationCourse);
    }
    
    public List<AviationSection> synchronizeSectionsFromCanvasToDb(List<Section> sections) {
        List<AviationSection> ret = new ArrayList<>();
        
        for(Section section: sections) {
            AviationSection aviationSection = sectionRepository.findByCanvasSectionId(Long.valueOf(section.getId()));
            
            if(aviationSection == null) {
                aviationSection = new AviationSection();
            }
            
            aviationSection.setName(section.getName());
            aviationSection.setCanvasSectionId(Long.valueOf(section.getId()));
            aviationSection.setCanvasCourseId(Long.valueOf(section.getCourseId()));
            
            sectionRepository.save(aviationSection);
        }
        
        return ret;
    }
    
    
    public List<AviationStudent> synchronizeStudentsFromCanvasToDb(List<Section> sections, EnrollmentsReader enrollmentsReader) throws InvalidOauthTokenException, IOException {
        List<AviationStudent> ret = new ArrayList<>();
        
        for(Section section: sections) {
            List<AviationStudent> existingStudents = studentRepository.findBySectionIdOrderByNameAsc(section.getId());
            
            for (Enrollment enrollment : enrollmentsReader.getSectionEnrollments((int) section.getId(), Collections.singletonList(EnrollmentType.STUDENT))) {    
                List<AviationStudent> foundUsers = existingStudents.stream().filter(u -> u.getSisUserId().equals(enrollment.getUser().getSisUserId()) ).collect(Collectors.toList());
                
                if(foundUsers.isEmpty()) {
                    AviationStudent student = new AviationStudent();
                    student.setSisUserId(enrollment.getUser().getSisUserId());
                    student.setName(enrollment.getUser().getSortableName());
                    student.setSectionId(section.getId());
                    student.setCanvasCourseId(section.getCourseId());
                    
                    studentRepository.save(student);
                    ret.add(student);
                } else {
                    ret.addAll(foundUsers);
                }
            }
        }
        
        return ret;
    }
    
    public void loadCourseInfoIntoForm(CourseConfigurationForm courseConfigurationForm, Long courseId) {
        AviationCourse aviationCourse = aviationCourseRepository.findByCanvasCourseId(courseId);
        
        courseConfigurationForm.setTotalClassMinutes(aviationCourse.getTotalMinutes());
        courseConfigurationForm.setDefaultMinutesPerSession(aviationCourse.getDefaultMinutesPerSession());
    }

    public void loadAttendanceIntoRoster(RosterForm rosterForm, Date date) {
        long begin = System.currentTimeMillis();
        
        Long canvaseCourseId = rosterForm.getSectionInfoList().get(0).getCanvasCourseId();
        List<Attendance> attendancesInDb = attendanceRepository.getAttendanceByCourseByDayOfClass(canvaseCourseId, date);
        LOG.debug("attendances found for a given couse and a given day of class: "+attendancesInDb.size());
        
        
        for(SectionInfo sectionInfo: rosterForm.getSectionInfoList()) {
            List<AttendanceModel> sectionAttendances = new ArrayList<>();
            List<AviationStudent> aviationStudents = studentRepository.findBySectionIdOrderByNameAsc(sectionInfo.getSectionId());
            
            for(AviationStudent student: aviationStudents) {
                Attendance foundAttendance = findAttendanceFrom(attendancesInDb, student);
                if(foundAttendance == null) {
                    sectionAttendances.add(new AttendanceModel(student, Status.PRESENT, date));
                } else {
                    sectionAttendances.add(new AttendanceModel(foundAttendance));
                }
            }
            
            sectionInfo.setAttendances(sectionAttendances);
        }
        
        long end = System.currentTimeMillis();
        LOG.info("loadAttendanceForDateIntoRoster took: "+(end - begin)+" millis");
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
