package com.quantumsoft.hrms.servicei;

import com.quantumsoft.hrms.entity.Department;

import java.util.List;

public interface DepartmentServicei {
   List<Department> getAllDepartments();
    Department getDepartmentById(Long id);
    Department createDepartment(Department department);
    Department updateDepartment(Long id, Department department);
    void deleteDepartment(Long id);
}
