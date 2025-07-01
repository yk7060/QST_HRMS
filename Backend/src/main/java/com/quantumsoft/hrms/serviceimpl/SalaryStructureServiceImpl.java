package com.quantumsoft.hrms.serviceimpl;

import com.quantumsoft.hrms.dto.SalaryStructureDto;
import com.quantumsoft.hrms.entity.Employee;
import com.quantumsoft.hrms.entity.SalaryStructure;
import com.quantumsoft.hrms.enums.SalaryStructureStatus;
import com.quantumsoft.hrms.exception.ResourceNotFoundException;
import com.quantumsoft.hrms.repository.EmployeeRepository;
import com.quantumsoft.hrms.repository.SalaryStructureRepository;
import com.quantumsoft.hrms.servicei.SalaryStructureServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SalaryStructureServiceImpl implements SalaryStructureServiceI {

    @Autowired
    private SalaryStructureRepository salaryStructureRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public String assignSalaryStructureToEmployee(SalaryStructure salaryStructure) {
        double totalEarning = salaryStructure.getBasicSalary() + salaryStructure.getHra()
                + salaryStructure.getBonus() + salaryStructure.getSpecialAllowance();
        double totalDeductions = salaryStructure.getPfDeduction() + salaryStructure.getTaxDeduction();

        /* Total earning must be greater than total deductions */
        if(totalEarning>totalDeductions) {

            /* If we are assigning new SalaryStructure to employee then we have to expire the older one and add new one
               So we are finding the salary structure by using employee id and which must be active because there must be
               only one ACTIVE salary structure, we are expiring this salary structure by setting effectiveTo value and
               status value to INACTIVE */

            Optional<SalaryStructure> optionalSalaryStructure = salaryStructureRepository.findByEmployee_empIdAndStatus(salaryStructure.getEmployee().getEmpId(), SalaryStructureStatus.ACTIVE);
            if(optionalSalaryStructure.isPresent()) {
                SalaryStructure olderSalaryStructure = optionalSalaryStructure.get();
                olderSalaryStructure.setEffectiveTo(LocalDate.now());
                olderSalaryStructure.setStatus(SalaryStructureStatus.INACTIVE);
                salaryStructureRepository.save(olderSalaryStructure);
            }
                Employee employee = employeeRepository.findById(salaryStructure.getEmployee().getEmpId()).orElseThrow(() -> new ResourceNotFoundException("Employee record not found in database"));
                salaryStructure.setEmployee(employee);

                double totalCtc = totalEarning - totalDeductions;
                salaryStructure.setTotalCTC(totalCtc);
                salaryStructure.setStatus(SalaryStructureStatus.ACTIVE);

                salaryStructureRepository.save(salaryStructure);

                return "Salary structure is assigned to employee...!";
        }else
            return "Total Earning must be greater than total deduction";
    }

    @Override
    public String updateSalaryStructureOfEmployee(UUID salaryStructureId, SalaryStructure salaryStructure) {
        Optional<SalaryStructure> salaryStructureOptional = salaryStructureRepository.findById(salaryStructureId);
        if(salaryStructureOptional.isPresent()) {
            double totalEarning = salaryStructure.getBasicSalary() + salaryStructure.getHra()
                    + salaryStructure.getBonus() + salaryStructure.getSpecialAllowance();
            double totalDeductions = salaryStructure.getPfDeduction() + salaryStructure.getTaxDeduction();
            if(totalEarning>totalDeductions) {
                double totalCtc = totalEarning - totalDeductions;
                salaryStructure.setTotalCTC(totalCtc);
                salaryStructureRepository.save(salaryStructure);
                return "Salary structure updated successfully...!";
            }else
                return "Total Earning must be greater than total deduction";
        }else
            throw new ResourceNotFoundException("Salary structure record not found in database");
    }

    @Override
    public SalaryStructure getSalaryStructureOfEmployee(UUID salaryStructureId) {
        return salaryStructureRepository.findById(salaryStructureId).orElseThrow(() -> new ResourceNotFoundException("Salary Structure record no found in database"));

//        SalaryStructureDto salaryStructureDto = new SalaryStructureDto();
//
//        salaryStructureDto.setSalaryStructureId(salaryStructure.getSalaryStructureId());
//        salaryStructureDto.setEmpId(salaryStructure.getEmployee().getEmpId());
//        salaryStructureDto.setName(salaryStructure.getEmployee().getFirstName() + " " + salaryStructure.getEmployee().getLastName());
//        salaryStructureDto.setBasicSalary(salaryStructure.getBasicSalary());
//        salaryStructureDto.setHra(salaryStructure.getHra());
//        salaryStructureDto.setSpecialAllowance(salaryStructure.getSpecialAllowance());
//        salaryStructureDto.setBonus(salaryStructure.getBonus());
//        salaryStructureDto.setPfDeduction(salaryStructure.getPfDeduction());
//        salaryStructureDto.setTaxDeduction(salaryStructure.getTaxDeduction());
//        salaryStructureDto.setTotalCTC(salaryStructure.getTotalCTC());
//        salaryStructureDto.setStatus(salaryStructure.getStatus());
//        salaryStructureDto.setEffectiveFrom(salaryStructure.getEffectiveFrom());
//        salaryStructureDto.setEffectiveTo(salaryStructure.getEffectiveTo());
//        salaryStructureDto.setCreatedDate(salaryStructure.getCreatedDate());
//        salaryStructureDto.setUpdatedDate(salaryStructure.getUpdatedDate());

//        return salaryStructureDto;
    }

    @Override
    public void deleteSalaryStructureOfEmployee(UUID salaryStructureId) {
        try {
            SalaryStructure salaryStructure = salaryStructureRepository.findById(salaryStructureId).orElseThrow(() -> new ResourceNotFoundException("Salary Structure record no found in database"));
            salaryStructureRepository.delete(salaryStructure);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<SalaryStructure> getAllSalaryStructures() {
        return salaryStructureRepository.findAll();
    }
    @Override
    public SalaryStructure getByEmployeeId(UUID empId) {
        return salaryStructureRepository.findByEmployeeEmpId(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Salary Structure not found for the given Employee ID."));
    }

}
