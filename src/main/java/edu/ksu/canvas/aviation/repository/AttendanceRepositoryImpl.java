package edu.ksu.canvas.aviation.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Repository;

import edu.ksu.canvas.aviation.entity.Attendance;


@Repository
public class AttendanceRepositoryImpl implements AttendanceRepositoryCustom {

    private static final int BATCH_SIZE = 30;

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * @throws NullPointerException when the dateOfClass parameter is null
     */
    public List<Attendance> getAttendanceByCourseAndDayOfClass(int courseId, Date dateOfClass) {
        Validate.notNull(dateOfClass, "The dateOfClass parameter must not be null");

        String jpql = "SELECT a " +
                      "FROM Attendance a join fetch a.aviationStudent " +
                      "WHERE a.aviationStudent.canvasCourseId = :courseId and trunc(a.dateOfClass) = trunc(:dateOfClass)";

        TypedQuery<Attendance> query = entityManager.createQuery(jpql, Attendance.class);
        query.setParameter("courseId", courseId);
        query.setParameter("dateOfClass", dateOfClass, TemporalType.DATE);

        return query.getResultList();
    }


    @Transactional
    public void saveInBatches(List<Attendance> attendances) {
        if (attendances == null || attendances.size() == 0) {
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

}
