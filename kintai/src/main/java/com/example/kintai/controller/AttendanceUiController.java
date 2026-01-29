package com.example.kintai.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kintai.service.AttendanceService;

@Controller
@RequestMapping("/ui/attendance")
public class AttendanceUiController {

    private final AttendanceService attendanceService;

    public AttendanceUiController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/check-in")
    public String checkIn(@RequestParam Long employeeId,
                          @RequestParam String workDate,
                          @RequestParam String time,
                          @RequestParam(required = false) String month,
                          RedirectAttributes ra) {
        try {
            LocalDate date = LocalDate.parse(workDate);   // expects yyyy-MM-dd
            LocalTime t = LocalTime.parse(time);          // expects HH:mm
            attendanceService.checkIn(employeeId, date, t);
            ra.addFlashAttribute("successMessage", "出勤登録しました");
        } catch (DateTimeParseException ex) {
            ra.addFlashAttribute("errorMessage", "日付/時間の形式が正しくありません");
        } catch (ResponseStatusException ex) {
            ra.addFlashAttribute("errorMessage", ex.getReason());
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", "エラーが発生しました");
        }
        return "redirect:/ui/employees/" + employeeId;
    }

    @PostMapping("/check-out")
    public String checkOut(@RequestParam Long employeeId,
                           @RequestParam String workDate,
                           @RequestParam String time,
                           @RequestParam(required = false) String month,
                           RedirectAttributes ra) {
        try {
            LocalDate date = LocalDate.parse(workDate);
            LocalTime t = LocalTime.parse(time);
            attendanceService.checkOut(employeeId, date, t);
            ra.addFlashAttribute("successMessage", "退勤登録しました");
        } catch (DateTimeParseException ex) {
            ra.addFlashAttribute("errorMessage", "日付/時間の形式が正しくありません");
        } catch (ResponseStatusException ex) {
            ra.addFlashAttribute("errorMessage", ex.getReason());
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", "エラーが発生しました");
        }
        return "redirect:/ui/employees/" + employeeId;
    }

    @PostMapping("/break")
    public String updateBreak(@RequestParam Long employeeId,
                              @RequestParam String workDate,
                              @RequestParam String breakMinutes,
                              @RequestParam(required = false) String month,
                              RedirectAttributes ra) {
        try {
            LocalDate date = LocalDate.parse(workDate);
            int minutes = Integer.parseInt(breakMinutes);
            if (minutes < 0) {
                ra.addFlashAttribute("errorMessage", "休憩(分)は0以上で入力してください");
                return "redirect:/ui/employees/" + employeeId;
            }

            attendanceService.updateBreak(employeeId, date, minutes);
            ra.addFlashAttribute("successMessage", "休憩時間を更新しました");
        } catch (DateTimeParseException ex) {
            ra.addFlashAttribute("errorMessage", "日付の形式が正しくありません");
        } catch (NumberFormatException ex) {
            ra.addFlashAttribute("errorMessage", "休憩(分)は数字で入力してください");
        } catch (ResponseStatusException ex) {
            ra.addFlashAttribute("errorMessage", ex.getReason());
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", "エラーが発生しました");
        }
        if (month != null && !month.isBlank()) {
            return "redirect:/ui/employees/" + employeeId + "?month=" + month;
        }
        return "redirect:/ui/employees/" + employeeId;
    }
}
