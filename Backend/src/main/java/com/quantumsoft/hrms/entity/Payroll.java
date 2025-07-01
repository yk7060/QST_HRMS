package com.quantumsoft.hrms.entity;

import com.quantumsoft.hrms.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID payrollId;
    @ManyToOne
    @JoinColumn(name = "empId")
    private Employee employee;
    @ManyToOne
    @JoinColumn(name = "salaryStructureId")
    private SalaryStructure salaryStructure;
    private LocalDate generatedDate;
    private String year;
    private String month;
    private Double totalEarnings;
    private Double totalDeductions;
    private Double netSalary;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private LocalDate paymentDate; // if paid
    private String payslipPath;
    private Double workingDays;
    private Double workingHoursInMinutes;
}