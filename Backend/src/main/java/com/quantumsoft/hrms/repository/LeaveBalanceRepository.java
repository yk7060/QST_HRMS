package com.quantumsoft.hrms.repository;

import com.quantumsoft.hrms.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, UUID> {

    Optional<LeaveBalance> findByUsername(String username);

    Optional<LeaveBalance> findByEmployee_empId(UUID empId);
}
