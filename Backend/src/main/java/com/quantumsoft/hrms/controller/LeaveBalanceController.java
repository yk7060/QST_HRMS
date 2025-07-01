package com.quantumsoft.hrms.controller;

import com.quantumsoft.hrms.entity.LeaveBalance;
import com.quantumsoft.hrms.entity.LeaveType;
import com.quantumsoft.hrms.servicei.LeaveBalanceServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/leaveBalance")
public class LeaveBalanceController {

    @Autowired
    private LeaveBalanceServiceI leaveBalanceService;

    /* Why we are creating this functionality is because if Employee want to see leave balance before taking
       any leave then employee should be able to see all types of available leaves. We are updating LeaveBalance
       record only when Manager will approve the leave request then only leave balance will change */

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping(value = "/get", produces = "application/json")
    public ResponseEntity<LeaveBalance> getLeaveBalance(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<LeaveBalance>(leaveBalanceService.getLeaveBalance(username), HttpStatus.CREATED);
    }

}
