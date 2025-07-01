package com.quantumsoft.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PayslipDto {
    private String name;
    private UUID employeeId;
    private String department;
    private String month;
    private String year;
    private Double basicSalary;
    private Double hra;
    private Double specialAllowance;
    private Double bonus;
    private Double pfDeduction;
    private Double taxDeduction;
    private Double workingDays;
    private Double workingHoursInMinutes;
    private Double netSalary;
}