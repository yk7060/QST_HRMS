package com.quantumsoft.hrms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quantumsoft.hrms.enums.OptionalHolidayStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class EmployeeOptionalHoliday {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID employeeOptionalHolidayId;
    @ManyToOne
    @JsonIgnore
    private Employee employee;
    @OneToOne
    private OptionalHoliday optionalHoliday;
    @Enumerated(EnumType.STRING)
    private OptionalHolidayStatus status;
    private Integer month;
    private Integer year;
}
