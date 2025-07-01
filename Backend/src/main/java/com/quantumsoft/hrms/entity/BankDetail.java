package com.quantumsoft.hrms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;

import lombok.*;

@Entity

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class BankDetail {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @NotBlank(message = "Account holder name is required")

    private String accountHolderName;

    @NotBlank(message = "Bank name is required")

    private String bankName;

    @NotBlank(message = "Account number is required")

    @Size(min = 8, max = 20, message = "Account number must be between 8 and 20 digits")

    private String accountNumber;

    @NotBlank(message = "IFSC code is required")

    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code format")

    private String ifscCode;

    @NotBlank(message = "Branch name is required")

    private String branch;

    @NotBlank(message = "Account type is required")

    private String accountType; // e.g. "Savings", "Current"

    public Long getId() {

        return id;

    }

    public void setId(Long id) {

        this.id = id;

    }

    public String getAccountHolderName() {

        return accountHolderName;

    }

    public void setAccountHolderName(String accountHolderName) {

        this.accountHolderName = accountHolderName;

    }

    public String getBankName() {

        return bankName;

    }

    public void setBankName(String bankName) {

        this.bankName = bankName;

    }

    public String getAccountNumber() {

        return accountNumber;

    }

    public void setAccountNumber(String accountNumber) {

        this.accountNumber = accountNumber;

    }

    public String getIfscCode() {

        return ifscCode;

    }

    public void setIfscCode(String ifscCode) {

        this.ifscCode = ifscCode;

    }

    public String getBranch() {

        return branch;

    }

    public void setBranch(String branch) {

        this.branch = branch;

    }

    public String getAccountType() {

        return accountType;

    }

    public void setAccountType(String accountType) {

        this.accountType = accountType;

    }
}

