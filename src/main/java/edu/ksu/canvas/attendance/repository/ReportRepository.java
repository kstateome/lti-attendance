package edu.ksu.canvas.attendance.repository;

import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.model.AttendanceSummaryModel.Entry;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;


@Repository
public class ReportRepository {

    private static final Logger LOG = Logger.getLogger(ReportRepository.class);


    @PersistenceContext
    private EntityManager entityManager;


    public List<AttendanceSummaryModel> getMinutedAttendanceSummary(long canvasSectionId) {
        String sql =
                "select course_id, canvas_section_id, student_id, student_name, deleted," +
                "sum_minutes_madeup, " +
                "sum_minutes_missed - sum_minutes_madeup as remaining_minutes_madeup, " +
                "sum_minutes_missed, " +
                "round(sum_minutes_missed / course_total_minutes * 100,2) as percent_course_missed " +
                "from ( " +
                "  select course.course_id, course.total_minutes as course_total_minutes, " +
                "  student.canvas_section_id, student.student_id, student.student_name, student.deleted," +
                "  nvl(missed.sum_minutes_missed,0) as sum_minutes_missed, " +
                "  nvl(madeup.sum_minutes_madeup,0) as sum_minutes_madeup " +
                "  from attendance_course course " +
                "       left outer join " +
                "       attendance_student student on course.canvas_course_id = student.canvas_course_id " +
                "       left outer join " +
                "       ( " +
                "         select student_id, sum(nvl(minutes_missed,0)) as sum_minutes_missed " +
                "         from attendance " +
                "         group by student_id " +
                "       ) missed on student.student_id = missed.student_id " +
                "       left outer join " +
                "       ( " +
                "         select student_id, sum(nvl(minutes_madeup,0)) as sum_minutes_madeup " +
                "         from attendance_makeup " +
                "         group by student_id " +
                "       ) madeup on student.student_id = madeup.student_id " +
                "  where course.course_id IN " +
                "  ( " +
                "    select distinct course.course_id " +
                "    from attendance_student student, attendance_course course " +
                "    where student.canvas_course_id = course.canvas_course_id and canvas_section_id = :canvas_section_id " +
                "  ) " +
                ") " +
                "order by course_id, canvas_section_id, student_name";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("canvas_section_id", canvasSectionId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<AttendanceSummaryModel> ret = new ArrayList<AttendanceSummaryModel>();


        AttendanceSummaryModel currentSection = null;
        long currentCanvasSectionId = -1;

        for (Object[] result : results) {
            canvasSectionId = ((Number) result[1]).longValue();
            if (currentSection == null || currentCanvasSectionId != canvasSectionId) {
                currentCanvasSectionId = canvasSectionId;
                currentSection = new AttendanceSummaryModel(canvasSectionId);
                ret.add(currentSection);
            }

            currentSection.add(new Entry(
                    ((Number) result[0]).longValue(),
                    canvasSectionId,
                    ((Number) result[2]).longValue(),
                    (String) result[3],
                    getBoolean(((Number)result[4]).intValue()),
                    ((Number) result[5]).intValue(),
                    ((Number) result[6]).intValue(),
                    ((Number) result[7]).intValue(),
                    ((Number) result[8]).doubleValue()
            ));
        }

        return ret;
    }

    public List<AttendanceSummaryModel> getSimpleAttendanceSummary(long canvasSectionId) {
        String sql =
                "select course_id, canvas_section_id, student_id, student_name, deleted," +
                        "total_classes_tardy, " +
                        "total_classes_missed " +
                        "from ( " +
                        "  select course.course_id," +
                        "  student.canvas_section_id, student.student_id, student.student_name, student.deleted," +
                        "  nvl(missed.total_classes_missed,0) as total_classes_missed, " +
                        "  nvl(tardy.total_classes_tardy,0) as total_classes_tardy " +
                        "  from attendance_course course " +
                        "       left outer join " +
                        "       attendance_student student on course.canvas_course_id = student.canvas_course_id " +
                        "       left outer join " +
                        "       ( " +
                        "         select student_id, count(status) as total_classes_missed " +
                        "         from attendance " +
                        "         where status='ABSENT'" +
                        "         group by student_id " +
                        "       ) missed on student.student_id = missed.student_id " +
                        "       left outer join " +
                        "       ( " +
                        "         select student_id, count(status) as total_classes_tardy " +
                        "         from attendance " +
                        "         where status='TARDY'" +
                        "         group by student_id " +
                        "       ) tardy on student.student_id = tardy.student_id " +
                        "  where course.course_id IN " +
                        "  ( " +
                        "    select distinct course.course_id " +
                        "    from attendance_student student, attendance_course course " +
                        "    where student.canvas_course_id = course.canvas_course_id and canvas_section_id = :canvas_section_id " +
                        "  ) " +
                        ") " +
                        "order by course_id, canvas_section_id, student_name";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("canvas_section_id", canvasSectionId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<AttendanceSummaryModel> ret = new ArrayList<AttendanceSummaryModel>();

        AttendanceSummaryModel currentSection = null;
        long currentCanvasSectionId = -1;

        for (Object[] result : results) {
            canvasSectionId = ((Number) result[1]).longValue();
            if (currentSection == null || currentCanvasSectionId != canvasSectionId) {
                currentCanvasSectionId = canvasSectionId;
                currentSection = new AttendanceSummaryModel(canvasSectionId);
                ret.add(currentSection);
            }

            currentSection.add(new Entry(
                    ((Number) result[0]).longValue(),
                    canvasSectionId,
                    ((Number) result[2]).longValue(),
                    (String) result[3],
                    getBoolean(((Number)result[4]).intValue()),
                    ((Number ) result[5]).intValue(),
                    ((Number ) result[6]).intValue()
            ));
        }

        return ret;
    }

    private boolean getBoolean(int i) {
        return i != 0;
    }

}
