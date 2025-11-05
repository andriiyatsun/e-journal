package ejournal.ejournal.controller;

import ejournal.ejournal.repo.AcademicYearRepository;
import ejournal.ejournal.service.StudentGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable; // ✅ Імпортуємо
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/journals")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminJournalsController {

    private final StudentGroupService studentGroupService;
    // ❌ academicYearRepo більше не потрібен для перевірки
    // private final AcademicYearRepository academicYearRepo;

    @PostMapping("/subjects/add")
    public String addSubjectAndJournal(@RequestParam String subjectName,
                                       @RequestParam Long departmentId,
                                       @RequestParam Long academicYearId, // ✅ Додаємо ID року
                                       RedirectAttributes redirectAttributes) {

        // ❌ Видаляємо стару перевірку на "активний" рік

        try {
            // ✅ Передаємо ID року в сервіс
            studentGroupService.createSubjectAndJournal(subjectName, departmentId, academicYearId);
            redirectAttributes.addFlashAttribute("journalSuccess",
                    "Журнал для предмету '" + subjectName + "' успішно створено.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("journalError", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("journalError",
                    "Виникла неочікувана системна помилка. Зверніться до адміністратора.");
        }

        return "redirect:/admin/dashboard#journals";
    }

    /**
     * ✅ НОВИЙ МЕТОД:
     * Обробляє запит на видалення журналу (StudentGroup).
     */
    @PostMapping("/{id}/delete")
    public String deleteJournal(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentGroupService.deleteJournal(id);
            redirectAttributes.addFlashAttribute("journalSuccess", "Журнал успішно видалено.");
        } catch (IllegalArgumentException e) {
            // Напр., "Журнал не знайдено"
            redirectAttributes.addFlashAttribute("journalError", e.getMessage());
        } catch (Exception e) {
            // На випадок, якщо щось піде не так (напр., обмеження бази даних)
            redirectAttributes.addFlashAttribute("journalError", "Не вдалося видалити журнал. Можливо, з ним пов'язані інші дані.");
        }
        return "redirect:/admin/dashboard#journals";
    }
}