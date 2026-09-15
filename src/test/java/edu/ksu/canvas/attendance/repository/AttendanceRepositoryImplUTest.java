package edu.ksu.canvas.attendance.repository;

import edu.ksu.canvas.attendance.entity.Attendance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AttendanceRepositoryImplUTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<AttendanceCommentEntry> attendanceCommentEntryQuery;

    private AttendanceRepositoryImpl attendanceRepository;


    @Before
    public void setup() {
        attendanceRepository = new AttendanceRepositoryImpl();
        ReflectionTestUtils.setField(attendanceRepository, "entityManager", entityManager);
    }


    @Test(expected=NullPointerException.class)
    public void getAttendanceByCourseAndDayOfClass_NullDateOfClass() {
        int irrelevantCourseId = 2;
        Date nullDateOfClass = null;

        attendanceRepository.getAttendanceByCourseAndDayOfClass(irrelevantCourseId, nullDateOfClass);
    }


    @Test
    public void saveInBatches_tollerateNullAttendances() {
        List<Attendance> nullAttendances = null;

        attendanceRepository.saveInBatches(nullAttendances);
    }

    @Test(expected=NullPointerException.class)
    public void deleteAttendanceByCourseAndDayOfClass_NullDateOfClass() {
        int irrelevantCourseId = 2;
        Date nullDateOfClass = null;
        long irrelevantSectionId = 2;

        attendanceRepository.deleteAttendanceByCourseAndDayOfClass(irrelevantCourseId, nullDateOfClass, irrelevantSectionId);
    }

    @Test
    public void getAttendanceCommentsBySectionId_groupsCommentsByStudent() {
        long sectionId = 101L;
        Date firstDate = date(2024, Calendar.JANUARY, 15);
        Date secondDate = date(2024, Calendar.JANUARY, 16);

        when(entityManager.createQuery(anyString(), eq(AttendanceCommentEntry.class)))
                .thenReturn(attendanceCommentEntryQuery);
        when(attendanceCommentEntryQuery.getResultList()).thenReturn(Arrays.asList(
                new AttendanceCommentEntry(42L, firstDate, "First comment"),
                new AttendanceCommentEntry(42L, secondDate, "Second comment"),
                new AttendanceCommentEntry(7L, firstDate, "Other student comment")
        ));

        Map<Long, String> commentsByStudent = attendanceRepository.getAttendanceCommentsBySectionId(sectionId);

        assertEquals(2, commentsByStudent.size());
        assertEquals("01/15/2024: First comment\n01/16/2024: Second comment\n", commentsByStudent.get(42L));
        assertEquals("01/15/2024: Other student comment\n", commentsByStudent.get(7L));
    }

    @Test
    public void getAttendanceCommentsBySectionId_returnsEmptyMapWhenQueryFails() {
        long sectionId = 202L;

        when(entityManager.createQuery(anyString(), eq(AttendanceCommentEntry.class)))
                .thenReturn(attendanceCommentEntryQuery);
        when(attendanceCommentEntryQuery.getResultList()).thenThrow(new PersistenceException("query failed"));

        Map<Long, String> commentsByStudent = attendanceRepository.getAttendanceCommentsBySectionId(sectionId);

        assertTrue(commentsByStudent.isEmpty());
    }

    private Date date(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, dayOfMonth);
        return calendar.getTime();
    }
}
