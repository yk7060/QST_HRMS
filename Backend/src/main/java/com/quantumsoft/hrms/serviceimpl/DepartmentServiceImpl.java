package com.quantumsoft.hrms.serviceimpl;

import com.quantumsoft.hrms.entity.Department;
import com.quantumsoft.hrms.exception.DuplicateResourceException;
import com.quantumsoft.hrms.exception.ResourceNotFoundException;
import com.quantumsoft.hrms.repository.DepartmentRepository;
import com.quantumsoft.hrms.repository.EmployeeRepository;
import com.quantumsoft.hrms.servicei.DepartmentServicei;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentServicei {

    @Autowired
    private DepartmentRepository departmentRepository;
    private EmployeeRepository employeeRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
    }

    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public Department createDepartment(Department department) {
        if (departmentRepository.findByDepartmentCode(department.getDepartmentCode()).isPresent()) {
            throw new DuplicateResourceException("Department code already exists.");
        }
        return departmentRepository.save(department);
    }

    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public Department updateDepartment(Long id, Department department) {
        Department existing = getDepartmentById(id);
        if (!existing.getDepartmentCode().equals(department.getDepartmentCode())
                && departmentRepository.findByDepartmentCode(department.getDepartmentCode()).isPresent()) {
            throw new DuplicateResourceException("Department code already exists.");
        }

        existing.setDepartmentCode(department.getDepartmentCode());
        existing.setName(department.getName());
        existing.setDescription(department.getDescription());
        existing.setLocation(department.getLocation());
        existing.setDepartmentHeadId(department.getDepartmentHeadId());
        return departmentRepository.save(existing);
    }

    @Override
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);

    }


}


//
//    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
//    public void deleteDepartment(Long id) {
//        if (employeeRepository.existsByDepartmentId(id)) {
//            throw new InvalidOperationException("Cannot delete department with active employees.");
//        }
//        departmentRepository.deleteById(id);
//
//   }
//}
