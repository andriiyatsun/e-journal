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

@Controller
@RequestMapping("/journal")
@RequiredArgsConstructor
public class JournalAdminController {

    private final StudentGroupAdminService groupAdminService;

    /**
     * Обробляє збереження форми "Основні відомості" (вкладка INFO)
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
}