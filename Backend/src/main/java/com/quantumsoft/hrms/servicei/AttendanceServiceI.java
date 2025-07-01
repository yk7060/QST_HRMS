package com.quantumsoft.hrms.servicei;

import com.quantumsoft.hrms.dto.ClockInInput;
import com.quantumsoft.hrms.dto.ReportInput;
import com.quantumsoft.hrms.entity.Attendance;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AttendanceServiceI {

   public String clockIn(ClockInInput clockInInput);

   public String clockOut();

   public Resource generatePdfReport(ReportInput reportInput) throws FileNotFoundException;

   public Resource generateCsvReport(ReportInput reportInput) throws IOException;

   public List<Attendance> fetchMonthlyAttendanceStatus(LocalDate fromDate, LocalDate toDate, UUID empId);
}
