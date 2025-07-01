package com.quantumsoft.hrms.controller;

import com.quantumsoft.hrms.entity.AuditLog;
import com.quantumsoft.hrms.servicei.AuditLogServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/auditLog")
public class AuditLogController {

    @Autowired
    private AuditLogServiceI service;

    @GetMapping(value = "/logs", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<List<AuditLog>> getAuditLog(@RequestParam String username){
       return new ResponseEntity<List<AuditLog>>(service.getAuditLog(username), HttpStatus.OK);
    }

//    @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER') or hasRole('FINANCE')")
//    @GetMapping(value = "/logs")
//    public ResponseEntity<List<AuditLog>> getAuditLogs(){
//        return new ResponseEntity<List<AuditLog>>(service.getAuditLogs(), HttpStatus.OK);
//    }
}
