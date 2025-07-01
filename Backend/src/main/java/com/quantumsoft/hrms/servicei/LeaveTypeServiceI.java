package com.quantumsoft.hrms.servicei;

import com.quantumsoft.hrms.entity.LeaveType;

import java.util.List;
import java.util.UUID;

public interface LeaveTypeServiceI {

    public String addLeaveType(LeaveType leaveType);

    public String updateLeaveType(UUID leaveTypeId, LeaveType leaveType);

    public LeaveType getLeaveType(UUID leaveTypeId);

    public List<LeaveType> getLeaveTypes();

    public void deleteLeaveType(UUID leaveTypeId);
}
