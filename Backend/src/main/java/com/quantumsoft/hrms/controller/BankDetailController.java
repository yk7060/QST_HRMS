package com.quantumsoft.hrms.controller;

import com.quantumsoft.hrms.entity.BankDetail;

import com.quantumsoft.hrms.servicei.BankDetailService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController

@RequestMapping("/api/bank-details")

@RequiredArgsConstructor

public class BankDetailController {

    @Autowired

    private BankDetailService bankDetailService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping

    public ResponseEntity<BankDetail> createBankDetail(@Valid @RequestBody BankDetail bankDetail) {

        return ResponseEntity.ok(bankDetailService.createBankDetail(bankDetail));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")

    public ResponseEntity<BankDetail> getBankDetail(@PathVariable Long id) {

        return ResponseEntity.ok(bankDetailService.getBankDetailById(id));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping

    public ResponseEntity<List<BankDetail>> getAllBankDetails() {

        return ResponseEntity.ok(bankDetailService.getAllBankDetails());

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")

    public ResponseEntity<BankDetail> updateBankDetail(

            @PathVariable Long id,

            @Valid @RequestBody BankDetail bankDetail) {

        return ResponseEntity.ok(bankDetailService.updateBankDetail(id, bankDetail));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")

    public ResponseEntity<Void> deleteBankDetail(@PathVariable Long id) {

        bankDetailService.deleteBankDetail(id);

        return ResponseEntity.noContent().build();

    }

}

