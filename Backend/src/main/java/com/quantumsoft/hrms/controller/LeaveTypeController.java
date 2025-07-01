package com.quantumsoft.hrms.controller;

import com.quantumsoft.hrms.entity.LeaveType;
import com.quantumsoft.hrms.servicei.LeaveTypeServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/leaveType")
public class LeaveTypeController {

    @Autowired
    private LeaveTypeServiceI leaveTypeService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    @PostMapping(value = "/add", consumes = "application/json", produces = "plain/text")
    public ResponseEntity<String> addLeaveType(@RequestBody LeaveType leaveType){
        return new ResponseEntity<String>(leaveTypeService.addLeaveType(leaveType), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    @PutMapping(value = "/update/{leaveTypeId}", consumes = "application/json", produces = "plain/text")
    public ResponseEntity<String> updateLeaveType(@PathVariable UUID leaveTypeId, @RequestBody LeaveType leaveType){
        return new ResponseEntity<String>(leaveTypeService.updateLeaveType(leaveTypeId, leaveType), HttpStatus.OK);
    }

    @GetMapping(value = "/get/{leaveTypeId}", produces = "application/json")
    public ResponseEntity<LeaveType> getLeaveType(@PathVariable UUID leaveTypeId){
        return new ResponseEntity<LeaveType>(leaveTypeService.getLeaveType(leaveTypeId), HttpStatus.OK);
    }

    @GetMapping(value = "/get", produces = "application/json")
    public ResponseEntity<List<LeaveType>> getLeaveTypes(){
        return new ResponseEntity<List<LeaveType>>(leaveTypeService.getLeaveTypes(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    @DeleteMapping(value = "/delete/{leaveTypeId}")
    public ResponseEntity<Void> deleteLeaveType(@PathVariable UUID leaveTypeId){
        leaveTypeService.deleteLeaveType(leaveTypeId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

}
