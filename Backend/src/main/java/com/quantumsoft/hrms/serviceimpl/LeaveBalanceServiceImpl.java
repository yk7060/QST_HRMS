package com.quantumsoft.hrms.serviceimpl;

import com.quantumsoft.hrms.entity.Employee;
import com.quantumsoft.hrms.entity.LeaveBalance;
import com.quantumsoft.hrms.entity.LeaveType;
import com.quantumsoft.hrms.entity.User;
import com.quantumsoft.hrms.enums.LeaveName;
import com.quantumsoft.hrms.exception.ResourceNotFoundException;
import com.quantumsoft.hrms.repository.EmployeeRepository;
import com.quantumsoft.hrms.repository.LeaveBalanceRepository;
import com.quantumsoft.hrms.repository.LeaveTypeRepository;
import com.quantumsoft.hrms.repository.UserRepository;
import com.quantumsoft.hrms.servicei.LeaveBalanceServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceServiceI {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    /* Why we are not setting available and casual leaves values hardcode because if company
    *  wants change the casual leaves from 12 to 15 then we have th make changes in code but if we call
    *  call casual leaves value from repository then we are not required to do that. ADMIn will change
    *  casual leaves value using functionality update leaveType
    */

    @Override
    public LeaveBalance getLeaveBalance(String username) {
        Optional<LeaveBalance> leaveBalanceOptional = leaveBalanceRepository.findByUsername(username);
        if(leaveBalanceOptional.isEmpty()) {
            LeaveType casualLeave = leaveTypeRepository.findByName(LeaveName.CASUAL_LEAVE);
            LeaveType sickLeave = leaveTypeRepository.findByName(LeaveName.SICK_LEAVE);
            LeaveType paidLeave = leaveTypeRepository.findByName(LeaveName.PAID_LEAVE);
            LeaveType marriageLeave = leaveTypeRepository.findByName(LeaveName.MARRIAGE_LEAVE);
            LeaveType maternityLeave = leaveTypeRepository.findByName(LeaveName.MATERNITY_LEAVE);
            LeaveType paternityLeave = leaveTypeRepository.findByName(LeaveName.PATERNIY_LEAVE);

            User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User record not found in database"));

            Employee employee = employeeRepository.findByUser_userId(user.getUserId()).orElseThrow(() -> new ResourceNotFoundException("Employee record not found in database"));

            LeaveBalance leaveBalance = new LeaveBalance();

            leaveBalance.setUsername(username);
            leaveBalance.setEmployee(employee);
            leaveBalance.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());


            leaveBalance.setAvailableCasualLeaves(casualLeave.getMaxDaysPerYear());
            leaveBalance.setAvailableSickLeaves(sickLeave.getMaxDaysPerYear());
            leaveBalance.setAvailablePaidLeaves(paidLeave.getMaxDaysPerYear());
            leaveBalance.setAvailableMarriageLeaves(marriageLeave.getMaxDaysPerYear());
            leaveBalance.setAvailablePaternityLeaves(paternityLeave.getMaxDaysPerYear());
            leaveBalance.setAvailableMaternityLeaves(maternityLeave.getMaxDaysPerYear());
            leaveBalance.setYear(LocalDate.now().getYear() + "");
            return leaveBalanceRepository.save(leaveBalance);
        }else {
            LeaveBalance leaveBalance = leaveBalanceOptional.get();
            /* to carry forward sick and paid leaves...we are checking current year is equal to saved year or not
               if it is not then leaves will be carry forward */
            if(!leaveBalance.getYear().equals(LocalDate.now().getYear()+"")) {
                LeaveType sickLeave = leaveTypeRepository.findByName(LeaveName.SICK_LEAVE);
                LeaveType paidLeave = leaveTypeRepository.findByName(LeaveName.PAID_LEAVE);
                double availableSickLeaves = leaveBalance.getAvailableSickLeaves() + sickLeave.getMaxDaysPerYear();
                double availablePaidLeaves = leaveBalance.getAvailablePaidLeaves() + paidLeave.getMaxDaysPerYear();
                leaveBalance.setAvailableSickLeaves(availableSickLeaves);
                leaveBalance.setAvailablePaidLeaves(availablePaidLeaves);
                return leaveBalanceRepository.save(leaveBalance);
            }
            return leaveBalance;
        }
    }
}
