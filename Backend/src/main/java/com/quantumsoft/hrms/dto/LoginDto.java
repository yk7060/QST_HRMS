package com.quantumsoft.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginDto {

    private String username;
    private String password;
}
