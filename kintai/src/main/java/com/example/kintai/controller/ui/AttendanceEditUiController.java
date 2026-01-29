package com.example.kintai.controller.ui;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kintai.dto.AttendanceEditForm;
import com.example.kintai.model.Attendance;
import com.example.kintai.service.AttendanceService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/attendance")
public class AttendanceEditUiController {

    private final AttendanceService attendanceService;

    public AttendanceEditUiController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
    @GetMapping("/{attendanceId:\\d+}/edit")
    public String editForm(@PathVariable Long attendanceId,
                           @RequestParam(required = false) String month,
                           Model model, RedirectAttributes ra) {
        try {
            Attendance a = attendanceService.findById(attendanceId);
            Long employeeId = a.getEmployee().getId();
            AttendanceEditForm form = new AttendanceEditForm();
            form.setCheckIn(a.getCheckIn() == null ? "" : a.getCheckIn().format(fmt));
            form.setCheckOut(a.getCheckOut() == null ? "" : a.getCheckOut().format(fmt));
            form.setBreakMinutes(a.getBreakMinutes());
            model.addAttribute("employeeId", employeeId);
            model.addAttribute("attendance", a);
            model.addAttribute("form", form);
            model.addAttribute("month", month);

            return "attendance-edit-layout";
        } catch (ResponseStatusException ex) {
        ra.addFlashAttribute("errorMessage", ex.getReason() != null ? ex.getReason() : "勤怠が見つかりません");
        return "redirect:/ui/employees";
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", "エラーが発生しました");
            return "redirect:/ui/employees";
        }
    }

    @PostMapping("/{attendanceId:\\d+}/edit")
    public String saveEdit(@PathVariable Long attendanceId,
                           @RequestParam(required = false) String month,
                           @Valid @ModelAttribute("form") AttendanceEditForm form,
                           BindingResult br,
                           RedirectAttributes ra) {

        Attendance a = attendanceService.findById(attendanceId);

        if (br.hasErrors()) {
            ra.addFlashAttribute("errorMessage", "入力内容を確認してください");
            return redirectToEmployee(a.getEmployee().getId(), month);
        }

        try {
            LocalTime in = LocalTime.parse(form.getCheckIn());
            LocalTime out = LocalTime.parse(form.getCheckOut());

            attendanceService.updateAttendance(attendanceId, in, out, form.getBreakMinutes());
            ra.addFlashAttribute("successMessage", "勤怠を修正しました");

        } catch (DateTimeParseException ex) {
            ra.addFlashAttribute("errorMessage", "時間の形式が正しくありません（例: 09:00）");
        } catch (ResponseStatusException ex) {
            ra.addFlashAttribute("errorMessage", ex.getReason());
        }

        return redirectToEmployee(a.getEmployee().getId(), month);
    }

    private String redirectToEmployee(Long employeeId, String month) {
        if (month != null && !month.isBlank()) {
            return "redirect:/ui/employees/" + employeeId + "?month=" + month;
        }
        return "redirect:/ui/employees/" + employeeId;
    }
}
