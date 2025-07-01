package com.quantumsoft.hrms.serviceimpl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.quantumsoft.hrms.dto.ClockInInput;
import com.quantumsoft.hrms.dto.ReportInput;
import com.quantumsoft.hrms.entity.Attendance;
import com.quantumsoft.hrms.entity.Employee;
import com.quantumsoft.hrms.entity.LeaveRequest;
import com.quantumsoft.hrms.enums.AttendanceStatus;
import com.quantumsoft.hrms.enums.LeaveStatus;
import com.quantumsoft.hrms.exception.ResourceNotFoundException;
import com.quantumsoft.hrms.repository.AttendanceRepository;
import com.quantumsoft.hrms.repository.EmployeeRepository;
import com.quantumsoft.hrms.repository.LeaveRequestRepository;
import com.quantumsoft.hrms.servicei.AttendanceServiceI;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class AttendanceImpl implements AttendanceServiceI {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /* getDistance method is used to calculate distance between office location and employee current location
       by providing employee current location as input.
     * If we want to calculate distance between two points on earth then use Haversine formula.
     * Here we need to provide employee current location and then we get distance between
    */
    public double getDistance(String location){
        String officeLocation = "Latitude: 18.5204303, Longitude: 73.8567437";
        String officeLatitude = officeLocation.split(",")[0].split(":")[1].trim();
        String officeLongitude = officeLocation.split(",")[1].split(":")[1].trim();
        double officeLat = Double.parseDouble(officeLatitude);
        double officeLong = Double.parseDouble(officeLongitude);

        String latitude = location.split(",")[0].split(":")[1].trim();
        String longitude = location.split(",")[1].split(":")[1].trim();
        double empLat = Double.parseDouble(latitude);
        double empLong = Double.parseDouble(longitude);

        /* String operation explanation:
           "Latitude: 18.5204303, Longitude: 73.8567437" -> [Latitude: 18.5204303, Longitude: 73.8567437]
           -> "Latitude: 18.5204303" -> [Latitude, 18.5204303] -> 18.5204303
        */

        double dLat = Math.toRadians(officeLat - empLat);
        double dLon = Math.toRadians(officeLong - empLong);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(empLat)) * Math.cos(Math.toRadians(officeLat))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = 6371000 * c; // Earth's average radius in meters = 6371000
        return distance;
    }

    /* First we are checking the clockIn time, if the clockIn time is after the absentTime then mark as absent.
       Then we check if workingFrom is OFFICE and mode is WEB or MOBILE then for clockIn, employee must be within
       office radius i.e within 100m and we are creating Attendance object and saving it in database, if employee
       not in within office location then it display "Your are outside the office location, please be within
       the office radius for clockIn"
       If Employee try to clock in multiple times on same day then we are sending message "you are already
       clock in"
    */

    @Override
    public String clockIn(ClockInInput clockInInput) {
        LocalTime absentTime = LocalTime.parse("11:00:00");
        LocalTime officeStartTime = LocalTime.parse("09:00:00");
        LocalTime officeEndTime = LocalTime.parse("18:00:00");

        // here we are checking if employee has clocked in after 11 AM or not
        if(LocalTime.now().isAfter(absentTime)){
            Employee employee = employeeRepository.findById(clockInInput.getEmployeeId()).orElseThrow(() -> new ResourceNotFoundException("Employee record not found in database."));
            if(clockInInput.getWorkingFrom().name().equals("OFFICE") && (clockInInput.getMode().name().equals("WEB") || clockInInput.getMode().name().equals("MOBILE"))){
                Double distance = getDistance(clockInInput.getLocation());
                System.out.println(distance);
                if(distance>100){
                    return "Your are outside the office location, please be within the office radius for clockIn";
                }
            }
            Attendance attendance = new Attendance();
            attendance.setClockInTime(LocalTime.now());
            attendance.setEmployee(employee);
            attendance.setWorkingFrom(clockInInput.getWorkingFrom());
            attendance.setMode(clockInInput.getMode());
            attendance.setLocation(clockInInput.getLocation());
            attendance.setTimesheetProjectCode("HR-Att-11");
            attendance.setStatus(AttendanceStatus.ABSENT);

            Map<String, String> notification = new HashMap<>();
            notification.put("message", employee.getFirstName() + " " + employee.getLastName() + " is clocked in at " + LocalDateTime.now());

            simpMessagingTemplate.convertAndSend( "/topic/notifications", notification);

            attendanceRepository.save(attendance);

            return "You are clocked in successfully and mark as ABSENT";
        } else {
            Optional<Attendance> attendanceOptional = attendanceRepository.findByDate(LocalDate.now());
            if (attendanceOptional.isEmpty()) {
                if(clockInInput.getWorkingFrom().name().equals("OFFICE") && (clockInInput.getMode().name().equals("WEB") || clockInInput.getMode().name().equals("MOBILE"))){
                    double distance = getDistance(clockInInput.getLocation());
                    if(distance>100){
                        return "Your are outside the office location";
                    }
                }
                Employee employee = employeeRepository.findById(clockInInput.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee record not found in database."));
                Attendance attendance = new Attendance();
                attendance.setClockInTime(LocalTime.now());
                // Here we are checking employee has clocked in late or not and setting lateBy time accordingly
                if(attendance.getClockInTime().isAfter(officeStartTime)){
                    Duration duration = Duration.between(officeStartTime, attendance.getClockInTime());
                    int hours = (int)duration.toHours();
                    int minutes = (int)duration.toMinutes()%60;
                    LocalTime lateBy = LocalTime.of(hours, minutes);
                    attendance.setLateBy(lateBy);
                }
                attendance.setEmployee(employee);
                attendance.setWorkingFrom(clockInInput.getWorkingFrom());
                attendance.setMode(clockInInput.getMode());
                attendance.setLocation(clockInInput.getLocation());
                attendance.setTimesheetProjectCode("HRMS");
                attendance.setStatus(AttendanceStatus.PRESENT);

                Map<String, String> notification = new HashMap<>();
                notification.put("message", employee.getFirstName() + " " + employee.getLastName() + " is clocked in at " + LocalDateTime.now());

                simpMessagingTemplate.convertAndSend( "/topic/notifications", notification);

                attendanceRepository.save(attendance);
                return "Clocked in successfully...!";
            } else
                return "You are already clocked in....!";
        }
    }

    /* When Employee clockIn then only employee can see clockOut button.
       Here we are going to check by Date whether Attendance record is present or not.
       If employee has clocked in then there must be Attendance record present in database */

    @Override
    public String clockOut() {
        LocalTime officeEndTime = LocalTime.parse("18:00:00");
        Attendance attendance = attendanceRepository.findByDate(LocalDate.now()).orElseThrow(() -> new ResourceNotFoundException("Because of technical reason Attendance record had not saved. Please check the code."));
        Employee employee = attendance.getEmployee();

        if(attendance.getStatus().name().equals("ABSENT")) {
            attendance.setClockOutTime(LocalTime.now());
            attendanceRepository.save(attendance);

            Map<String, String> notification = new HashMap<>();
            notification.put("message", employee.getFirstName() + " " + employee.getLastName() + " is clocked out at " + LocalDateTime.now());

            simpMessagingTemplate.convertAndSend( "/topic/notifications", notification);

            return "You have clocked out successfully but as your had clocked in after 11AM, so your working hours are not considered";
        }else{
            attendance.setClockOutTime(LocalTime.now());

            if(attendance.getClockInTime().isBefore(officeEndTime)){
                Duration duration = Duration.between(attendance.getClockOutTime(), officeEndTime);
                int hours = (int)duration.toHours();
                int minutes = (int)duration.toMinutes()%60;
                LocalTime earlyBy = LocalTime.of(hours, minutes);
                attendance.setEarlyBy(earlyBy);
            }

            Duration duration = Duration.between(attendance.getClockInTime(), attendance.getClockOutTime());
            LocalTime workingHours = LocalTime.of((int) duration.toHours(), (int) duration.toMinutes() % 60, (int) duration.toSeconds() % 60);
            // duration.toMinutes() % 60 if we not done this then it will give whole duration in minutes
            // e.g duration is 2 hours then it will give 120 minutes

            attendance.setWorkingHours(workingHours);

            attendanceRepository.save(attendance);

            Map<String, String> notification = new HashMap<>();
            notification.put("message", employee.getFirstName() + " " + employee.getLastName() + " is clocked out at " + LocalDateTime.now());

            simpMessagingTemplate.convertAndSend( "/topic/notifications", notification);

            return "You have ClockOut successfully..!";
        }
    }

    @Override
    public Resource generatePdfReport(ReportInput reportInput) throws FileNotFoundException {
        List<Attendance> attendances = attendanceRepository.findByDateBetween(reportInput.getFromDate(), reportInput.getToDate());

        List<Attendance> list = attendances.stream().filter(a -> a.getEmployee().getEmpId().equals(reportInput.getEmpId())).toList();

        File dir = new File("attendance_reports/pdf");
        if (!dir.exists()) dir.mkdirs();

        Employee employee = employeeRepository.findById(reportInput.getEmpId()).orElseThrow(() -> new ResourceNotFoundException("Employee record not found in database"));

        String fileName = "attendance_report_" + employee.getFirstName().toLowerCase() + "_" + employee.getLastName().toLowerCase() + "_from_" + reportInput.getFromDate() + "_to_" + reportInput.getToDate() + ".pdf";
        String filePath = "attendance_reports/pdf" + File.separator + fileName;

        File file = new File(filePath);

        PdfWriter writer = new PdfWriter(new FileOutputStream(file));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Attendance Report")
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(18));

        Table table = new Table(10);
        table.setWidth(UnitValue.createPercentValue(90));               // table spans 90% of page width
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);       // center table block
        table.setTextAlignment(TextAlignment.CENTER);

        table.addHeaderCell("Date");
        table.addHeaderCell("Employee");
        table.addHeaderCell("Department");
        table.addHeaderCell("Working From");
        table.addHeaderCell("Status");
        table.addHeaderCell("Clock In");
        table.addHeaderCell("Clock Out");
        table.addHeaderCell("LateBy");
        table.addHeaderCell("EarlyBy");
        table.addHeaderCell("Working Hours");

        for (Attendance a : list) {
            table.addCell(a.getDate().toString());
            table.addCell(a.getEmployee().getFirstName() + " " + a.getEmployee().getLastName());
            table.addCell(a.getEmployee().getDepartment().getName());
            table.addCell(a.getWorkingFrom().name());
            table.addCell(a.getStatus().name());
            table.addCell(a.getClockInTime().toString());
            table.addCell(a.getClockOutTime().toString());
            table.addCell(a.getLateBy().toString());
            table.addCell(a.getEarlyBy().toString());
            table.addCell(a.getWorkingHours().toString());
        }

        document.add(table);
        document.close();

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return resource;
    }

    @Override
    public Resource generateCsvReport(ReportInput reportInput) throws IOException {
        List<Attendance> attendances = attendanceRepository.findByDateBetween(reportInput.getFromDate(), reportInput.getToDate());

        List<Attendance> list = attendances.stream().filter(a -> a.getEmployee().getEmpId().equals(reportInput.getEmpId())).toList();

        Path csvDir = Paths.get("attendance_reports", "csv");
        if (Files.notExists(csvDir)) {
            Files.createDirectories(csvDir);
        }

        Employee employee = employeeRepository.findById(reportInput.getEmpId()).orElseThrow(() -> new ResourceNotFoundException("Employee record not found in database"));

        String fileName = "attendance_report_" + employee.getFirstName().toLowerCase() + "_" + employee.getLastName().toLowerCase() + "_from_" + reportInput.getFromDate() + "_to_" + reportInput.getToDate() + ".csv";
        Path csvFile = csvDir.resolve(fileName);

        try (
                PrintWriter pw = new PrintWriter(Files.newBufferedWriter(csvFile));
                CSVPrinter csvPrinter = new CSVPrinter(pw, CSVFormat.DEFAULT
                        .withHeader("Date", "Employee", "Department", "Working From", "status", "Clock In", "Clock Out", "LateBy", "EarlyBy", "Working Hours"));
        ){
            for (Attendance a : list) {
                csvPrinter.printRecord(
                        a.getDate(),
                        a.getEmployee().getFirstName() + " " + a.getEmployee().getLastName(),
                        a.getEmployee().getDepartment().getName(),
                        a.getWorkingFrom().name(),
                        a.getStatus().name(),
                        a.getClockInTime(),
                        a.getClockOutTime(),
                        a.getLateBy(),
                        a.getEarlyBy(),
                        a.getWorkingHours()
                );
            }
            csvPrinter.flush();
            return new InputStreamResource(Files.newInputStream(csvFile));
       }
    }

    @Override
    public List<Attendance> fetchMonthlyAttendanceStatus(LocalDate fromDate, LocalDate toDate, UUID empId) {

        /*List<LeaveRequest> employeeLeaves = leaveRequestRepository.findByEmployee_empIdAndStatus(empId, LeaveStatus.APPROVED);
        List<LeaveRequest> leaveInParticularMonths = employeeLeaves.stream().filter(l -> l.getMonth().equals(fromDate.getMonth().toString())).toList();*/
        
        List<Attendance> byDateBetween = attendanceRepository.findByDateBetween(fromDate, toDate);
        return byDateBetween.stream().filter(a -> a.getEmployee().getEmpId().equals(empId)).toList();
    }
}
