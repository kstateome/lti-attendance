package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Component
public class AttendanceSummaryCSVService {

    /**
    * Formats the data of the Attendance Summary page into a csv format.
    * */
    public  StringBuilder createAttendanceSummaryCsv(boolean isSimpleAttendance, List<AttendanceSummaryModel> summaryForSections) throws IOException {
        //Add headers to the cvs file
        StringBuilder csvStringBuilder = new StringBuilder();
        List<String> headers = isSimpleAttendance ? Arrays.asList("Name", "Total Classes Present", "Total Classes Tardy", "Total Classes Absent", "Total Classes Excused")
                : Arrays.asList("Name", "Total Minutes Missed", "Minutes Made Up", "Minutes To Be Made Up", "% of Course Missed");
        writeLine(csvStringBuilder, headers);

        List<String> row;
        //Adds entries as rows to the cvs
        for (AttendanceSummaryModel model : summaryForSections) {
            for (AttendanceSummaryModel.Entry entry: model.getEntries()) {
                if (!entry.isDropped()) {
                    row = isSimpleAttendance ? Arrays.asList(entry.getStudentName(), entry.getTotalClassesPresent() + "", entry.getTotalClassesTardy() + "", entry.getTotalClassesMissed() + "", + entry.getTotalClassesExcused() + "")
                            : Arrays.asList(entry.getStudentName(), entry.getSumMinutesMissed() + "", entry.getSumMinutesMadeup() + "", entry.getRemainingMinutesMadeup() + "", entry.getPercentCourseMissed() + "");
                    writeLine(csvStringBuilder, row);
                }
            }
        }
        return csvStringBuilder;
    }

    private  void writeLine(StringBuilder sb, List<String> values) throws IOException {
        StringJoiner sj = new StringJoiner('"'+","+'"', '"'+"", '"'+"\n");
        for (String value : values) {
            sj.add(value);
        }
        sb.append(sj.toString());
    }

}
