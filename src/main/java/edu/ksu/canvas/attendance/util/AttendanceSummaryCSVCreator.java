package edu.ksu.canvas.attendance.util;

import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by jesusorr on 9/7/16.
 */
public class AttendanceSummaryCSVCreator {

    private AttendanceSummaryCSVCreator() {
        // This is an util class, so it doesn't need a public constructor
    }

    /**
    * Formats the data of the Attendance Summary page into a csv format.
    * */
    public static StringBuilder createAttendanceSummaryCsv(boolean isSimpleAttendance, List<AttendanceSummaryModel> summaryForSections) throws IOException {
        //Add headers to the cvs file
        StringBuilder csvStringBuilder = new StringBuilder();
        List<String> headers = isSimpleAttendance ? Arrays.asList("Name", "Total Classes Absent", "Total Classes Tardy")
                : Arrays.asList("Name", "Total Minutes Missed", "Minutes Made Up", "Minutes To Be Made Up", "% of Course Missed");
        writeLine(csvStringBuilder, headers);

        List<String> row;
        //Adds entries as rows to the cvs
        for (AttendanceSummaryModel model : summaryForSections) {
            for (AttendanceSummaryModel.Entry entry: model.getEntries()) {
                if (!entry.isDropped()) {
                    row = isSimpleAttendance ? Arrays.asList(entry.getStudentName(), entry.getTotalClassesMissed() + "", entry.getTotalClassesTardy() + "")
                            : Arrays.asList(entry.getStudentName(), entry.getSumMinutesMissed() + "", entry.getSumMinutesMadeup() + "", entry.getRemainingMinutesMadeup() + "", entry.getPercentCourseMissed() + "");
                    writeLine(csvStringBuilder, row);
                }
            }
        }
        return csvStringBuilder;
    }

    private static void writeLine(StringBuilder sb, List<String> values) throws IOException {
        StringJoiner sj = new StringJoiner('"'+","+'"', '"'+"", '"'+"\n");
        for (String value : values) {
            sj.add(value);
        }
        sb.append(sj.toString());
    }

}
