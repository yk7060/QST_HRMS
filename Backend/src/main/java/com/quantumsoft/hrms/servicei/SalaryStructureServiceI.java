package com.quantumsoft.hrms.servicei;

import com.quantumsoft.hrms.entity.SalaryStructure;

import java.util.List;
import java.util.UUID;

public interface SalaryStructureServiceI {

    String assignSalaryStructureToEmployee(SalaryStructure salaryStructure);

    String updateSalaryStructureOfEmployee(UUID salaryStructureId, SalaryStructure salaryStructure);

    SalaryStructure getSalaryStructureOfEmployee(UUID salaryStructureId);

    void deleteSalaryStructureOfEmployee(UUID salaryStructureId);
    List<SalaryStructure> getAllSalaryStructures();
    SalaryStructure getByEmployeeId(UUID empId);


}
