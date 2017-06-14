package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;


    public List<AttendanceSummaryModel> getAviationAttendanceSummaryReport(long sectionId) {
        return reportRepository.getAviationAttendanceSummary(sectionId);
    }

    public List<AttendanceSummaryModel> getSimpleAttendanceSummaryReport(long sectionId) {
        return reportRepository.getSimpleAttendanceSummary(sectionId);
    }

}
