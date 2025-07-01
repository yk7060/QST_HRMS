package com.quantumsoft.hrms.servicei;


import com.quantumsoft.hrms.entity.EmployeeBenefit;

import java.util.List;
import java.util.UUID;

public interface EmployeeBenefitService {
    EmployeeBenefit assignBenefit(EmployeeBenefit benefit);
    List<EmployeeBenefit> getBenefitsForEmployee(UUID empId);

    EmployeeBenefit updateBenefit(Long id, EmployeeBenefit updated);
    void softDeleteBenefit(Long id);
}