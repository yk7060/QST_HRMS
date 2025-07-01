package com.quantumsoft.hrms.entity;

import com.quantumsoft.hrms.enums.Action;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID auditLogId;
    private String username;
    @Enumerated(EnumType.STRING)
    private Action action;
    private LocalDateTime time;
}
