package edu.ksu.canvas.attendance.repository;

import edu.ksu.canvas.attendance.entity.Attendance;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;


@Repository
public class AttendanceRepositoryImpl implements AttendanceRepositoryCustom {

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

        String jpql ="SELECT NEW edu.ksu.canvas.attendance.repository.AttendanceCommentEntry(att.attendanceStudent.studentId, att.dateOfClass, att.notes) " +
                    "FROM Attendance att " +
                    "WHERE att.notes IS NOT NULL AND att.attendanceStudent.canvasSectionId = :canvasSectionId ";

        Query query = entityManager.createQuery(jpql);
        query.setParameter("canvasSectionId", sectionId);

        List<AttendanceCommentEntry> results = (List<AttendanceCommentEntry>) query.getResultList();

        return groupCommentsByStudents(results);
    }

    /**
     * Group and organize students with his/her comments
     * @param studentResults
     * @return a map with pairing a student with all his/her comments
     */
    private Map<Long, String> groupCommentsByStudents(List<AttendanceCommentEntry> studentResults) {
        Map<Long, List<AttendanceCommentEntry>> commentsSeparatedByStudent = studentResults.stream().collect(Collectors.groupingBy(AttendanceCommentEntry::getStudentId));
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
    public void deleteAttendanceByCourseAndDayOfClass(long courseId, Date dateOfClass, long sectionId) {
        Validate.notNull(dateOfClass, "The dateOfClass parameter must not be null");

        String jpql = "DELETE FROM Attendance WHERE attendanceId IN (" +
                "select a.attendanceId " +
                "FROM Attendance a " +
                "WHERE a.attendanceStudent.canvasCourseId = :courseId " +
                "AND trunc(a.dateOfClass) = trunc(:dateOfClass)" +
                "AND a.attendanceStudent.canvasSectionId = :sectionId)";

        javax.persistence.Query query = entityManager.createQuery(jpql);
        query.setParameter("courseId", courseId);
        query.setParameter("dateOfClass", dateOfClass, TemporalType.DATE);
        query.setParameter("sectionId", sectionId);
        query.executeUpdate();
    }

}
