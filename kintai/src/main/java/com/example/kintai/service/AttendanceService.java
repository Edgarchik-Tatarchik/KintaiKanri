package com.example.kintai.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.kintai.model.Attendance;

public interface AttendanceService {
    Attendance checkIn(Long employeeId, LocalDate workDate, LocalTime checkInTime);
    Attendance checkOut(Long employeeId, LocalDate workDate, LocalTime checkOutTime);
    Attendance updateBreak(Long employeeId, LocalDate workDate, int breakMinutes);
    List<Attendance> listByEmployee(Long employeeId);
}
