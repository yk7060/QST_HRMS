package com.quantumsoft.hrms.repository;

import com.quantumsoft.hrms.entity.LeaveRequest;
import com.quantumsoft.hrms.enums.LeaveName;
import com.quantumsoft.hrms.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {

    boolean existsByYearAndMonthAndLeaveType_name(String year, String month, LeaveName name);

    List<LeaveRequest> findByEmployee_empIdAndStatus(UUID empId, LeaveStatus leaveStatus);
}
