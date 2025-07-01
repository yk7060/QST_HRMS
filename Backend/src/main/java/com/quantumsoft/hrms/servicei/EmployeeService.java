package com.quantumsoft.hrms.servicei;

import com.quantumsoft.hrms.entity.Employee;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public interface EmployeeService {
//    Employee createEmployee(Employee employee, MultipartFile photo);
     Employee createEmployee(Employee employee);

   // Employee updateEmployee(UUID empId, Employee employee, MultipartFile photo);
    void deleteEmployee(UUID empId);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(UUID empId);

//    Employee updateEmployee(UUID empId, Employee employee, MultipartFile photo);

    Employee assignManagerToEmployee(UUID empId, UUID managerId);

    Employee addBankDetailsToEmployee(UUID empId, Long bankDetailsId);
    Employee getEmployeeByUsername(String username);

}