package edu.ksu.canvas.aviation.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import edu.ksu.canvas.aviation.model.AttendanceSummaryModel;
import edu.ksu.canvas.aviation.model.AttendanceSummaryModel.Entry;


@Repository
public class ReportRepository {


    @PersistenceContext
    private EntityManager entityManager;


    public List<AttendanceSummaryModel> getAttendanceSummary(long sectionId) {
        String sql =
                "select course_id, section_id, student_id, student_name, " +
                "sum_minutes_madeup, " +
                "sum_minutes_missed - sum_minutes_madeup as remaining_minutes_madeup, " +
                "sum_minutes_missed, " +
                "round(sum_minutes_missed / course_total_minutes * 100,2) as percent_course_missed " +
                "from ( " +
                "  select course.course_id, course.total_minutes as course_total_minutes, " +
                "  student.section_id, student.student_id, student.student_name, " +
                "  nvl(missed.sum_minutes_missed,0) as sum_minutes_missed, " +
                "  nvl(madeup.sum_minutes_madeup,0) as sum_minutes_madeup " +
                "  from aviation_course course, aviation_student student, " +
                " ( " +
                "    select student_id, sum(nvl(minutes_missed,0)) as sum_minutes_missed " +
                "    from aviation_attendance " +
                "    group by student_id " +
                "  ) missed, " +
                "  ( " +
                "    select student_id, sum(nvl(minutes_madeup,0)) as sum_minutes_madeup " +
                "    from aviation_makeup " +
                "    group by student_id " +
                "  ) madeup " +
                "  where course.canvas_course_id = student.canvas_course_id(+) and " +
                "  student.student_id = missed.student_id(+) and " +
                "  student.student_id = madeup.student_id(+) and " +
                "  course.course_id IN " +
                "  ( " +
                "    select distinct course.course_id " +
                "    from aviation_student student, aviation_course course " +
                "    where student.canvas_course_id = course.canvas_course_id and section_id = :section_id " +
                "  ) " +
                ") " +
                "order by course_id, section_id, student_name";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("section_id", sectionId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<AttendanceSummaryModel> ret = new ArrayList<AttendanceSummaryModel>();


        AttendanceSummaryModel currentSection = null;
        long currentSectionId = -1;

        for (Object[] result : results) {
            sectionId = ((BigDecimal) result[1]).longValue();
            if (currentSection == null || currentSectionId != sectionId) {
                currentSectionId = sectionId;
                currentSection = new AttendanceSummaryModel(sectionId);
                ret.add(currentSection);
            }

            currentSection.add(new Entry(
                    ((BigDecimal) result[0]).longValue(),
                    sectionId,
                    ((BigDecimal) result[2]).longValue(),
                    (String) result[3],
                    ((BigDecimal) result[4]).intValue(),
                    ((BigDecimal) result[5]).intValue(),
                    ((BigDecimal) result[6]).intValue(),
                    ((BigDecimal) result[7]).doubleValue()
            ));
        }

        return ret;
    }

}
