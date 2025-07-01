package com.quantumsoft.hrms.serviceimpl;

import com.quantumsoft.hrms.entity.BankDetail;
import com.quantumsoft.hrms.entity.Employee;
import com.quantumsoft.hrms.entity.User;
import com.quantumsoft.hrms.exception.ResourceNotFoundException;
import com.quantumsoft.hrms.repository.BankDetailRepository;
import com.quantumsoft.hrms.repository.EmployeeRepository;
import com.quantumsoft.hrms.repository.UserRepository;
import com.quantumsoft.hrms.servicei.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private UserRepository userRepository;

    private String uploadDir;

    @Autowired
    private BankDetailRepository bankDetailRepository;

    @Override
    public Employee createEmployee(Employee employee) {
        User user = userRepository.findById(employee.getUser().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User record not found in database"));
        employee.setUser(user);
        return repository.save(employee);
    }

    @Override
    public void deleteEmployee(UUID empId) {
        repository.deleteById(empId);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    @Override
    public Employee getEmployeeById(UUID empId) {
        return repository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + empId + " not found"));
    }

    @Override
    public Employee assignManagerToEmployee(UUID empId, UUID managerId) {
        Employee employee = repository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee record not found in database"));
        Employee manager = repository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager record not found in database"));
        employee.setManagerId(manager.getEmpId());
        return repository.save(employee);
    }

    @Override
    public Employee addBankDetailsToEmployee(UUID empId, Long bankDetailsId) {
        Employee employee = repository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee record not found in database"));
        BankDetail bankDetail = bankDetailRepository.findById(bankDetailsId)
                .orElseThrow(() -> new ResourceNotFoundException("BankDetails record not found in database"));
        employee.setBankDetailsId(bankDetail);
        return repository.save(employee);
    }

    @Override
    public Employee getEmployeeByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return repository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for user: " + username));
    }

    private String savePhoto(MultipartFile photo) {
        try {
            String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
            File uploadDir = new File("uploads/employees");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            File destination = new File(uploadDir, fileName);
            photo.transferTo(destination);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Photo upload failed", e);
        }
    }
}
