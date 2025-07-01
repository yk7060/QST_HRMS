package com.quantumsoft.hrms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quantumsoft.hrms.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeBenefit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeBenefitId;

    @ManyToOne
    @JoinColumn(name = "employee_id",referencedColumnName = "empId")
    @JsonIgnore
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "benefit_id",referencedColumnName = "benefitId")
    @JsonIgnore
    private Benefit benefit;

    private BigDecimal amount;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String notes;

    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}