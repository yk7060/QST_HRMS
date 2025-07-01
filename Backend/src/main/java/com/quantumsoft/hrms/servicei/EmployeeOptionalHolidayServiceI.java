package com.quantumsoft.hrms.servicei;

import com.quantumsoft.hrms.entity.EmployeeOptionalHoliday;

import java.util.List;
import java.util.UUID;

public interface EmployeeOptionalHolidayServiceI {

    String selectOptionalHoliday(String username, UUID optionalHolidayId);

    String approveRejectEmployeeOptionalHoliday(UUID employeeOptionalHolidayId, String status);

    List<EmployeeOptionalHoliday> fetchAllEmployeeOptionalHoliday(String username);

    EmployeeOptionalHoliday fetchEmployeeOptionalHoliday(UUID employeeOptionalHolidayId);
}
