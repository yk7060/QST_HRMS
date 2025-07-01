package com.quantumsoft.hrms.controller;

import com.quantumsoft.hrms.entity.LeaveRequest;
import com.quantumsoft.hrms.servicei.LeaveRequestServiceI;
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
@RequestMapping(value = "/api/leaveRequest")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestServiceI leaveRequestService;

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping(value = "/add")
    public ResponseEntity<String> addLeaveRequest(@RequestBody LeaveRequest leaveRequest){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<String>(leaveRequestService.addLeaveRequest(username, leaveRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping(value = "/approveReject/{leaveRequestId}")
    public ResponseEntity<String> approveRejectedLeaveRequest(@PathVariable UUID leaveRequestId, @RequestParam String status){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<String>(leaveRequestService.approveRejectedLeaveRequest(leaveRequestId, status), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping(value = "/fetch/{leaveRequestId}")
    public ResponseEntity<LeaveRequest> fetchLeaveRequest(@PathVariable UUID leaveRequestId){
        return new ResponseEntity<LeaveRequest>(leaveRequestService.fetchLeaveRequest(leaveRequestId), HttpStatus.OK);
    }

    // to gets list of rejected approved and pending leave request of employee
    @PreAuthorize("hasRole('MANAGER') or hasRole('HR') or hasRole('EMPLOYEE')")
    @GetMapping(value = "/trackLeave/{empId}")
    public ResponseEntity<List<LeaveRequest>> trackLeaveRequest(@PathVariable UUID empId, @RequestParam String status){
        return new ResponseEntity<List<LeaveRequest>>(leaveRequestService.trackLeaveRequest(empId, status), HttpStatus.OK);
    }

}
