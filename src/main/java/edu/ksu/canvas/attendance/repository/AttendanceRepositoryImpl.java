package edu.ksu.canvas.attendance.repository;

import edu.ksu.canvas.attendance.entity.Attendance;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Repository
public class AttendanceRepositoryImpl implements AttendanceRepositoryCustom {

    private static final Logger LOG = Logger.getLogger(AttendanceRepositoryImpl.class);

    private static final int BATCH_SIZE = 30;

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * @throws NullPointerException when the dateOfClass parameter is null
     */
    @Override
    public List<Attendance> getAttendanceByCourseAndDayOfClass(long courseId, Date dateOfClass) {
        Validate.notNull(dateOfClass, "The dateOfClass parameter must not be null");

        String jpql = "SELECT a " +
                      "FROM Attendance a join fetch a.attendanceStudent " +
                      "WHERE a.attendanceStudent.canvasCourseId = :courseId and trunc(a.dateOfClass) = trunc(:dateOfClass)";

        TypedQuery<Attendance> query = entityManager.createQuery(jpql, Attendance.class);
        query.setParameter("courseId", courseId);
        query.setParameter("dateOfClass", dateOfClass, TemporalType.DATE);

        return query.getResultList();
    }

    /**
     * Gets from the data base all the comments, grouped by student, of one section
     * @param sectionId
     * @return a map with pairing a student with all his/her comments
     */
    @Override
    public Map<Long, String> getAttendanceCommentsBySectionId(long sectionId) {
        Validate.notNull(sectionId, "The sectionId parameter must be not null");

        String sql ="SELECT att.student_id, att.date_of_class, att.notes " +
                    "FROM attendance att " +
                    "INNER JOIN attendance_student ats " +
                    "ON att.student_id = ats.student_id AND ats.canvas_section_id = :canvas_section_id " +
                    "WHERE att.notes IS NOT NULL ";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("canvas_section_id", sectionId);

        List<Object[]> results = query.getResultList();
        Map<Long, String> studentCommentsMap = groupCommentsByStudents(results);

        return studentCommentsMap;
    }

    /**
     * Group and organize students with his/her comments
     * @param results
     * @return a map with pairing a student with all his/her comments
     */
    private Map<Long, String> groupCommentsByStudents(List<Object[]> results) {
        List<StudentDailyComment> studentResults = new ArrayList<>();
        for (Object[] result: results) {
            studentResults.add(new StudentDailyComment(((Number) result[0]).longValue(), (Date) result[1], (String) result[2]));
        }

        Map<Long, List<StudentDailyComment>> commentsSeparatedByStudent = studentResults.stream().collect(Collectors.groupingBy(StudentDailyComment::getStudentId));
        Map<Long, String> studentCommentsMap = new HashMap<>();
        commentsSeparatedByStudent.forEach((studentId, studentCommentObjectList) -> {
            StringBuilder stringBuilder = new StringBuilder();
            studentCommentObjectList.forEach(sdc -> stringBuilder.append(sdc.getComment() + "\n"));
            studentCommentsMap.put(studentId, stringBuilder.toString());
        });
        return studentCommentsMap;
    }

    @Override
    @Transactional
    public void saveInBatches(List<Attendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            return;
        }

        int count = 0;
        for (Attendance attendance : attendances) {
            if (attendance.getAttendanceId() == null) {
                entityManager.persist(attendance);
            } else {
                entityManager.merge(attendance);
            }

            count++;
            if (count % BATCH_SIZE == 0) {
                entityManager.flush();
            }
        }
    }

    @Override
    public void deleteAttendanceByCourseAndDayOfClass(long courseId, Date dateOfClass) {
        Validate.notNull(dateOfClass, "The dateOfClass parameter must not be null");

        String jpql = "DELETE FROM Attendance WHERE attendanceId IN (" +
                "select a.attendanceId " +
                "FROM Attendance a " +
                "WHERE a.attendanceStudent.canvasCourseId = :courseId " +
                "AND trunc(a.dateOfClass) = trunc(:dateOfClass)" +
                ")";

        javax.persistence.Query query = entityManager.createQuery(jpql);
        query.setParameter("courseId", courseId);
        query.setParameter("dateOfClass", dateOfClass, TemporalType.DATE);
        int result = query.executeUpdate();
    }


    /**
     * Private class that represents the data extracted from one attendance.
     * This is needed in order to organize and group comments by student.
     */
    private class StudentDailyComment {
        private Long studentId;
        private String comment;

        public StudentDailyComment(Long studentId, Date date, String comment) {
            this.studentId = studentId;
            this.comment = new SimpleDateFormat("MM/dd/yyyy").format(date)+ ": " + comment;
        }

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

}
