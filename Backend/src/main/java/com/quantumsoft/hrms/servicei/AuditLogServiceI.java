package com.quantumsoft.hrms.servicei;

import com.quantumsoft.hrms.entity.AuditLog;
import com.quantumsoft.hrms.enums.Action;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogServiceI {
   public void logInfo(String username, Action action);

   public List<AuditLog> getAuditLog(String username);

   public List<AuditLog> getAuditLogs();
}
