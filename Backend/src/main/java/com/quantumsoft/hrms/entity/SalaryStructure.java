package com.quantumsoft.hrms.entity;

import com.quantumsoft.hrms.enums.SalaryStructureStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID salaryStructureId;
    @ManyToOne
    @JoinColumn(name = "empId")
    private Employee employee;
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

    @PrePersist
    public void setCreatedDate(){
        this.createdDate = LocalDateTime.now();
    }

    @PreUpdate
    public void setUpdatedDate(){
        this.updatedDate = LocalDateTime.now();
    }
}