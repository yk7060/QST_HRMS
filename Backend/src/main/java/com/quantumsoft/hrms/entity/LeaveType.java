package com.quantumsoft.hrms.entity;

import com.quantumsoft.hrms.enums.LeaveName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID leaveTypeId;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private LeaveName name;
    private String description;
    private Double maxDaysPerYear;
}
