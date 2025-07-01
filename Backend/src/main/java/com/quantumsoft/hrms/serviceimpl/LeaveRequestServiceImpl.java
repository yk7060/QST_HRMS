package com.quantumsoft.hrms.serviceimpl;

import com.quantumsoft.hrms.entity.*;
import com.quantumsoft.hrms.enums.LeaveName;
import com.quantumsoft.hrms.enums.LeaveStatus;
import com.quantumsoft.hrms.exception.ResourceNotFoundException;
import com.quantumsoft.hrms.repository.*;
import com.quantumsoft.hrms.servicei.LeaveRequestServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestServiceI {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value(value = "${spring.mail.username}")
    private String from;

    @Override
    public String addLeaveRequest(String username, LeaveRequest leaveRequest) {
        try {
            leaveRequest.setMonth(LocalDate.now().getMonth().toString());
            leaveRequest.setYear(LocalDate.now().getYear() + "");

            // check no of casual leaves are greater than 1 or not
            if(leaveRequest.getLeaveType().getName().equals(LeaveName.CASUAL_LEAVE)) {
                double noOfLeaves = (double) ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;
                if(noOfLeaves>1){
                    return "Employee cannot take more than 1 casual leave per month.";
                }
            }
            // check employee has already taken casual leave or not
            if(leaveRequest.getLeaveType().getName().equals(LeaveName.CASUAL_LEAVE) && leaveRequestRepository.existsByYearAndMonthAndLeaveType_name(leaveRequest.getYear(), leaveRequest.getMonth(), leaveRequest.getLeaveType().getName())){
                return "You can take only one casual leave per month";
            }

            LeaveBalance leaveBalance = leaveBalanceRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Leave Balance record not found in database"));

            // checking employee has sick leaves available in leave balance or not
            if(leaveRequest.getLeaveType().getName().equals(LeaveName.SICK_LEAVE) && leaveBalance.getAvailableSickLeaves()==0) {
                return "No sick leaves available";
            }

            if(leaveRequest.getLeaveType().getName().equals(LeaveName.PAID_LEAVE) && leaveBalance.getAvailablePaidLeaves()==0) {
                return "No paid leaves available";
            }

            if(leaveRequest.getLeaveType().getName().equals(LeaveName.MARRIAGE_LEAVE) && leaveBalance.getAvailableMarriageLeaves()==0) {
                return "No marriage leaves available";
            }

            if(leaveRequest.getLeaveType().getName().equals(LeaveName.MATERNITY_LEAVE) && leaveBalance.getAvailableMaternityLeaves()==0) {
                return "No maternity leaves available";
            }

            if(leaveRequest.getLeaveType().getName().equals(LeaveName.PATERNIY_LEAVE) && leaveBalance.getAvailablePaternityLeaves()==0) {
                return "No paternity leaves available";
            }

            User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User record not found in database"));
            Employee employee = employeeRepository.findByUser_userId(user.getUserId()).orElseThrow(() -> new ResourceNotFoundException("Employee record not found in database"));
            Employee manager = employeeRepository.findById(employee.getManagerId()).orElseThrow(() -> new ResourceNotFoundException("Manager record not found in database"));

            leaveRequest.setEmployee(employee);

            LeaveType leaveType = leaveTypeRepository.findByName(leaveRequest.getLeaveType().getName());
            leaveRequest.setLeaveType(leaveType);

            leaveRequest.setStatus(LeaveStatus.PENDING);

            leaveRequestRepository.save(leaveRequest);

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(from);
            simpleMailMessage.setTo(manager.getEmail());
            simpleMailMessage.setSubject("HRMS: Leave Request");
            simpleMailMessage.setText(employee.getFirstName() + " " + employee.getLastName() + " is requesting a leave with leaveRequestId " + leaveRequest.getLeaveRequestId());

            javaMailSender.send(simpleMailMessage);

            return "Leave request added successfully..!";
        }catch(Exception e){
            return "Failed to add leave request: " + e.getMessage();
        }
    }

    @Override
    public String approveRejectedLeaveRequest(UUID leaveRequestId, String status) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId).orElseThrow(() -> new ResourceNotFoundException("Leave request record not found in database"));
        if(status.equals("APPROVED")){
            leaveRequest.setStatus(LeaveStatus.APPROVED);
            leaveRequestRepository.save(leaveRequest);
            LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployee_empId(leaveRequest.getEmployee().getEmpId()).orElseThrow(() -> new ResourceNotFoundException("Leave Balance record not found in database"));
            double noOfLeaves = (double) ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;

            // if employee is requesting for half days so here we are calculating noOfHalfDays
            if(leaveRequest.getNoOfHalfDays()!=0){
                noOfLeaves = noOfLeaves - (leaveRequest.getNoOfHalfDays())/2;
            }
            System.out.println(leaveRequest.getLeaveType().getName().equals(LeaveName.SICK_LEAVE));
            System.out.println("no of leaves: " + noOfLeaves);
            if(leaveRequest.getLeaveType().getName().equals(LeaveName.SICK_LEAVE)){
                leaveBalance.setConsumedSickLeaves(noOfLeaves);
                System.out.println("consumed sick leaves: " + leaveBalance.getConsumedCasualLeaves());
                Double remainingSickLeaves = leaveBalance.getAvailableSickLeaves() - noOfLeaves;
                System.out.println("available sick leaves: " + remainingSickLeaves);
                leaveBalance.setAvailableSickLeaves(remainingSickLeaves);
            }
            else if(leaveRequest.getLeaveType().getName().equals(LeaveName.CASUAL_LEAVE)){
                leaveBalance.setConsumedCasualLeaves(noOfLeaves);
                Double remainingCasualLeaves = leaveBalance.getAvailableCasualLeaves() - noOfLeaves;
                leaveBalance.setAvailableCasualLeaves(remainingCasualLeaves);
            }
            else if(leaveRequest.getLeaveType().getName().equals(LeaveName.MATERNITY_LEAVE)){
                leaveBalance.setConsumedMaternityLeaves(noOfLeaves);
                Double remainingMaternityLeaves = leaveBalance.getAvailableMaternityLeaves() - noOfLeaves;
                leaveBalance.setAvailableMaternityLeaves(remainingMaternityLeaves);
            }
            else if(leaveRequest.getLeaveType().getName().equals(LeaveName.PATERNIY_LEAVE)){
                leaveBalance.setConsumedPaternityLeaves(noOfLeaves);
                Double remainingPaternityLeaves = leaveBalance.getAvailablePaternityLeaves() - noOfLeaves;
                leaveBalance.setAvailablePaternityLeaves(remainingPaternityLeaves);
            }
            else if(leaveRequest.getLeaveType().getName().equals(LeaveName.MARRIAGE_LEAVE)){
                leaveBalance.setConsumedMarriageLeaves(noOfLeaves);
                Double remainingMarriageLeaves = leaveBalance.getAvailableMarriageLeaves() - noOfLeaves;
                leaveBalance.setAvailableMarriageLeaves(remainingMarriageLeaves);
            }
            leaveBalanceRepository.save(leaveBalance);

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(from);
            simpleMailMessage.setTo(leaveRequest.getEmployee().getEmail());
            simpleMailMessage.setSubject("HRMS: Leave Request Status");
            simpleMailMessage.setText("Hi " + leaveRequest.getEmployee().getFirstName() + " " + leaveRequest.getEmployee().getLastName() + "\n\n Your request of " + leaveRequest.getLeaveType().getName() + " has been " + leaveRequest.getStatus().name());

            javaMailSender.send(simpleMailMessage);

            return "Leave request is approved";
        }else {
            leaveRequest.setStatus(LeaveStatus.REJECT);

            leaveRequestRepository.save(leaveRequest);

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(from);
            simpleMailMessage.setTo(leaveRequest.getEmployee().getEmail());
            simpleMailMessage.setSubject("HRMS: Leave Request Status");
            simpleMailMessage.setText("Hi " + leaveRequest.getEmployee().getFirstName() + " " + leaveRequest.getEmployee().getLastName() + "\n\n Your request of " + leaveRequest.getLeaveType().getName() + " has been " + leaveRequest.getStatus().name());

            javaMailSender.send(simpleMailMessage);

            return "Leave request is rejected.";
        }
    }

    @Override
    public List<LeaveRequest> trackLeaveRequest(UUID empId, String status) {
        if(status.equals("APPROVED")) {
            return leaveRequestRepository.findByEmployee_empIdAndStatus(empId, LeaveStatus.APPROVED);
        }
        else if(status.equals("REJECT")){
            return leaveRequestRepository.findByEmployee_empIdAndStatus(empId, LeaveStatus.REJECT);
        }else {
            return leaveRequestRepository.findByEmployee_empIdAndStatus(empId, LeaveStatus.PENDING);
        }
    }

    @Override
    public LeaveRequest fetchLeaveRequest(UUID leaveRequestId) {
        return leaveRequestRepository.findById(leaveRequestId).orElseThrow(() -> new ResourceNotFoundException("LeaveRequest record not found in database"));
    }
}
