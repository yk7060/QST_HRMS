package com.quantumsoft.hrms.serviceimpl;

import com.quantumsoft.hrms.entity.AuditLog;
import com.quantumsoft.hrms.enums.Action;
import com.quantumsoft.hrms.exception.ResourceNotFoundException;
import com.quantumsoft.hrms.repository.AuditLogRepository;
import com.quantumsoft.hrms.servicei.AuditLogServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogServiceI {

    @Autowired
    private AuditLogRepository repository;

    @Override
    public void logInfo(String username, Action action) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUsername(username);
        auditLog.setAction(action);
        auditLog.setTime(LocalDateTime.now());

        repository.save(auditLog);
    }

    @Override
    public List<AuditLog> getAuditLog(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("AuditLog with given username not found in database"));
    }

    @Override
    public List<AuditLog> getAuditLogs() {
        return repository.findAll();
    }
}
