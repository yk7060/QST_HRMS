package com.quantumsoft.hrms.repository;

import com.quantumsoft.hrms.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, UUID> {


    Optional<Payroll> findByMonthAndYear(String month, String year);

    Optional<Payroll>  findByEmployee_empIdAndMonth(UUID empId, String month);

    Optional<Payroll> findByEmployee_User_usernameAndMonth(String username, String month);
}
