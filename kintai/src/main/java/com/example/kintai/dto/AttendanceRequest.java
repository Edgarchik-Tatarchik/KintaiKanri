package com.example.kintai.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public class AttendanceRequest {
    @NotNull(message = "employeeId is required")
    private Long employeeId;

    @NotNull(message = "workDate is required")
    private LocalDate workDate;

    @NotNull(message = "time is required")
    private LocalTime time;

    public Long getEmployeeId() { return employeeId; }
    public LocalDate getWorkDate() { return workDate; }
    public LocalTime getTime() { return time; }

    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }
    public void setTime(LocalTime time) { this.time = time; }
}
