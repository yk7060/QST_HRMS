package com.quantumsoft.hrms.controller;

import com.quantumsoft.hrms.entity.Department;
import com.quantumsoft.hrms.servicei.DepartmentServicei;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private  DepartmentServicei  departmentServicei;

    public DepartmentController(DepartmentServicei departmentServicei) {
        this.departmentServicei = departmentServicei;
    }

    @GetMapping
    public List<Department> getAll() {
        return departmentServicei.getAllDepartments();
    }

    @GetMapping("/{id}")
    public Department getById(@PathVariable Long id) {
        return departmentServicei.getDepartmentById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public Department create(@Valid @RequestBody Department dept) {

        return departmentServicei.createDepartment(dept);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public Department update(@PathVariable Long id, @Valid @RequestBody Department dept) {
        return departmentServicei.updateDepartment(id, dept);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public void delete(@PathVariable Long id) {
        departmentServicei.deleteDepartment(id);
    }
}
