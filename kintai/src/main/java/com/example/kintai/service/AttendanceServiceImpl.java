package com.example.kintai.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.time.YearMonth;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.kintai.model.Attendance;
import com.example.kintai.model.Employee;
import com.example.kintai.repository.AttendanceRepository;
import com.example.kintai.repository.EmployeeRepository;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                 EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Attendance checkIn(Long employeeId, LocalDate workDate, LocalTime checkInTime) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        Attendance attendance = attendanceRepository.findByEmployee_IdAndWorkDate(employeeId, workDate)
                .orElseGet(() -> new Attendance(employee, workDate));

        if (attendance.getCheckIn() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already checked in");
        }

        attendance.setCheckIn(checkInTime);
        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance checkOut(Long employeeId, LocalDate workDate, LocalTime checkOutTime) {
        Attendance attendance = attendanceRepository.findByEmployee_IdAndWorkDate(employeeId, workDate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found"));

        if (attendance.getCheckIn() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Check-in required before check-out");
        }

        if (attendance.getCheckOut() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already checked out");
        }

        if (checkOutTime.isBefore(attendance.getCheckIn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check-out cannot be before check-in");
        }

        attendance.setCheckOut(checkOutTime);
        return attendanceRepository.save(attendance);
    }

    @Override
    public List<Attendance> listByEmployee(Long employeeId) {
        
        return attendanceRepository.findByEmployee_Id(employeeId);
    }
    @Override
    public List<Attendance> listByEmployeeAndMonth(Long employeeId, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        return attendanceRepository.findByEmployee_IdAndWorkDateBetween(employeeId, start, end);
    }
    
    @Override
    public Attendance updateBreak(Long employeeId, LocalDate workDate, int breakMinutes) {
        Attendance attendance = attendanceRepository.findByEmployee_IdAndWorkDate(employeeId, workDate)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found"));

        attendance.setBreakMinutes(breakMinutes);
        return attendanceRepository.save(attendance);
}
}
