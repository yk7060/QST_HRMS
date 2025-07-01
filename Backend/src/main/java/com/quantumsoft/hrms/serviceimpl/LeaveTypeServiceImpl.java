package com.quantumsoft.hrms.serviceimpl;

import com.quantumsoft.hrms.entity.LeaveType;
import com.quantumsoft.hrms.exception.ResourceNotFoundException;
import com.quantumsoft.hrms.repository.LeaveTypeRepository;
import com.quantumsoft.hrms.servicei.LeaveTypeServiceI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LeaveTypeServiceImpl implements LeaveTypeServiceI {

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    private final Logger logger = LoggerFactory.getLogger(LeaveTypeServiceImpl.class);

    @Override
    public String addLeaveType(LeaveType leaveType) {
        try {
            leaveTypeRepository.save(leaveType);
            return "LeaveType added successfully...!";
        }catch (Exception e){
            return "LeaveType failed to save in database: " + e.getMessage();
        }
    }

    @Override
    public String updateLeaveType(UUID leaveTypeId, LeaveType leaveType) {
        Optional<LeaveType> leaveTypeOptional = leaveTypeRepository.findById(leaveTypeId);
        if(leaveTypeOptional.isPresent()) {
            leaveTypeRepository.save(leaveType);
            return "LeaveType record updated successfully...!";
        }else
            throw new ResourceNotFoundException("LeaveType record failed to update");
    }

    @Override
    public LeaveType getLeaveType(UUID leaveTypeId) {
        return leaveTypeRepository.findById(leaveTypeId).orElseThrow(() -> new ResourceNotFoundException("LeaveType record not found in database"));
    }

    @Override
    public List<LeaveType> getLeaveTypes() {
        return leaveTypeRepository.findAll();
    }

    @Override
    public void deleteLeaveType(UUID leaveTypeId) {
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId).orElseThrow(() -> new ResourceNotFoundException("LeaveType record not found in database"));
        leaveTypeRepository.delete(leaveType);
    }
}
