package com.example.kintai.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
    name = "attendances",
    uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "work_date"})
)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull(message = "workDate is required")
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "check_in")
    private LocalTime checkIn;

    @Column(name = "check_out")
    private LocalTime checkOut;

    @Min(value = 0, message = "breakMinutes must be >= 0")
    @Column(name = "break_minutes", nullable = false)
    private int breakMinutes = 60; 

    protected Attendance() {}

    public Attendance(Employee employee, LocalDate workDate) {
        this.employee = employee;
        this.workDate = workDate;
    }

    public Long getId() { return id; }
    public Employee getEmployee() { return employee; }
    public LocalDate getWorkDate() { return workDate; }
    public LocalTime getCheckIn() { return checkIn; }
    public LocalTime getCheckOut() { return checkOut; }
    public int getBreakMinutes() { return breakMinutes; }

    public void setCheckIn(LocalTime checkIn) { this.checkIn = checkIn; }
    public void setCheckOut(LocalTime checkOut) { this.checkOut = checkOut; }
    public void setBreakMinutes(int breakMinutes) { this.breakMinutes = breakMinutes; }
    @Transient
    public Integer getWorkedMinutes() {
        if (checkIn == null || checkOut == null) return null;

        int in = checkIn.toSecondOfDay();
        int out = checkOut.toSecondOfDay();

        int minutes = (out - in) / 60 - breakMinutes;
        return Math.max(minutes, 0);
    }

    @Transient
    public String getWorkedTime() {
        Integer m = getWorkedMinutes();
        if (m == null) return "-";
        int h = m / 60;
        int mm = m % 60;
        return String.format("%d:%02d", h, mm);
    }
    @Transient
    public String getStatusLabel() {
        if (checkIn == null) return "未出勤";
        if (checkOut == null) return "出勤中";
        return "退勤済";
    }

    @Transient
    public String getStatusClass() {
        if (checkIn == null) return "status-work"; 
        if (checkOut == null) return "status-in";  
        return "status-out";                       
    }
}
