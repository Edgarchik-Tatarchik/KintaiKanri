package com.example.kintai.controller;


import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kintai.dto.EmployeeForm;
import com.example.kintai.model.Attendance;
import com.example.kintai.model.Employee;
import com.example.kintai.service.AttendanceService;
import com.example.kintai.service.EmployeeService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/employees")
public class EmployeeUiController {

    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;

    public EmployeeUiController(EmployeeService employeeService,
                                AttendanceService attendanceService) {
        this.employeeService = employeeService;
        this.attendanceService = attendanceService;
    }

    
    @GetMapping
    public String list(Model model) {
        model.addAttribute("employees", employeeService.findAll());
        return "employees-list";
    }

    
    @GetMapping("/{id:\\d+}")
    public String details(@PathVariable Long id, Model model, RedirectAttributes ra) {
        
    return employeeService.findById(id)
        .map(employee -> {
            List<Attendance> attendances =
                        attendanceService.listByEmployee(id);

                
                int totalMinutes = attendances.stream()
                        .map(Attendance::getWorkedMinutes)
                        .filter(m -> m != null)
                        .mapToInt(Integer::intValue)
                        .sum();
            model.addAttribute("employee", employee);
            model.addAttribute("attendances", attendances);
            model.addAttribute("today", java.time.LocalDate.now());
            model.addAttribute("totalWorkedTime", formatMinutes(totalMinutes));
            return "employee-details";
        })
        .orElseGet(() -> {
            ra.addFlashAttribute("errorMessage", "社員が存在しません");
            return "redirect:/ui/employees";
        });
        
    }
    private String formatMinutes(int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;
        return String.format("%d:%02d", h, m);
    }
    @GetMapping("/new")
public String newEmployeeForm(Model model) {
    model.addAttribute("employee", new EmployeeForm());
    return "employee-new";
}

    @PostMapping
    public String createEmployee(@Valid @ModelAttribute("employee") EmployeeForm form,
                             BindingResult bindingResult,
                             RedirectAttributes ra) {

    if (bindingResult.hasErrors()) {
        return "employee-new";
    }

    

    Employee saved = employeeService.save(
    Employee.create(form.getName(), form.getEmail(), form.getDepartment())
    );

    ra.addFlashAttribute("successMessage", "社員を作成しました");
    return "redirect:/ui/employees/" + saved.getId();
}
}
