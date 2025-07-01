package com.quantumsoft.hrms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID leaveBalanceId;

    @OneToOne
    @JoinColumn(name = "empId")
    @JsonIgnore
    private Employee employee;
    @JsonIgnore
    private String username;
    private String employeeName;
    private String year; // this year filed will be used for carry forward the sick and paid leaves
    private Double availableSickLeaves;
    private Double consumedSickLeaves;
    private Double availableCasualLeaves;
    private Double consumedCasualLeaves;
    private Double availablePaidLeaves;
    private Double consumedPaidLeaves;
    private Double availableMaternityLeaves;
    private Double consumedMaternityLeaves;
    private Double availablePaternityLeaves;
    private Double consumedPaternityLeaves;
    private Double availableMarriageLeaves;
    private Double consumedMarriageLeaves;
}
