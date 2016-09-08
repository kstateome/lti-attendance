package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AttendanceSummaryCSVServiceUTest {

    private AttendanceSummaryCSVService attendanceSummaryCSVService;

    private final long ARBITRARY_COURSE = 702111;
    private final long ARBITRARY_SECTION = 705555;
    //Student 1 Data
    private final long ARBITRARY_STUDENT_1 = 111784;
    private final String ARBITRARY_STUDENT_NAME_1 = "Student, Uno";
    private final boolean ARBITRARY_STUDENT_DROPED_1 = false;
    private final int ARBITRARY_STUDENT_TARDY_1 = 5;
    private final int ARBITRARY_STUDENT_MISSED_1 = 2;
    private final int ARBITRARY_STUDENT_TOT_MADEUP_1 = 2;
    private final int ARBITRARY_STUDENT_TOBE_MADEUP_1 = 3;
    private final int ARBITRARY_STUDENT_MIN_MISSED_1 = 50;
    private final double ARBITRARY_STUDENT_PERCENT_1 = 2.5;
    //Student 2 Data
    private final long ARBITRARY_STUDENT_2 = 222784;
    private final String ARBITRARY_STUDENT_NAME_2 = "Student, Dos";
    private final boolean ARBITRARY_STUDENT_DROPED_2 = false;
    private final int ARBITRARY_STUDENT_TARDY_2 = 1;
    private final int ARBITRARY_STUDENT_MISSED_2 = 5;
    private final int ARBITRARY_STUDENT_TOT_MADEUP_2 = 0;
    private final int ARBITRARY_STUDENT_TOBE_MADEUP_2 = 6;
    private final int ARBITRARY_STUDENT_MIN_MISSED_2 = 100;
    private final double ARBITRARY_STUDENT_PERCENT_2 = 50.1;
    //Student 3 Data (dropped)
    private final long ARBITRARY_STUDENT_3 = 222784;
    private final String ARBITRARY_STUDENT_NAME_3 = "Student, Tres";
    private final boolean ARBITRARY_STUDENT_DROPED_3 = true;
    private final int ARBITRARY_STUDENT_TARDY_3 = 1;
    private final int ARBITRARY_STUDENT_MISSED_3 = 5;
    private final int ARBITRARY_STUDENT_TOT_MADEUP_3 = 0;
    private final int ARBITRARY_STUDENT_TOBE_MADEUP_3 = 6;
    private final int ARBITRARY_STUDENT_MIN_MISSED_3 = 100;
    private final double ARBITRARY_STUDENT_PERCENT_3 = 50.1;


    @Before
    public void setup() {
        attendanceSummaryCSVService = new AttendanceSummaryCSVService();
    }

    @Test
    public void createCsvSimpleAttendanceHappyPath() throws IOException {

        List<AttendanceSummaryModel> attendanceSummaryModelList = generateSimpleSummaryModelList();
        boolean isSimpleAttendance = true;

        StringBuilder shouldGetThis = new StringBuilder();
        shouldGetThis.append("\"Name\",\"Total Classes Absent\",\"Total Classes Tardy\"\n");
        shouldGetThis.append("\""+ARBITRARY_STUDENT_NAME_1 +"\",\""+ARBITRARY_STUDENT_MISSED_1+"\",\""+ARBITRARY_STUDENT_TARDY_1+"\"\n");
        shouldGetThis.append("\""+ARBITRARY_STUDENT_NAME_2 +"\",\""+ARBITRARY_STUDENT_MISSED_2+"\",\""+ARBITRARY_STUDENT_TARDY_2+"\"\n");

        StringBuilder returned = attendanceSummaryCSVService.createAttendanceSummaryCsv(isSimpleAttendance, attendanceSummaryModelList);
        assertEquals(shouldGetThis.toString(), returned.toString());
    }

    @Test
    public void createCsvAviationAttendanceHappyPath() throws IOException {

        List<AttendanceSummaryModel> attendanceSummaryModelList = generateAviationSummaryModelList();
        boolean isSimpleAttendance = false;

        StringBuilder shouldGetThis = new StringBuilder();
        shouldGetThis.append("\"Name\",\"Total Minutes Missed\",\"Minutes Made Up\",\"Minutes To Be Made Up\",\"% of Course Missed\"\n");
        shouldGetThis.append("\""+ARBITRARY_STUDENT_NAME_1+"\",\""+ARBITRARY_STUDENT_MIN_MISSED_1+"\",\""+ARBITRARY_STUDENT_TOT_MADEUP_1+"\",\""+ARBITRARY_STUDENT_TOBE_MADEUP_1+"\",\""+ARBITRARY_STUDENT_PERCENT_1+"\"\n");
        shouldGetThis.append("\""+ARBITRARY_STUDENT_NAME_2+"\",\""+ARBITRARY_STUDENT_MIN_MISSED_2+"\",\""+ARBITRARY_STUDENT_TOT_MADEUP_2+"\",\""+ARBITRARY_STUDENT_TOBE_MADEUP_2+"\",\""+ARBITRARY_STUDENT_PERCENT_2+"\"\n");

        StringBuilder returned = attendanceSummaryCSVService.createAttendanceSummaryCsv(isSimpleAttendance, attendanceSummaryModelList);
        assertEquals(shouldGetThis.toString(), returned.toString());
    }

    private List<AttendanceSummaryModel> generateSimpleSummaryModelList() {
        AttendanceSummaryModel.Entry entry1 = new AttendanceSummaryModel.Entry(ARBITRARY_COURSE, ARBITRARY_SECTION, ARBITRARY_STUDENT_1,
                ARBITRARY_STUDENT_NAME_1, ARBITRARY_STUDENT_DROPED_1, ARBITRARY_STUDENT_TARDY_1, ARBITRARY_STUDENT_MISSED_1);
        AttendanceSummaryModel.Entry entry2 = new AttendanceSummaryModel.Entry(ARBITRARY_COURSE, ARBITRARY_SECTION, ARBITRARY_STUDENT_2,
                ARBITRARY_STUDENT_NAME_2, ARBITRARY_STUDENT_DROPED_2, ARBITRARY_STUDENT_TARDY_2, ARBITRARY_STUDENT_MISSED_2);
        AttendanceSummaryModel.Entry entry3 = new AttendanceSummaryModel.Entry(ARBITRARY_COURSE, ARBITRARY_SECTION, ARBITRARY_STUDENT_3,
                ARBITRARY_STUDENT_NAME_3, ARBITRARY_STUDENT_DROPED_3, ARBITRARY_STUDENT_TARDY_3, ARBITRARY_STUDENT_MISSED_3);

        AttendanceSummaryModel attendanceSummaryModel = new AttendanceSummaryModel(ARBITRARY_SECTION);
        attendanceSummaryModel.add(entry1);
        attendanceSummaryModel.add(entry2);
        attendanceSummaryModel.add(entry3);

        List<AttendanceSummaryModel> attendanceSummaryModelList = new ArrayList<>();
        attendanceSummaryModelList.add(attendanceSummaryModel);
        return attendanceSummaryModelList;
    }

    private List<AttendanceSummaryModel> generateAviationSummaryModelList() {
        AttendanceSummaryModel.Entry entry1 = new AttendanceSummaryModel.Entry(ARBITRARY_COURSE, ARBITRARY_SECTION, ARBITRARY_STUDENT_1,
                ARBITRARY_STUDENT_NAME_1, ARBITRARY_STUDENT_DROPED_1, ARBITRARY_STUDENT_TOT_MADEUP_1, ARBITRARY_STUDENT_TOBE_MADEUP_1, ARBITRARY_STUDENT_MIN_MISSED_1, ARBITRARY_STUDENT_PERCENT_1);
        AttendanceSummaryModel.Entry entry2 = new AttendanceSummaryModel.Entry(ARBITRARY_COURSE, ARBITRARY_SECTION, ARBITRARY_STUDENT_2,
                ARBITRARY_STUDENT_NAME_2, ARBITRARY_STUDENT_DROPED_2, ARBITRARY_STUDENT_TOT_MADEUP_2, ARBITRARY_STUDENT_TOBE_MADEUP_2, ARBITRARY_STUDENT_MIN_MISSED_2, ARBITRARY_STUDENT_PERCENT_2);
        AttendanceSummaryModel.Entry entry3 = new AttendanceSummaryModel.Entry(ARBITRARY_COURSE, ARBITRARY_SECTION, ARBITRARY_STUDENT_3,
                ARBITRARY_STUDENT_NAME_3, ARBITRARY_STUDENT_DROPED_3, ARBITRARY_STUDENT_TOT_MADEUP_3, ARBITRARY_STUDENT_TOBE_MADEUP_3, ARBITRARY_STUDENT_MIN_MISSED_3, ARBITRARY_STUDENT_PERCENT_3);

        AttendanceSummaryModel attendanceSummaryModel = new AttendanceSummaryModel(ARBITRARY_SECTION);
        attendanceSummaryModel.add(entry1);
        attendanceSummaryModel.add(entry2);
        attendanceSummaryModel.add(entry3);

        List<AttendanceSummaryModel> attendanceSummaryModelList = new ArrayList<>();
        attendanceSummaryModelList.add(attendanceSummaryModel);
        return attendanceSummaryModelList;
    }

}
