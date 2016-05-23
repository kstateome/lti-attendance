package edu.ksu.canvas.aviation.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ksu.canvas.aviation.model.AttendanceSummaryForSectionModel;
import edu.ksu.canvas.aviation.repository.ReportRepository;


@Component
public class ReportService {

    
    @Autowired
    private ReportRepository reportRepository;
    
    
    public List<AttendanceSummaryForSectionModel> getAttendanceSummaryReport(Long sectionId) {
        return reportRepository.getAttendanceSummary(new Long(sectionId));
    }
}
