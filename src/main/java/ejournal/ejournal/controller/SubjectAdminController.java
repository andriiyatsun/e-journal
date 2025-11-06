package ejournal.ejournal.controller;

import ejournal.ejournal.service.StudentGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable; // ✅ Імпорт
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/subjects")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SubjectAdminController {

    private final StudentGroupService studentGroupService;

    /**
     * Створює лише Предмет (шаблон).
     */
    @PostMapping("/add")
    public String addSubject(@RequestParam String subjectName,
                             @RequestParam Long departmentId,
                             RedirectAttributes redirectAttributes) {

        try {
            studentGroupService.createSubject(subjectName, departmentId);
            redirectAttributes.addFlashAttribute("journalSuccess",
                    "Предмет '" + subjectName + "' успішно створено.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("journalError", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("journalError",
                    "Виникла неочікувана системна помилка при створенні предмету.");
        }

        return "redirect:/admin/dashboard#journals";
    }

    /**
     * ✅ НОВИЙ ЕНДПОІНТ: Видаляє Предмет та всі пов'язані журнали.
     */
    @PostMapping("/{id}/delete")
    public String deleteSubject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentGroupService.deleteSubject(id);
            redirectAttributes.addFlashAttribute("journalSuccess", "Предмет та всі пов'язані з ним журнали успішно видалено.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("journalError", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("journalError", "Не вдалося видалити предмет. " + e.getMessage());
        }
        return "redirect:/admin/dashboard#journals";
    }
}