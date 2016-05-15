package edu.ksu.canvas.aviation.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import edu.ksu.canvas.aviation.entity.Attendance;


@Repository
public class AttendanceRepositoryImpl implements AttendanceRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    

    public List<Attendance> getAttendanceByCourseByDayOfClass(Long courseId, Date dateOfClass) {
        String jpql = "SELECT a " +
                      "FROM Attendance a join fetch a.aviationStudent " +
                      "WHERE a.aviationStudent.canvasCourseId = :courseId and trunc(a.dateOfClass) = trunc(:dateOfClass)";
        
        TypedQuery<Attendance> query = entityManager.createQuery(jpql, Attendance.class);
        query.setParameter("courseId", courseId.intValue());
        query.setParameter("dateOfClass", dateOfClass, TemporalType.DATE);
        
        return query.getResultList();
    }
}
