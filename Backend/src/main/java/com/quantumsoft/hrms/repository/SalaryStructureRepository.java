package com.quantumsoft.hrms.repository;

import com.quantumsoft.hrms.entity.SalaryStructure;
import com.quantumsoft.hrms.enums.SalaryStructureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, UUID> {

    boolean existsByEmployee_empIdAndStatus(UUID empId, SalaryStructureStatus salaryStructureStatus);

    Optional<SalaryStructure> findByEmployee_empIdAndStatus(UUID empId, SalaryStructureStatus salaryStructureStatus);
    Optional<SalaryStructure> findByEmployeeEmpId(UUID empId);

}
