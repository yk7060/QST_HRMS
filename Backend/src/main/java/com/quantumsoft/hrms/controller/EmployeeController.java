package com.quantumsoft.hrms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantumsoft.hrms.entity.Employee;
import com.quantumsoft.hrms.servicei.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Employee> createEmployee(@Valid @RequestPart(value = "employee", required = true) String employee,
                                                   @RequestPart(value = "photo", required = true) MultipartFile photo) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Employee employee1 = objectMapper.readValue(employee, Employee.class);
        Employee e = new Employee();
        e.setAddress(employee1.getAddress());
        e.setUser(employee1.getUser());
        e.setCertifications(employee1.getCertifications());
        e.setBankDetailsId(employee1.getBankDetailsId());
        e.setAddress(employee1.getAddress());
        e.setContactNumber(employee1.getContactNumber());
        e.setEmployeeCode(employee1.getEmployeeCode());
        e.setDesignation(employee1.getDesignation());
        e.setEducation(employee1.getEducation());
        e.setDepartment(employee1.getDepartment());
        e.setEmail(employee1.getEmail());
        e.setExitReason(employee1.getExitReason());
        e.setPhoto(photo.getBytes());
        e.setFirstName(employee1.getFirstName());
        e.setLastName(employee1.getLastName());
        e.setJobType(employee1.getJobType());
        e.setDesignation(employee1.getDesignation());
        e.setStatus(employee1.getStatus());
        e.setGender(employee1.getGender());
        e.setDob(employee1.getDob());
        e.setEmergencyContact(employee1.getEmergencyContact());
        e.setExperience(employee1.getExperience());
        e.setJoiningDate(employee1.getJoiningDate());
        e.setProbationEndDate(employee1.getProbationEndDate());
        e.setLocation(employee1.getLocation());
        e.setManagerId(employee1.getManagerId());
        Employee saveEmployee = employeeService.createEmployee(e);
        return ResponseEntity.ok(saveEmployee);
        // return ResponseEntity.ok(employeeService.createEmployee(employee, photo));
    }

    @PreAuthorize("hasRole('HR'))")
    @PatchMapping(value = "/{empId}")
    public ResponseEntity<Employee> assignManagerToEmployee(@PathVariable UUID empId, @RequestParam UUID managerId) {
        return new ResponseEntity<Employee>(employeeService.assignManagerToEmployee(empId, managerId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('HR')")
    @PutMapping(value = "/{empId}")
    public ResponseEntity<Employee> addBankDetailsToEmployee(@PathVariable UUID empId, @RequestParam Long bankDetailsId) {
        return new ResponseEntity<Employee>(employeeService.addBankDetailsToEmployee(empId, bankDetailsId), HttpStatus.OK);
    }

//    @PutMapping("/{empId}")
//    @PreAuthorize("hasRole('HR') or hasRole('EMPLOYEE')")
//    public ResponseEntity<Employee> updateEmployee(@PathVariable UUID empId,
//                                                   @Valid @RequestPart("employee") String employee,
//                                                   @RequestPart(value = "photo", required = true) MultipartFile photo) throws IOException {
//        ObjectMapper objectMapper= new ObjectMapper();
//        Employee employee1 = objectMapper.readValue(employee, Employee.class);
//        Employee e=new Employee();
//        e.setAddress(employee1.getAddress());
//        e.setUser(employee1.getUser());
//        e.setCertifications(employee1.getCertifications());
//        e.setBankDetailsId(employee1.getBankDetailsId());
//        e.setAddress(employee1.getAddress());
//        e.setContactNumber(employee1.getContactNumber());
//        e.setEmployeeCode(employee1.getEmployeeCode());
//        e.setDesignation(employee1.getDesignation());
//        e.setEducation(employee1.getEducation());
//        e.setDepartment(employee1.getDepartment());
//        e.setEmail(employee1.getEmail());
//        e.setExitReason(employee1.getExitReason());
//        e.setPhoto(photo.getBytes());
//        e.setFirstName(employee1.getFirstName());
//        e.setLastName(employee1.getLastName());
//        e.setJobType(employee1.getJobType());
//        e.setDesignation(employee1.getDesignation());
//        e.setStatus(employee1.getStatus());
//        e.setGender(employee1.getGender());
//        e.setDob(employee1.getDob());
//        e.setEmergencyContact(employee1.getEmergencyContact());
//        e.setExperience(employee1.getExperience());
//        e.setJoiningDate(employee1.getJoiningDate());
//        e.setProbationEndDate(employee1.getProbationEndDate());
//        e.setLocation(employee1.getLocation());
//        e.setManagerId(employee1.getManagerId());
//        return ResponseEntity.ok(employeeService.updateEmployee(empId, e, photo));
//    }

    @DeleteMapping("/{empId}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID empId) {

        employeeService.deleteEmployee(empId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/single/{empId}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable UUID empId) {
        return ResponseEntity.ok(employeeService.getEmployeeById(empId));
    }

    @GetMapping("/myProfile")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    public ResponseEntity<Employee> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Employee employee = employeeService.getEmployeeByUsername(username);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        } else {
            return ResponseEntity.notFound().build();
        }

    }
}
