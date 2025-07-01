package com.quantumsoft.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReportInput {

    private LocalDate fromDate;
    private LocalDate toDate;
    private UUID empId;
    private Long departmentId;
}
