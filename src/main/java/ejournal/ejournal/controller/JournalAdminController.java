package ejournal.ejournal.controller;

import ejournal.ejournal.service.StudentGroupAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List; // ✅ Імпортуємо List

@Controller
@RequestMapping("/journal")
@RequiredArgsConstructor
public class JournalAdminController {

    private final StudentGroupAdminService groupAdminService;

    // ... (існуючий метод updateJournalDetails) ...

    /**
     * ✅ НОВИЙ МЕТОД: Обробляє збереження форм КП-1/КП-2 (редагування тем/приміток)
     */
    @PostMapping("/{id}/update-ktp-topics")
    @PreAuthorize("hasRole('ADMIN') or @journalSecurityService.canViewJournal(authentication, #id)")
    public String updateKtpTopics(@PathVariable Long id,
                                  @RequestParam(name = "lessonId") List<Long> lessonIds,
                                  @RequestParam(name = "topic") List<String> topics,
                                  @RequestParam(name = "note") List<String> notes,
                                  @RequestParam(name = "correctedDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<LocalDate> correctedDates, // ✅ ДОДАНО
                                  @RequestParam(name = "fragment") String fragment,
                                  RedirectAttributes redirectAttributes) {

        try {
            // ✅ ОНОВЛЕНИЙ ВИКЛИК СЕРВІСУ
            groupAdminService.updateLessonPlanTopics(id, lessonIds, topics, notes, correctedDates);

            redirectAttributes.addFlashAttribute("journalSuccess", "Теми, дати та примітки успішно оновлено.");

        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            redirectAttributes.addFlashAttribute("journalError", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("journalError", "Неочікувана системна помилка при оновленні КТП.");
        }

        return "redirect:/journal/" + id + "#" + fragment;
    }
}