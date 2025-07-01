package com.quantumsoft.hrms.dto;

import com.quantumsoft.hrms.enums.Mode;
import com.quantumsoft.hrms.enums.WorkingFrom;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClockInInput {

    private UUID employeeId;
    @Enumerated(EnumType.STRING)
    private WorkingFrom workingFrom;
    @Enumerated(EnumType.STRING)
    private Mode mode;
    private String location; // (latitude_value, longitude_value)
}
