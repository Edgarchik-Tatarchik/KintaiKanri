package com.example.kintai.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kintai.model.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployee_IdAndWorkDate(Long employeeId, LocalDate workDate);

    List<Attendance> findByEmployee_Id(Long employeeId);
    List<Attendance> findByEmployee_IdAndWorkDateBetween(Long employeeId, LocalDate start, LocalDate end);
}
