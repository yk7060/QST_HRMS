package com.quantumsoft.hrms.repository;

import com.quantumsoft.hrms.entity.EmployeeOptionalHoliday;
import com.quantumsoft.hrms.enums.OptionalHolidayStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeOptionalHolidayRepository extends JpaRepository<EmployeeOptionalHoliday, UUID> {

    List<EmployeeOptionalHoliday> findByStatus(OptionalHolidayStatus optionalHolidayStatus);

    List<EmployeeOptionalHoliday> findByEmployee_empId(UUID empId);
}
