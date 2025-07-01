package com.quantumsoft.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PayrollInput {

    private UUID empId;
    private LocalDate fromDate;
    private LocalDate toDate;
}
