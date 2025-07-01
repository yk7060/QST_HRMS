package com.quantumsoft.hrms.dto;

import com.quantumsoft.hrms.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateRoleInput {

    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
}
