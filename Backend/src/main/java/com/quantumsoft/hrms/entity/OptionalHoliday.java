package com.quantumsoft.hrms.entity;

import com.quantumsoft.hrms.enums.OptionalHolidayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class OptionalHoliday {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID optionalHolidayId;
    private String name;
    private LocalDate date;
    private String description;
    @Enumerated(EnumType.STRING)
    private OptionalHolidayType optionalHolidayType;
}
