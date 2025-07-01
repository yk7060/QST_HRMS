package com.quantumsoft.hrms.dto;

import com.quantumsoft.hrms.entity.Employee;
import com.quantumsoft.hrms.enums.SalaryStructureStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class SalaryStructureDto {
    private UUID salaryStructureId;
    private UUID empId;
    private String name;
    private Double basicSalary;
    private Double hra;
    private Double specialAllowance;
    private Double bonus;
    private Double pfDeduction;
    private Double taxDeduction;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private Double totalCTC;
    @Enumerated(EnumType.STRING)
    private SalaryStructureStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
