package com.quantumsoft.hrms.serviceimpl;

import com.quantumsoft.hrms.entity.Employee;
import com.quantumsoft.hrms.entity.EmployeeBenefit;
import com.quantumsoft.hrms.enums.Status;
import com.quantumsoft.hrms.exception.ResourceNotFoundException;
import com.quantumsoft.hrms.repository.EmployeeBenefitRepository;
import com.quantumsoft.hrms.repository.EmployeeRepository;
import com.quantumsoft.hrms.servicei.EmployeeBenefitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeBenefitServiceImpl implements EmployeeBenefitService {

    private final EmployeeBenefitRepository repository;
    private final EmployeeRepository employeeRepository;

//    @Override
//    public EmployeeBenefit assignBenefit(EmployeeBenefit benefit) {

    @Override
    public EmployeeBenefit assignBenefit(EmployeeBenefit benefit) {

       Employee employee=employeeRepository.findById(benefit.getEmployee().getEmpId())
                .orElseThrow(()->new ResourceNotFoundException("Employee not in dataBase"));
        System.out.println("empId "+employee.getEmpId());

        var oldBenefits =repository.findByEmployee_EmpIdAndBenefit_BenefitIdAndStatus(
          benefit.getEmployee().getEmpId(), benefit.getBenefit().getBenefitId(), String.valueOf(Status.ACTIVE));

        for (EmployeeBenefit eb : oldBenefits) {
            eb.setEmployee(employee);
            eb.setEffectiveTo(LocalDate.now());
            eb.setStatus(Status.DEACTIVE);
            eb.setUpdatedAt(LocalDateTime.now());
            repository.save(eb);
        }
        benefit.setEmployee(employee);
        benefit.setStatus(Status.ACTIVE);
        benefit.setEffectiveFrom(LocalDate.now());
        benefit.setCreatedAt(LocalDateTime.now());
        return repository.save(benefit);



    }

    ////        EmployeeRepository.findById(benefit.getEmployee().getEmpId())
////                .orElseThrow(()->new ResourceNotFoundException("Employee not in dataBase"));
////
////
//
//  var oldBenefits =repository.findByEmployee_EmpIdAndBenefit_BenefitIdAndStatus(
//          benefit.getEmployee().getEmpId(), benefit.getBenefit().getBenefitId(), String.valueOf(Status.ACTIVE));
//        EmployeeRepository.findById(benefit.getEmployee().getEmpId())
//                .orElseThrow(()->new ResourceNotFoundException("Employee not in dataBase"));
//
//
//        for (EmployeeBenefit eb : oldBenefits) {
//            eb.setEffectiveTo(LocalDate.now());
//            eb.setStatus(Status.INACTIVE);
//            eb.setUpdatedAt(LocalDateTime.now());
//            repository.save(eb);
//        }
//
//        benefit.setStatus(Status.ACTIVE);
//        benefit.setEffectiveFrom(LocalDate.now());
//        benefit.setCreatedAt(LocalDateTime.now());
//        return repository.save(benefit);
//    }

    @Override
    public List<EmployeeBenefit> getBenefitsForEmployee(UUID empId) {
        return repository.findByEmployee_EmpId(empId);
    }

    @Override
    public EmployeeBenefit updateBenefit(Long id, EmployeeBenefit updated) {
        EmployeeBenefit existing = repository.findById(id).orElseThrow();
        existing.setAmount(updated.getAmount());
        existing.setNotes(updated.getNotes());
        existing.setUpdatedBy(updated.getUpdatedBy());
        existing.setUpdatedAt(LocalDateTime.now());
        return repository.save(existing);
    }

    @Override
    public void softDeleteBenefit(Long id) {
        EmployeeBenefit existing = repository.findById(id).orElseThrow();
        existing.setStatus(Status.DEACTIVE);
        existing.setEffectiveTo(LocalDate.now());
        existing.setUpdatedAt(LocalDateTime.now());
        repository.save(existing);
    }
}

