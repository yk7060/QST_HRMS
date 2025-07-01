package com.quantumsoft.hrms.serviceimpl;

import com.quantumsoft.hrms.entity.Employee;
import com.quantumsoft.hrms.entity.EmployeeOptionalHoliday;
import com.quantumsoft.hrms.entity.OptionalHoliday;
import com.quantumsoft.hrms.entity.User;
import com.quantumsoft.hrms.enums.OptionalHolidayStatus;
import com.quantumsoft.hrms.exception.ResourceNotFoundException;
import com.quantumsoft.hrms.repository.EmployeeOptionalHolidayRepository;
import com.quantumsoft.hrms.repository.EmployeeRepository;
import com.quantumsoft.hrms.repository.OptionalHolidayRepository;
import com.quantumsoft.hrms.repository.UserRepository;
import com.quantumsoft.hrms.servicei.EmployeeOptionalHolidayServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeOptionalHolidayServiceImpl implements EmployeeOptionalHolidayServiceI {

    @Autowired
    private EmployeeOptionalHolidayRepository employeeOptionalHolidayRepository;

    @Autowired
    private OptionalHolidayRepository optionalHolidayRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value(value = "${spring.mail.username}")
    private String from;

    // Employee can apply for the optional holiday in the same month in which optional holiday is occurring.

    @Override
    public String selectOptionalHoliday(String username, UUID optionalHolidayId) {

        // User => EMPLOYEE
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found in database"));

        Employee employee = employeeRepository.findByUser_userId(user.getUserId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found in database"));

        EmployeeOptionalHoliday employeeOptionalHoliday = new EmployeeOptionalHoliday();

        employeeOptionalHoliday.setMonth(LocalDate.now().getMonth().getValue());
        employeeOptionalHoliday.setYear(LocalDate.now().getYear());

        List<EmployeeOptionalHoliday> approvedOptionalHolidays = employeeOptionalHolidayRepository.findByStatus(OptionalHolidayStatus.APPROVED);

        long countOfHolidaysInFirstSixMonth = approvedOptionalHolidays.stream().filter(o -> o.getMonth() <= 6).count();

        long countOfHolidaysInSecondSixMonth = approvedOptionalHolidays.stream().filter(o -> o.getMonth() > 6).count();

        int currentMonth =  LocalDate.now().getMonth().getValue();

        if(currentMonth<=6 && countOfHolidaysInFirstSixMonth==2){
            return "You have reached the limit of optional holidays. You can take only 2 Optional holidays in 1st half year and 2 optional holidays in 2nd half year.";
        }

        if(currentMonth > 6 && countOfHolidaysInSecondSixMonth == 2){
            return "You have reached the limit of optional holidays. You can take only 2 Optional holidays in 1st half year and 2 optional holidays in 2nd half year.";
        }

        employeeOptionalHoliday.setEmployee(employee);
        OptionalHoliday optionalHoliday = optionalHolidayRepository.findById(optionalHolidayId).orElseThrow(() -> new ResourceNotFoundException("OptionalHoliday record not found in database"));
        employeeOptionalHoliday.setOptionalHoliday(optionalHoliday);
        employeeOptionalHoliday.setStatus(OptionalHolidayStatus.PENDING);
        employeeOptionalHolidayRepository.save(employeeOptionalHoliday);

        // User => MANAGER
        User manager = userRepository.findById(employee.getManagerId()).orElseThrow(() -> new ResourceNotFoundException("Manager record not found in database"));

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(manager.getEmail());
        simpleMailMessage.setSubject("HRMS: Optional Holiday Request");
        simpleMailMessage.setText(employee.getFirstName() + " " + employee.getLastName() + " is requesting a OptionalHoliday leave with employeeOptionalHolidayId " + employeeOptionalHoliday.getEmployeeOptionalHolidayId());

        javaMailSender.send(simpleMailMessage);

        return "Employee OptionalHoliday request is saved and send to Manager/HR";
    }

    @Override
    public String approveRejectEmployeeOptionalHoliday(UUID employeeOptionalHolidayId, String status) {
        EmployeeOptionalHoliday employeeOptionalHoliday = employeeOptionalHolidayRepository.findById(employeeOptionalHolidayId).orElseThrow(() -> new ResourceNotFoundException("EmployeeOptionalHoliday record not found in database"));
        if(status.equals("APPROVED")){
            employeeOptionalHoliday.setStatus(OptionalHolidayStatus.APPROVED);
            employeeOptionalHolidayRepository.save(employeeOptionalHoliday);

            Employee employee = employeeOptionalHoliday.getEmployee();

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(from);
            simpleMailMessage.setTo(employee.getEmail());
            simpleMailMessage.setSubject("HRMS: Optional Holiday Approval");
            simpleMailMessage.setText(employee.getFirstName() + " " + employee.getLastName() + ", your request for holiday on " + employeeOptionalHoliday.getOptionalHoliday().getDate() + " is approved");

            javaMailSender.send(simpleMailMessage);

            return "Employee request approved.";
        }else{
            employeeOptionalHoliday.setStatus(OptionalHolidayStatus.REJECT);
            employeeOptionalHolidayRepository.save(employeeOptionalHoliday);

            Employee employee = employeeOptionalHoliday.getEmployee();

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(from);
            simpleMailMessage.setTo(employee.getEmail());
            simpleMailMessage.setSubject("HRMS: Optional Holiday Approval");
            simpleMailMessage.setText(employee.getFirstName() + " " + employee.getLastName() + ", your request for holiday on " + employeeOptionalHoliday.getOptionalHoliday().getDate() + " is rejected");

            javaMailSender.send(simpleMailMessage);

            return "Employee request approved.";
        }
    }

    @Override
    public List<EmployeeOptionalHoliday> fetchAllEmployeeOptionalHoliday(String username) {
        // User => EMPLOYEE
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found in database"));

        Employee employee = employeeRepository.findByUser_userId(user.getUserId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found in database"));

        return employeeOptionalHolidayRepository.findByEmployee_empId(employee.getEmpId());
    }

    @Override
    public EmployeeOptionalHoliday fetchEmployeeOptionalHoliday(UUID employeeOptionalHolidayId) {
        return employeeOptionalHolidayRepository.findById(employeeOptionalHolidayId).orElseThrow(() -> new ResourceNotFoundException("EmployeeOptionalHoliday record not found in database"));
    }
}
