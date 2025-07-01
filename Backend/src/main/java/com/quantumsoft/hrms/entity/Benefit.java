package com.quantumsoft.hrms.entity;

import com.quantumsoft.hrms.enums.BenefitType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Benefit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long benefitId;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private BenefitType type;

    private boolean isTaxable;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

