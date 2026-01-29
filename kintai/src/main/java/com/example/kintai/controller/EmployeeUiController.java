package com.example.kintai.controller;


import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kintai.dto.EmployeeForm;
import com.example.kintai.model.Attendance;
import com.example.kintai.model.Employee;
import com.example.kintai.service.AttendanceService;
import com.example.kintai.service.EmployeeService;

import jakarta.persistence.PersistenceException;
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
    public String list(@RequestParam(required = false) String keyword,
                   Model model) {

        var employees = (keyword == null || keyword.isBlank())
            ? employeeService.findAll()
            : employeeService.searchByName(keyword);

        model.addAttribute("employees", employees);
        model.addAttribute("keyword", keyword);

    
        var today = java.time.LocalDate.now();
        Map<Long, String> statusLabelMap = new HashMap<>();
        Map<Long, String> statusClassMap = new HashMap<>();

        for (Employee e : employees) {
            attendanceService.findTodayByEmployee(e.getId(), today)
                .ifPresentOrElse(a -> {
                    statusLabelMap.put(e.getId(), a.getStatusLabel());
                    statusClassMap.put(e.getId(), a.getStatusClass());
                }, () -> {
                    statusLabelMap.put(e.getId(), "未出勤");
                    statusClassMap.put(e.getId(), "status-work");
                });
        }

        model.addAttribute("todayStatusLabel", statusLabelMap);
        model.addAttribute("todayStatusClass", statusClassMap);

        return "employees-list";
    }

    
    @GetMapping("/{id:\\d+}")
    public String details(@PathVariable Long id, @RequestParam(required = false) String month, Model model, RedirectAttributes ra) {
        
    return employeeService.findById(id)
         .map(employee -> {

            YearMonth ym;
            try {
                ym = (month == null || month.isBlank())
                        ? YearMonth.now()
                        : YearMonth.parse(month); 
            } catch (DateTimeParseException ex) {
                ym = YearMonth.now();
                ra.addFlashAttribute("errorMessage", "月の形式が正しくありません（例: 2026-01）");
            }

            List<Attendance> attendances = attendanceService.listByEmployeeAndMonth(id, ym);

            int totalMinutes = attendances.stream()
                    .map(Attendance::getWorkedMinutes)
                    .filter(m -> m != null)
                    .mapToInt(Integer::intValue)
                    .sum();

            model.addAttribute("employee", employee);
            model.addAttribute("attendances", attendances);
            model.addAttribute("today", java.time.LocalDate.now());

            model.addAttribute("selectedMonth", ym.toString()); 
            model.addAttribute("prevMonth", ym.minusMonths(1).toString()); 
            model.addAttribute("nextMonth", ym.plusMonths(1).toString());  
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
    @GetMapping("/{id:\\d+}/csv")
    public ResponseEntity<byte[]> exportMonthlyCsv(@PathVariable Long id,
                                               @RequestParam String month) {

        Employee employee = employeeService.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

        YearMonth ym;
        try {
            ym = YearMonth.parse(month); 
        } catch (DateTimeParseException ex) {
            ym = YearMonth.now();
        }

    List<Attendance> attendances = attendanceService.listByEmployeeAndMonth(id, ym);

    StringBuilder sb = new StringBuilder();
    
    sb.append("社員ID,氏名,日付,出勤,退勤,休憩(分),労働時間(分),労働時間(h:mm)\n");

    for (Attendance a : attendances) {
        sb.append(escapeCsv(String.valueOf(employee.getId()))).append(",");
        sb.append(escapeCsv(employee.getName())).append(",");
        sb.append(escapeCsv(String.valueOf(a.getWorkDate()))).append(",");
        sb.append(escapeCsv(a.getCheckIn() == null ? "" : a.getCheckIn().toString())).append(",");
        sb.append(escapeCsv(a.getCheckOut() == null ? "" : a.getCheckOut().toString())).append(",");
        sb.append(a.getBreakMinutes()).append(",");

        Integer workedMin = a.getWorkedMinutes();
        sb.append(workedMin == null ? "" : workedMin).append(",");
        sb.append(escapeCsv(a.getWorkedTime())).append("\n");
    }

    
        byte[] bom = new byte[] {(byte)0xEF, (byte)0xBB, (byte)0xBF};
        byte[] body = sb.toString().getBytes(StandardCharsets.UTF_8);

        byte[] out = new byte[bom.length + body.length];
        System.arraycopy(bom, 0, out, 0, bom.length);
        System.arraycopy(body, 0, out, bom.length, body.length);

        String filename = "kintai_" + employee.getId() + "_" + ym + ".csv";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
            .body(out);
}

    @PostMapping
    public String createEmployee(@Valid @ModelAttribute("employee") EmployeeForm form,
                             BindingResult bindingResult,
                             RedirectAttributes ra) {
    String email = form.getEmail() == null ? "" : form.getEmail().trim().toLowerCase();
    form.setEmail(email);

    if (bindingResult.hasErrors()) {
        return "employee-new";
    }

    
    if (employeeService.existsByEmail(form.getEmail())) {
        bindingResult.rejectValue("email", "duplicate", "このメールアドレスは既に登録されています");
        return "employee-new";
    }

    try {
        Employee saved = employeeService.save(
                Employee.create(form.getName(), form.getEmail(), form.getDepartment())
        );

        ra.addFlashAttribute("successMessage", "社員を作成しました");
        return "redirect:/ui/employees/" + saved.getId();

    } catch (DataIntegrityViolationException | TransactionSystemException | PersistenceException ex) {
        
        bindingResult.rejectValue("email", "duplicate", "このメールアドレスは既に登録されています");
        return "employee-new";
    }
}
    private String escapeCsv(String s) {
    if (s == null) return "";
    boolean needQuote = s.contains(",") || s.contains("\n") || s.contains("\r") || s.contains("\"");
    String escaped = s.replace("\"", "\"\"");
    return needQuote ? "\"" + escaped + "\"" : escaped;
    }
}
