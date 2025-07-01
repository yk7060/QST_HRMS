package com.quantumsoft.hrms.controller;

import com.quantumsoft.hrms.entity.EmployeeBenefit;
import com.quantumsoft.hrms.servicei.EmployeeBenefitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/employee-benefits")
@RequiredArgsConstructor
public class EmployeeBenefitController {

    private final EmployeeBenefitService service;

    @PreAuthorize("hasRole('HR')")
    @PostMapping
    public EmployeeBenefit assign(@RequestBody EmployeeBenefit benefit) {
        System.out.println("empId "+benefit.getEmployee().getEmpId());
        return service.assignBenefit(benefit);
    }

    @GetMapping("/{employeeId}")
    public List<EmployeeBenefit> getBenefits(@PathVariable UUID empId) {
        return service.getBenefitsForEmployee(empId);
    }

    @PutMapping("/{id}")
    public EmployeeBenefit update(@PathVariable Long id, @RequestBody EmployeeBenefit updated) {
        return service.updateBenefit(id, updated);
    }

    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable Long id) {
        service.softDeleteBenefit(id);
    }
}

