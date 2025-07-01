package com.quantumsoft.hrms.controller;

import com.quantumsoft.hrms.entity.EmployeeOptionalHoliday;
import com.quantumsoft.hrms.servicei.EmployeeOptionalHolidayServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/employeeOptionalHoliday")
public class EmployeeOptionalHolidayController {

    @Autowired
    private EmployeeOptionalHolidayServiceI employeeOptionalHolidayService;

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping(value = "/select")
    public ResponseEntity<String> selectOptionalHoliday(@RequestParam UUID optionalHolidayId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<String>(employeeOptionalHolidayService.selectOptionalHoliday(username, optionalHolidayId), HttpStatus.CREATED);
    }

    // to check how many optional holidays are remaining and consumed

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping(value = "/fetchAll")
    public ResponseEntity<List<EmployeeOptionalHoliday>> fetchAllEmployeeOptionalHoliday(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<List<EmployeeOptionalHoliday>>(employeeOptionalHolidayService.fetchAllEmployeeOptionalHoliday(username), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping(value = "/fetch/{employeeOptionalHolidayId}")
    public ResponseEntity<EmployeeOptionalHoliday> fetchEmployeeOptionalHoliday(@PathVariable UUID employeeOptionalHolidayId){
        return new ResponseEntity<EmployeeOptionalHoliday>(employeeOptionalHolidayService.fetchEmployeeOptionalHoliday(employeeOptionalHolidayId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping(value = "/approveReject/{employeeOptionalHolidayId}")
    public ResponseEntity<String> approveRejectEmployeeOptionalHoliday(@PathVariable UUID employeeOptionalHolidayId, @RequestParam String status){
        return new ResponseEntity<String>(employeeOptionalHolidayService.approveRejectEmployeeOptionalHoliday(employeeOptionalHolidayId, status), HttpStatus.OK);
    }
}
