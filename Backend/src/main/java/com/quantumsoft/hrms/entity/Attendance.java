package com.quantumsoft.hrms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quantumsoft.hrms.enums.AttendanceStatus;
import com.quantumsoft.hrms.enums.Mode;
import com.quantumsoft.hrms.enums.WorkingFrom;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID attendanceId;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "empId")
    private Employee employee;
    private LocalDate date;
    private LocalTime clockInTime;
    private LocalTime clockOutTime;
    @Enumerated(EnumType.STRING)
    private WorkingFrom workingFrom;
    @Enumerated(EnumType.STRING)
    private Mode mode;
    private String location; // (latitude_value, longitude_value)
    private String timesheetProjectCode; // project name
    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;
    private LocalTime workingHours;
    private LocalTime lateBy;
    private LocalTime earlyBy;
    @PrePersist
    public void setDate(){
        this.date = LocalDate.now();
    }
}
