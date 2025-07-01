package com.quantumsoft.hrms.entity;

import com.quantumsoft.hrms.enums.LeaveName;
import com.quantumsoft.hrms.enums.LeaveStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID leaveRequestId;
    @ManyToOne
    @JoinColumn(name = "empId")
    private Employee employee;
    @ManyToOne
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private double noOfHalfDays;
    private String month; // to check whether employee is taking another leave in same month
    private String year; /* this is to check next year..like if in previous year in the same month you have
                            taken a casual leave then but in next year you have not taken a leave in that
                            month that check that wa are taking year */
    private String reason;
    @Enumerated(EnumType.STRING)
    private LeaveStatus status;
}
