package com.quantumsoft.hrms.controller;

import com.quantumsoft.hrms.entity.Benefit;
import com.quantumsoft.hrms.servicei.BenefitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/benefits")
@RequiredArgsConstructor
public class BenefitController {
    private final BenefitService service;

    @PostMapping
    public Benefit create(@RequestBody Benefit benefit) {
        return service.createBenefit(benefit);
    }

    @GetMapping
    public List<Benefit> getAll() {
        return service.getAllBenefits();
    }
}