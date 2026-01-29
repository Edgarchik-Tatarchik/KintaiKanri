package com.example.kintai.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.kintai.model.Attendance;
import com.example.kintai.repository.AttendanceRepository;
import com.example.kintai.repository.EmployeeRepository;

@Controller
public class DashboardUiController {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public DashboardUiController(AttendanceRepository attendanceRepository,
                                 EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping({"/", "/ui"})
    public String dashboard(Model model) {
        LocalDate today = LocalDate.now();

        long totalEmployees = employeeRepository.count();
        long checkedInToday = attendanceRepository.findByWorkDate(today).size();
        List<Attendance> notCheckedOut = attendanceRepository
                .findByWorkDateAndCheckInIsNotNullAndCheckOutIsNull(today);

        model.addAttribute("today", today);
        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("checkedInToday", checkedInToday);
        model.addAttribute("notCheckedOutCount", notCheckedOut.size());
        model.addAttribute("notCheckedOutList", notCheckedOut);
        notCheckedOut.forEach(a -> a.getEmployee().getName());
        
        return "dashboard";
    }
}
