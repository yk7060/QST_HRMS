package com.quantumsoft.hrms.servicei;

import com.quantumsoft.hrms.entity.LeaveRequest;

import java.util.List;
import java.util.UUID;

public interface LeaveRequestServiceI {

    public String addLeaveRequest(String username, LeaveRequest leaveRequest);

    String approveRejectedLeaveRequest(UUID leaveRequestId, String status);

    List<LeaveRequest> trackLeaveRequest(UUID empId, String status);

    LeaveRequest fetchLeaveRequest(UUID leaveRequestId);
}
