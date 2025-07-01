package com.quantumsoft.hrms.repository;

import com.quantumsoft.hrms.entity.LeaveType;
import com.quantumsoft.hrms.enums.LeaveName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, UUID> {

    LeaveType findByName(LeaveName leaveName);
}
