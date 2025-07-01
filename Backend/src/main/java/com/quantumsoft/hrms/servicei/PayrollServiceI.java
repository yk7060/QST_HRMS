package com.quantumsoft.hrms.servicei;

import com.quantumsoft.hrms.dto.PayrollInput;
import com.quantumsoft.hrms.dto.PayslipDto;
import com.quantumsoft.hrms.entity.SalaryStructure;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.util.UUID;

public interface PayrollServiceI {

    public SalaryStructure fetchActiveSalaryStructure(UUID empId);

    public String calculateMonthlySalary(PayrollInput payrollInput);

    public String generatePayslip(UUID empId, String month) throws FileNotFoundException;

    public Resource downloadPayslip(UUID empId, String month) throws FileNotFoundException;

    public Resource downloadPayslip(String username, String month) throws FileNotFoundException;

    public PayslipDto viewPayslip(String username, String month);

    public PayslipDto viewPayslip(UUID empId, String month);

    /* API'S
    * fetch active salary structure by using empId - ADMIN
    * calculate earnings deductions net salary by including monthly Attendance details - ADMIN
    * generate payslip - pdf - ADMIN
    * view payslip - EMPLOYEE or ADMIN
    * download payslip - EMPLOYEE or ADMIN
    * */
}
