package edu.ksu.canvas.attendance.repository;

import edu.ksu.canvas.attendance.entity.Attendance;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;


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
