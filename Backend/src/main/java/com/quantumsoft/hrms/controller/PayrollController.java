package com.quantumsoft.hrms.controller;

import com.quantumsoft.hrms.dto.PayrollInput;
import com.quantumsoft.hrms.dto.PayslipDto;
import com.quantumsoft.hrms.entity.SalaryStructure;
import com.quantumsoft.hrms.servicei.PayrollServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/payroll")
public class PayrollController {

    @Autowired
    private PayrollServiceI payrollService;

    /* API to fetch ACTIVE Salary Structure of Employee by using empId */

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/salaryStructure/{empId}", produces = "application/json")
    public ResponseEntity<SalaryStructure> fetchActiveSalaryStructure(@PathVariable UUID empId){
        return new ResponseEntity<SalaryStructure>(payrollService.fetchActiveSalaryStructure(empId), HttpStatus.OK);
    }

    /* API to save Payroll data (calculate monthly salary) of Employee by using empId */

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/addPayroll", consumes = "application/json", produces = "plain/text")
    public ResponseEntity<String> calculateMonthlySalary(@RequestBody PayrollInput payrollInput){
        return new ResponseEntity<String>(payrollService.calculateMonthlySalary(payrollInput), HttpStatus.OK);
    }

    // This api will generate the requested month payslip in pdf format

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/generatePayslip/{empId}", produces = "plain/text")
    public ResponseEntity<String> generatePayslip(@PathVariable UUID empId, @RequestParam String month) throws FileNotFoundException {
        return new ResponseEntity<String>(payrollService.generatePayslip(empId, month), HttpStatus.OK);
    }

    // ADMIN can download any employee's payslip

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/downloadPayslip/{empId}", produces = "application/pdf")
    public ResponseEntity<Resource> downloadPayslip(@PathVariable UUID empId, @RequestParam String month) throws FileNotFoundException {
        return new ResponseEntity<Resource>(payrollService.downloadPayslip(empId, month), HttpStatus.OK);
    }

    /* When ADMIN generates the payslip then only EMPLOYEE can download that payslip
    *  EMPLOYEE can download their own payslip only*/

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping(value = "/downloadPayslip", produces = "application/pdf")
    public ResponseEntity<Resource> downloadPayslip(@RequestParam String month) throws FileNotFoundException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<Resource>(payrollService.downloadPayslip(username, month), HttpStatus.OK);
    }

    // EMPLOYEE can only view their payslip.

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping(value = "/viewPayslip", produces = "application/json")
    public ResponseEntity<PayslipDto> viewPayslip(@RequestParam String month) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<PayslipDto>(payrollService.viewPayslip(username, month), HttpStatus.OK);
    }

    // ADMIN can view any employee's payslip.

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/viewPayslip/{empId}", produces = "application/json")
    public ResponseEntity<PayslipDto> viewPayslip(@PathVariable UUID empId, @RequestParam String month) {
        return new ResponseEntity<PayslipDto>(payrollService.viewPayslip(empId, month), HttpStatus.OK);
    }
}
