package edu.ksu.canvas.attendance.repository;

import edu.ksu.canvas.attendance.entity.Attendance;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    @Override
    public Map<Long, String> getAttendanceCommentsBySectionId(long sectionId) {
        Validate.notNull(sectionId, "The sectionId parameter must be not null");

        String sql = "SELECT att.student_id,  LISTAGG(att.date_of_class || ': ' || att.notes, ', /n') WITHIN GROUP (ORDER BY att.student_id) AS notes " +
                    "FROM attendance att" +
                    "INNER JOIN attendance_student ats" +
                    "ON att.student_id = ats.student_id AND ats.canvas_section_id = :canvas_section_id" +
                    "WHERE att.notes IS NOT NULL" +
                    "GROUP BY att.student_id";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("canvas_section_id", sectionId);

        List<Object[]> results = query.getResultList();
        Map<Long, String> studentCommentsMap = new HashMap<>();

        for(Object[] result: results) {
            studentCommentsMap.put(((Number) result[0]).longValue(), (String) result[1]);
        }

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

}
