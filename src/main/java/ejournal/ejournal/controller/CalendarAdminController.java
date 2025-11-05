package ejournal.ejournal.controller;

import ejournal.ejournal.service.CalendarAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/admin/calendar")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CalendarAdminController {

    private final CalendarAdminService calendarService;

    // --- Academic Years ---

    @PostMapping("/years/add")
    public String addYear(@RequestParam String name,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        calendarService.createAcademicYear(name, startDate, endDate);
        return "redirect:/admin/dashboard#calendar";
    }

    // ❌ Ендпоінт 'setActiveYear' видалено

    @PostMapping("/years/{id}/delete")
    public String deleteYear(@PathVariable Long id) {
        calendarService.deleteAcademicYear(id);
        return "redirect:/admin/dashboard#calendar";
    }

    // ... (решта методів (Holidays, Vacations, Settings) залишаються без змін) ...
    // --- Holidays ---
    @PostMapping("/holidays/add") // ✅ Покращено: прибираємо yearId з URL
    public String addHoliday(@RequestParam Long yearId, // ✅ Додаємо yearId як параметр
                             @RequestParam String name,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        calendarService.addHoliday(yearId, name, date);
        return "redirect:/admin/dashboard#calendar";
    }
    // ... (решта коду) ...
    @PostMapping("/holidays/{id}/delete")
    public String deleteHoliday(@PathVariable Long id) {
        calendarService.deleteHoliday(id);
        return "redirect:/admin/dashboard#calendar";
    }

    // --- Vacations ---
    @PostMapping("/vacations/add") // ✅ Покращено: прибираємо yearId з URL
    public String addVacation(@RequestParam Long yearId, // ✅ Додаємо yearId як параметр
                              @RequestParam String name,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        calendarService.addVacation(yearId, name, startDate, endDate);
        return "redirect:/admin/dashboard#calendar";
    }

    @PostMapping("/vacations/{id}/delete")
    public String deleteVacation(@PathVariable Long id) {
        calendarService.deleteVacation(id);
        return "redirect:/admin/dashboard#calendar";
    }

    // --- Global Settings ---
    @PostMapping("/settings/save")
    public String saveSettings(@RequestParam Map<String, String> settings) {
        calendarService.saveGlobalSettings(settings);
        return "redirect:/admin/dashboard#calendar";
    }
}