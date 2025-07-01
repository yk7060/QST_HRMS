package com.quantumsoft.hrms.serviceimpl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.quantumsoft.hrms.dto.PayrollInput;
import com.quantumsoft.hrms.dto.PayslipDto;
import com.quantumsoft.hrms.entity.Attendance;
import com.quantumsoft.hrms.entity.Payroll;
import com.quantumsoft.hrms.entity.SalaryStructure;
import com.quantumsoft.hrms.enums.AttendanceStatus;
import com.quantumsoft.hrms.enums.PaymentStatus;
import com.quantumsoft.hrms.enums.SalaryStructureStatus;
import com.quantumsoft.hrms.exception.ResourceNotFoundException;
import com.quantumsoft.hrms.repository.*;
import com.quantumsoft.hrms.servicei.PayrollServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class PayrollServiceImpl implements PayrollServiceI {

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private SalaryStructureRepository salaryStructureRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Override
    public SalaryStructure fetchActiveSalaryStructure(UUID empId) {
        return salaryStructureRepository.findByEmployee_empIdAndStatus(empId, SalaryStructureStatus.ACTIVE).orElseThrow(() -> new ResourceNotFoundException("Salary Structure record with given empId is not found in database"));
    }

    @Override
    public String calculateMonthlySalary(PayrollInput payrollInput) {

        String month = payrollInput.getToDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
        String year = String.valueOf(payrollInput.getToDate().getYear());

        /* We are fetching payroll record using month and year so that we can check whether payroll record is already created
           for given month and year or not */
        Optional<Payroll> payrollOptional = payrollRepository.findByMonthAndYear(month, year);
        if(payrollOptional.isEmpty()) {
            // All employees attendance records between start date and end date
            List<Attendance> allEmployeesAttendanceRecords = attendanceRepository.findByDateBetween(payrollInput.getFromDate(), payrollInput.getToDate());
            // Particular employee Attendance record
            List<Attendance> employeeAttendanceRecords = allEmployeesAttendanceRecords.stream().filter(a -> a.getEmployee().getEmpId().equals(payrollInput.getEmpId())).toList();
            // Days on which employee is PRESENT
            List<Attendance> employeesPresentDays = employeeAttendanceRecords.stream().filter(a -> a.getStatus().equals(AttendanceStatus.PRESENT)).toList();
            /* Extracting working hours in minutes which are not null
            *  Sometimes employee clocked in on time but forgot to clock out so in this case employee's attendance status remain PRESENT but
            *  as employee has not clocked out so his/her working hours has not get calculated. So working hours remain NULL
            *  That's why we are using following condition in filter() method */
            List<Attendance> attendanceRecordsWithWorkingHoursAreNotNull = employeesPresentDays.stream().filter(a -> a.getWorkingHours() != null).toList();
            double employeesWorkingDays = attendanceRecordsWithWorkingHoursAreNotNull.size();
            /* converting working hours of all attendance records between start date and end date into minutes
            *  This will give total working minutes of employee from start date to end date */
            double totalWorkingHoursInMinutes = attendanceRecordsWithWorkingHoursAreNotNull.stream().mapToDouble(a -> a.getWorkingHours().getMinute()).sum();
            /* Every employee has his/her own salary structure, we are fetching it for calculating total earnings, total deductions
            *  and net salary */
            System.out.println("total working hours in minutes: " + totalWorkingHoursInMinutes);
            SalaryStructure salaryStructure = salaryStructureRepository.findByEmployee_empIdAndStatus(payrollInput.getEmpId(), SalaryStructureStatus.ACTIVE).orElseThrow(() -> new ResourceNotFoundException("Salary Structure record with given empId is not found in database"));

            double totalEarnings = salaryStructure.getBasicSalary() + salaryStructure.getHra()
                    + salaryStructure.getBonus() + salaryStructure.getSpecialAllowance();
            double totalDeductions = salaryStructure.getPfDeduction() + salaryStructure.getTaxDeduction();

            double netSalary = totalEarnings - totalDeductions;

            System.out.println("net salary: " + netSalary);

            double perDaySalary = netSalary / 30; // considering 30 days in between startDate and endDate

            double perHourSalary = perDaySalary / 9; // considering 9 working hours in a day

            double perMinuteSalary = perHourSalary / 60;

            System.out.println("perMinuteSalary: " + perMinuteSalary);

            netSalary = perMinuteSalary * totalWorkingHoursInMinutes; //

            Payroll payroll = new Payroll();
            payroll.setSalaryStructure(salaryStructure);
            payroll.setEmployee(salaryStructure.getEmployee());
            payroll.setTotalEarnings(totalEarnings);
            payroll.setTotalDeductions(totalDeductions);
            payroll.setNetSalary(netSalary);
            payroll.setMonth(payrollInput.getToDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase());
            payroll.setYear(String.valueOf(payrollInput.getToDate().getYear()));
            payroll.setGeneratedDate(LocalDate.now());
            payroll.setPaymentStatus(PaymentStatus.PENDING);
            payroll.setWorkingDays(employeesWorkingDays);
            payroll.setWorkingHoursInMinutes(totalWorkingHoursInMinutes);

            payrollRepository.save(payroll);

            return "Payroll record is saved successfully...";
        }else
            return "Payroll record for month " + month + " " + year + " is already created.";
    }

    @Override
    public String generatePayslip(UUID empId, String month) throws FileNotFoundException {
        Payroll payroll = payrollRepository.findByEmployee_empIdAndMonth(empId, month).orElseThrow(() -> new ResourceNotFoundException("Payroll record not found in database"));

        File dir = new File("payslips");
        if (!dir.exists()) dir.mkdirs();

        String fileName = "payslip_" + payroll.getMonth() + "_" + payroll.getYear() + "_"
                           + payroll.getEmployee().getFirstName().toLowerCase() + "_"
                           + payroll.getEmployee().getLastName().toLowerCase() + ".pdf";
        String filePath = "payslips" + File.separator + fileName;

        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Quantumsoft Technologies Private Limited")
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        document.add(new Paragraph("Payslip for the month of " + payroll.getMonth() + " " + payroll.getYear())
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(new Cell().add(new Paragraph("Name").setFontSize(10)));
        table.addCell(String.valueOf(payroll.getEmployee().getFirstName() + " " + payroll.getEmployee().getLastName()));

        table.addCell(new Cell().add(new Paragraph("Employee ID").setFontSize(10)));
        table.addCell(String.valueOf(payroll.getEmployee().getEmpId()));

        table.addCell(new Cell().add(new Paragraph("Department").setFontSize(10)));
        table.addCell(String.valueOf(payroll.getEmployee().getDepartment().getName()));

        table.addCell(new Cell().add(new Paragraph("Basic Salary").setFontSize(10)));
        table.addCell("₹" + payroll.getSalaryStructure().getBasicSalary());

        table.addCell(new Cell().add(new Paragraph("HRA").setFontSize(10)));
        table.addCell(String.valueOf(payroll.getSalaryStructure().getHra()));

        table.addCell(new Cell().add(new Paragraph("Special Allowance").setFontSize(10)));
        table.addCell(String.valueOf(payroll.getSalaryStructure().getSpecialAllowance()));

        table.addCell(new Cell().add(new Paragraph("Bonus").setFontSize(10)));
        table.addCell(String.valueOf(payroll.getSalaryStructure().getBonus()));

        table.addCell(new Cell().add(new Paragraph("PF Deduction").setFontSize(10)));
        table.addCell(String.valueOf(payroll.getSalaryStructure().getPfDeduction()));

        table.addCell(new Cell().add(new Paragraph("Tax Deduction").setFontSize(10)));
        table.addCell(String.valueOf(payroll.getSalaryStructure().getTaxDeduction()));

        table.addCell(new Cell().add(new Paragraph("TotalEarnings").setFontSize(10)));
        table.addCell(String.valueOf(payroll.getTotalEarnings()));

        table.addCell(new Cell().add(new Paragraph("Total Deductions").setFontSize(10)));
        table.addCell(String.valueOf(payroll.getTotalDeductions()));

        table.addCell(new Cell().add(new Paragraph("Working Days").setFontSize(10)));
        table.addCell(String.valueOf(payroll.getWorkingDays()));

        table.addCell(new Cell().add(new Paragraph("Total Working Hours In Minutes").setFontSize(10)));
        table.addCell(String.valueOf(payroll.getWorkingHoursInMinutes()));

        table.addCell(new Cell().add(new Paragraph("Net Salary").setFontSize(10)));
        table.addCell(String.valueOf(payroll.getNetSalary()));

        document.add(table);

        document.add(new Paragraph("This is system generated payslip and does not requires signature")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20));

        document.close();
        payroll.setPayslipPath(filePath);
        payrollRepository.save(payroll);

        return "Payslip is generated...";
    }

    @Override
    public Resource downloadPayslip(UUID empId, String month) throws FileNotFoundException {
        Payroll payroll = payrollRepository.findByEmployee_empIdAndMonth(empId, month).orElseThrow(() -> new ResourceNotFoundException("Payroll record not found in database"));
        if(!payroll.getPayslipPath().isEmpty()) {
            File file = new File(payroll.getPayslipPath());
            return new InputStreamResource(new FileInputStream(file));
        }else
            throw new ResourceNotFoundException("Payslip cannot be downloaded because it is not generated by ADMIN yet");
    }

    @Override
    public Resource downloadPayslip(String username, String month) throws FileNotFoundException {
        Payroll payroll = payrollRepository.findByEmployee_User_usernameAndMonth(username, month).orElseThrow(() -> new ResourceNotFoundException("Payroll record not found in database"));
        if(!payroll.getPayslipPath().isEmpty()) {
            File file = new File(payroll.getPayslipPath());
            return new InputStreamResource(new FileInputStream(file));
        }else
         throw new ResourceNotFoundException("ADMIN has not created the payslip yet.");
    }

    @Override
    public PayslipDto viewPayslip(String username, String month) {
        Payroll payroll = payrollRepository.findByEmployee_User_usernameAndMonth(username, month).orElseThrow(() -> new ResourceNotFoundException("Payroll record not found in database"));

        PayslipDto payslipDto = new PayslipDto();

        payslipDto.setEmployeeId(payroll.getEmployee().getManagerId());
        payslipDto.setName(payroll.getEmployee().getFirstName() + " " + payroll.getEmployee().getLastName());
        payslipDto.setMonth(payroll.getMonth());
        payslipDto.setYear(payroll.getYear());
        payslipDto.setDepartment(payroll.getEmployee().getDepartment().getName());
        payslipDto.setBasicSalary(payroll.getSalaryStructure().getBasicSalary());
        payslipDto.setHra(payroll.getSalaryStructure().getHra());
        payslipDto.setSpecialAllowance(payroll.getSalaryStructure().getSpecialAllowance());
        payslipDto.setBonus(payroll.getSalaryStructure().getBonus());
        payslipDto.setPfDeduction(payroll.getSalaryStructure().getPfDeduction());
        payslipDto.setTaxDeduction(payroll.getSalaryStructure().getTaxDeduction());
        payslipDto.setWorkingDays(payroll.getWorkingDays());
        payslipDto.setWorkingHoursInMinutes(payroll.getWorkingHoursInMinutes());
        payslipDto.setNetSalary(payroll.getNetSalary());

        return payslipDto;
    }

    @Override
    public PayslipDto viewPayslip(UUID empId, String month) {
        Payroll payroll = payrollRepository.findByEmployee_empIdAndMonth(empId, month).orElseThrow(() -> new ResourceNotFoundException("Payroll record not found in database"));

        PayslipDto payslipDto = new PayslipDto();

        payslipDto.setEmployeeId(payroll.getEmployee().getManagerId());
        payslipDto.setName(payroll.getEmployee().getFirstName() + " " + payroll.getEmployee().getLastName());
        payslipDto.setMonth(payroll.getMonth());
        payslipDto.setYear(payroll.getYear());
        payslipDto.setDepartment(payroll.getEmployee().getDepartment().getName());
        payslipDto.setBasicSalary(payroll.getSalaryStructure().getBasicSalary());
        payslipDto.setHra(payroll.getSalaryStructure().getHra());
        payslipDto.setSpecialAllowance(payroll.getSalaryStructure().getSpecialAllowance());
        payslipDto.setBonus(payroll.getSalaryStructure().getBonus());
        payslipDto.setPfDeduction(payroll.getSalaryStructure().getPfDeduction());
        payslipDto.setTaxDeduction(payroll.getSalaryStructure().getTaxDeduction());
        payslipDto.setWorkingDays(payroll.getWorkingDays());
        payslipDto.setWorkingHoursInMinutes(payroll.getWorkingHoursInMinutes());
        payslipDto.setNetSalary(payroll.getNetSalary());

        return payslipDto;
    }

//    @Override
//    public String calculateMonthlySalary(PayrollInput payrollInput) {
//
//      String endDateMonth = payrollInput.getToDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
//      String year = String.valueOf(payrollInput.getToDate().getYear());
//      Optional<Payroll> payrollOptional = payrollRepository.findByMonthAndYear(endDateMonth, year);
//        if(payrollOptional.isEmpty()) {
//
//            /* This is the attendance list of all employees */
//            List<Attendance> attendances = attendanceRepository.findByDateBetween(payrollInput.getFromDate(), payrollInput.getToDate());
//
//            /* This is the attendance list of particular employee */
//            List<Attendance> attendanceListOfOneEmployee = attendances.stream().filter(a -> a.getEmployee().getEmpId().equals(payrollInput.getEmpId())).toList();
//
//            /* It represents the no. of days the employee had clocked in after 11 AM (If employee clocked in after 11 AM
//               then attendance will automatically mark has ABSENT) */
//            long clockInAfter11AmDays = attendanceListOfOneEmployee.stream().filter(a -> a.getStatus().equals(AttendanceStatus.ABSENT)).count();
//
//            /* These are leaves of particular employee */
//            List<LeaveRequest> leavesOfEmployee = leaveRequestRepository.findByEmployee_empIdAndStatus(payrollInput.getEmpId(), LeaveStatus.APPROVED);
//
//            String startDateMonth = payrollInput.getFromDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
//
//            /* These leaves are considering half days also as full days so we have to find out no of half days
//             *  and then subtract it from these leaves then we get actual leaves in starting date month*/
//
//            List<LeaveRequest> leavesInStartDateMonth = leavesOfEmployee.stream().filter(l -> l.getMonth().equalsIgnoreCase(startDateMonth)).toList();
//            long noOfHalfDaysInStartDateMonth = leavesInStartDateMonth.stream().map(LeaveRequest::getNoOfHalfDays).count();
//            // totalLeavesInStartDaysMonth are considering half day leaves as full day leaves
//            long totalLeavesInStartDaysMonth = leavesInStartDateMonth.stream().count();
//            double actualLeavesInStartingDateMonth = totalLeavesInStartDaysMonth - (double) noOfHalfDaysInStartDateMonth /2;
//            // why we are dividing noOfHalfDaysInStartDateMonth /2 is because to get actual noOfHalfDays
//
//            List<LeaveRequest> leavesInEndDateMonth = leavesOfEmployee.stream().filter(l -> l.getMonth().equalsIgnoreCase(endDateMonth)).toList();
//            long noOfHalfDayLeavesInEndDateMonth = leavesInEndDateMonth.stream().map(LeaveRequest::getNoOfHalfDays).count();
//            long totalLeavesInEndDateMonth = leavesInEndDateMonth.stream().count();
//            double actualLeavesInEndDateMonth = totalLeavesInEndDateMonth - (double) noOfHalfDayLeavesInEndDateMonth /2;
//
//            // these are the number of leaves employee had taken during this time period
//            double totalActualLeaves = actualLeavesInStartingDateMonth + actualLeavesInEndDateMonth;
//
//            SalaryStructure salaryStructure = salaryStructureRepository.findByEmployee_empIdAndStatus(payrollInput.getEmpId(), SalaryStructureStatus.ACTIVE).orElseThrow(() -> new ResourceNotFoundException("Salary Structure record with given empId is not found in database"));
//
//            double totalEarnings = salaryStructure.getBasicSalary() + salaryStructure.getHra()
//                    + salaryStructure.getBonus() + salaryStructure.getSpecialAllowance();
//            double totalDeductions = salaryStructure.getPfDeduction() + salaryStructure.getTaxDeduction();
//
//            double netSalary = totalEarnings - totalDeductions;
//
//            double perDaySalary = netSalary / 30;
//
//            netSalary = netSalary - (perDaySalary * clockInAfter11AmDays); // subtracting days of clockIn after 11 AM
//
//            if (totalActualLeaves != 0) {
//                netSalary = netSalary - (perDaySalary * totalActualLeaves); // subtracting total leaves in given days span
//            }
//
//            double perHourSalary = perDaySalary/9;
//
//            double perMinuteSalary = perHourSalary/60;
//
//            double totalWorkingHoursInMinutes = attendanceListOfOneEmployee.stream().mapToDouble(a -> a.getWorkingHours().getMinute()).sum();
//
//            double takeHomeSalary = totalWorkingHoursInMinutes * perMinuteSalary;
//
//            Employee employee = employeeRepository.findById(payrollInput.getEmpId()).orElseThrow(() -> new ResourceNotFoundException("Employee record with given empId is not found in database"));
//
//            Payroll payroll = new Payroll();
//
//            payroll.setEmployee(employee);
//            payroll.setSalaryStructure(salaryStructure);
//            payroll.setTotalEarnings(totalEarnings);
//            payroll.setTotalDeductions(totalDeductions);
//            payroll.setNetSalary(takeHomeSalary);
//            payroll.setMonth(endDateMonth);
//            payroll.setYear(String.valueOf(payrollInput.getToDate().getYear()));
//            payroll.setGeneratedDate(LocalDate.now());
//            payroll.setPaymentStatus(PaymentStatus.PENDING);
//
//            payrollRepository.save(payroll);
//            return "Payroll record is created...!";
//        }else{
//            return "Payroll record for given month is already created";
//        }
//    }


}
