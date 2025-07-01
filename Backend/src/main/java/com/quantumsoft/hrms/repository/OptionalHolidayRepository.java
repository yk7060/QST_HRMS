package com.quantumsoft.hrms.repository;

import com.quantumsoft.hrms.entity.OptionalHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OptionalHolidayRepository extends JpaRepository<OptionalHoliday, UUID> {

}
