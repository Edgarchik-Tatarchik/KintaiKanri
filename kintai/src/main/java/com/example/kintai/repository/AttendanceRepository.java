package com.example.kintai.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.kintai.model.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployee_IdAndWorkDate(Long employeeId, LocalDate workDate);
    @Query("""
    select a from Attendance a
    join fetch a.employee e
    where a.workDate = :workDate
        and a.checkIn is not null
        and a.checkOut is null
    """)
    List<Attendance> findNotCheckedOutWithEmployee(@Param("workDate") LocalDate workDate);
    List<Attendance> findByEmployee_Id(Long employeeId);
    List<Attendance> findByEmployee_IdAndWorkDateBetween(Long employeeId, LocalDate start, LocalDate end);
    List<Attendance> findByWorkDate(LocalDate workDate);
    List<Attendance> findByWorkDateAndCheckInIsNotNullAndCheckOutIsNull(LocalDate workDate);
    
}
