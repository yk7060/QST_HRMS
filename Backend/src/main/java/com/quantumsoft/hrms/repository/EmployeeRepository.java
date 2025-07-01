package com.quantumsoft.hrms.repository;

import com.quantumsoft.hrms.entity.Employee;
import com.quantumsoft.hrms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    boolean existsByEmail(String email);
    boolean existsByContactNumber(String contactNumber);
    boolean existsByEmployeeCode(String employeeCode);

    Optional<Employee> findByUser_userId(UUID userId);

    //Employee findByUserId(UUID userId);

    Optional<Employee> findByUser(User user);
}
