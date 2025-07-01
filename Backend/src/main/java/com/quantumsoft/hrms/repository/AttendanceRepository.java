package com.quantumsoft.hrms.repository;

import com.quantumsoft.hrms.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {

    public Optional<Attendance> findByDate(LocalDate date);

    public List<Attendance> findByDateBetween(LocalDate fromDate, LocalDate toDate);
}
