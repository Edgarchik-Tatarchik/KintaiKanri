package com.example.kintai.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import com.example.kintai.model.Attendance;



public interface AttendanceService {
    Attendance checkIn(Long employeeId, LocalDate workDate, LocalTime checkInTime);
    Attendance checkOut(Long employeeId, LocalDate workDate, LocalTime checkOutTime);
    Attendance updateBreak(Long employeeId, LocalDate workDate, int breakMinutes);
    Attendance updateAttendance(Long attendanceId, LocalTime checkIn, LocalTime checkOut, int breakMinutes);
    Attendance findById(Long attendanceId);
    List<Attendance> listByEmployeeAndMonth(Long employeeId, YearMonth month);
    List<Attendance> listByEmployee(Long employeeId);
    Optional<Attendance> findTodayByEmployee(Long employeeId, LocalDate date);
}
