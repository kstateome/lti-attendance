package edu.ksu.canvas.attendance.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.repository.ReportRepository;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class ReportServiceUTest {

    private ReportService reportService;
    
    @Mock
    private ReportRepository mockReportRepository;
    
    
    @Before
    public void setup() {
        reportService = new ReportService();
        ReflectionTestUtils.setField(reportService, "reportRepository", mockReportRepository);
    }
    
    
    @Test
    public void getAviationAttendanceSummaryReport_HappyPath() {
        long sectionId = 5L;
        List<AttendanceSummaryModel> expected = new ArrayList<>();
        
        when(mockReportRepository.getAviationAttendanceSummary(5)).thenReturn(expected);
        List<AttendanceSummaryModel> actual = reportService.getAviationAttendanceSummaryReport(sectionId);
        
        assertSame(expected, actual);
    }

    @Test
    public void getSimpleAttendanceSummaryReport_HappyPath() {
        long sectionId = 5L;
        List<AttendanceSummaryModel> expected = new ArrayList<>();

        when(mockReportRepository.getSimpleAttendanceSummary(5)).thenReturn(expected);
        List<AttendanceSummaryModel> actual = reportService.getSimpleAttendanceSummaryReport(sectionId);

        assertSame(expected, actual);
    }

}
