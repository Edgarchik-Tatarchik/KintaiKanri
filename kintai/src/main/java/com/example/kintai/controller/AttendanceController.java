package com.example.kintai.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kintai.dto.AttendanceRequest;
import com.example.kintai.model.Attendance;
import com.example.kintai.service.AttendanceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/check-in")
    public Attendance checkIn(@Valid @RequestBody AttendanceRequest req) {
        return attendanceService.checkIn(req.getEmployeeId(), req.getWorkDate(), req.getTime());
    }

    @PostMapping("/check-out")
    public Attendance checkOut(@Valid @RequestBody AttendanceRequest req) {
        return attendanceService.checkOut(req.getEmployeeId(), req.getWorkDate(), req.getTime());
    }

    @GetMapping("/{employeeId}")
    public List<Attendance> list(@PathVariable Long employeeId) {
        return attendanceService.listByEmployee(employeeId);
    }
}
