package com.quantumsoft.hrms.controller;

import com.quantumsoft.hrms.entity.SalaryStructure;
import com.quantumsoft.hrms.servicei.SalaryStructureServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// @CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/salaryStructure")
public class SalaryStructureController {

    @Autowired
    private SalaryStructureServiceI salaryStructureService;

    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    @PostMapping(value = "/assign")
    public ResponseEntity<String> assignSalaryStructureToEmployee(@RequestBody SalaryStructure salaryStructure){
        return new ResponseEntity<String>(salaryStructureService.assignSalaryStructureToEmployee(salaryStructure), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('HR') or hasROle('ADMIN') or hasRole('EMPLOYEE')")
    @GetMapping(value = "/get/{salaryStructureId}")
    public ResponseEntity<SalaryStructure> getSalaryStructureOfEmployee(@PathVariable UUID salaryStructureId){
        return new ResponseEntity<SalaryStructure>(salaryStructureService.getSalaryStructureOfEmployee(salaryStructureId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    @PutMapping(value = "/update/{salaryStructureId}")
    public ResponseEntity<String> updateSalaryStructureOfEmployee(@PathVariable UUID salaryStructureId, @RequestBody SalaryStructure salaryStructure){
        return new ResponseEntity<String>(salaryStructureService.updateSalaryStructureOfEmployee(salaryStructureId, salaryStructure), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('HR') or hasROle('ADMIN')")
    @DeleteMapping(value = "/delete/{salaryStructureId}")
    public ResponseEntity<Void> deleteSalaryStructureOfEmployee(@PathVariable UUID salaryStructureId){
        salaryStructureService.deleteSalaryStructureOfEmployee(salaryStructureId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<SalaryStructure>> getAllSalaryStructures() {
        return new ResponseEntity<>(salaryStructureService.getAllSalaryStructures(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    @GetMapping("/get/by-employee/{empId}")
    public ResponseEntity<SalaryStructure> getSalaryStructureByEmployeeId(@PathVariable UUID empId) {
        return new ResponseEntity<>(salaryStructureService.getByEmployeeId(empId), HttpStatus.OK);
    }


}
