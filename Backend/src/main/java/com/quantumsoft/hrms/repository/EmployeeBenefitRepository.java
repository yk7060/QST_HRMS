package com.quantumsoft.hrms.repository;


import com.quantumsoft.hrms.entity.EmployeeBenefit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeeBenefitRepository extends JpaRepository<EmployeeBenefit, Long> {
    List<EmployeeBenefit> findByEmployee_EmpId(UUID empId);
    List<EmployeeBenefit> findByEmployee_EmpIdAndBenefit_BenefitIdAndStatus(
            UUID empId, Long benefitId, String status);
}
