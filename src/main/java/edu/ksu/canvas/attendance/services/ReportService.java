package edu.ksu.canvas.attendance.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.repository.ReportRepository;


@Component
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;


    public List<AttendanceSummaryModel> getMinutedAttendanceSummaryReport(long sectionId) {
        return reportRepository.getMinutedAttendanceSummary(sectionId);
    }

    public List<AttendanceSummaryModel> getSimpleAttendanceSummaryReport(long sectionId) {
        return reportRepository.getSimpleAttendanceSummary(sectionId);
    }

}
