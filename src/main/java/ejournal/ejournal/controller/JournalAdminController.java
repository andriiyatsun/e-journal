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
import java.util.List;

@Controller
@RequestMapping("/journal")
@RequiredArgsConstructor
public class JournalAdminController {

    private final StudentGroupAdminService groupAdminService;

    /**
     * ✅ ЦЕЙ МЕТОД ОБРОБЛЯЄ ЗБЕРЕЖЕННЯ "ОСНОВНИХ ВІДОМОСТЕЙ"
     * (Він був відсутній у твоєму коді з чату)
     */
    @PostMapping("/{id}/update-details")
    @PreAuthorize("hasRole('ADMIN') or @journalSecurityService.canViewJournal(authentication, #id)")
    public String updateJournalDetails(@PathVariable Long id,
                                       @RequestParam(required = false) String programName,
                                       @RequestParam(required = false) String programApprovalDate,
                                       @RequestParam(required = false) String studyLevel,
                                       @RequestParam(required = false) String studyYear,
                                       @RequestParam(required = false) Integer hoursPerWeek,
                                       @RequestParam(required = false) String scheduleJson,
                                       @RequestParam(required = false) String groupNumber,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                       RedirectAttributes redirectAttributes) {
        try {
            groupAdminService.updateGroupDetails(id,
                    programName, programApprovalDate, studyLevel, studyYear, hoursPerWeek, scheduleJson, groupNumber, startDate, endDate);

            redirectAttributes.addFlashAttribute("journalSuccess", "Основні відомості успішно оновлено.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("journalError", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("journalError", "Помилка при оновленні: " + e.getMessage());
        }

        return "redirect:/journal/" + id + "#info";
    }

    /**
     * ✅ ЦЕЙ МЕТОД ОБРОБЛЯЄ ЗБЕРЕЖЕННЯ ТЕМ "КП-1" / "КП-2"
     * (Він був у твоєму коді з чату)
     */
    @PostMapping("/{id}/update-ktp-topics")
    @PreAuthorize("hasRole('ADMIN') or @journalSecurityService.canViewJournal(authentication, #id)")
    public String updateKtpTopics(@PathVariable Long id,
                                  @RequestParam(name = "lessonId") List<Long> lessonIds,
                                  @RequestParam(name = "topic") List<String> topics,
                                  @RequestParam(name = "note") List<String> notes,
                                  @RequestParam(name = "correctedDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<LocalDate> correctedDates,
                                  @RequestParam(name = "fragment") String fragment,
                                  RedirectAttributes redirectAttributes) {

        try {
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