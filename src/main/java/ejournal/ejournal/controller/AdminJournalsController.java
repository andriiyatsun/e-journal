package ejournal.ejournal.controller;

import ejournal.ejournal.model.LessonPlanEntity;
import ejournal.ejournal.repo.AcademicYearRepository;
import ejournal.ejournal.service.CalendarGeneratorService;
import ejournal.ejournal.service.StudentGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/journals")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminJournalsController {

    private final StudentGroupService studentGroupService;
    private final CalendarGeneratorService calendarGeneratorService;

    /**
     * ✅ ОНОВЛЕНИЙ ЕНДПОІНТ: Створює Журнал (StudentGroup) для існуючого Предмета.
     */
    @PostMapping("/add") // ✅ Шлях змінено з "/subjects/add"
    public String addJournal(@RequestParam Long subjectId, // ✅ Змінено: ID предмета
                             @RequestParam Long academicYearId,
                             RedirectAttributes redirectAttributes) {

        try {
            // ✅ Змінено: Викликаємо новий метод сервісу
            studentGroupService.createJournal(subjectId, academicYearId);
            redirectAttributes.addFlashAttribute("journalSuccess",
                    "Журнал для обраного предмету успішно створено.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("journalError", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("journalError",
                    "Виникла неочікувана системна помилка при створенні журналу.");
        }

        return "redirect:/admin/dashboard#journals";
    }

    @PostMapping("/{id}/delete")
    public String deleteJournal(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentGroupService.deleteJournal(id);
            redirectAttributes.addFlashAttribute("journalSuccess", "Журнал успішно видалено.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("journalError", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("journalError", "Не вдалося видалити журнал. Можливо, з ним пов'язані інші дані.");
        }
        return "redirect:/admin/dashboard#journals";
    }

    /**
     * ✅ (Без змін) Обробляє запит на генерацію КТП.
     */
    @PostMapping("/{id}/generate-ktp")
    public String generateKtp(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Тема-заглушка буде оновлена вручну керівником/методистом
            String defaultTopic = "Тема занять (планується)";
            List<LessonPlanEntity> lessons = calendarGeneratorService.generateLessonPlanEntries(id, defaultTopic);

            redirectAttributes.addFlashAttribute("journalSuccess",
                    "Успішно згенеровано " + lessons.size() + " планових занять.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("journalError", "Помилка генерації КТП: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("journalError", "Неочікувана системна помилка при генерації КТП.");
        }
        // Перенаправляємо на сторінку журналу, щоб бачити результат
        return "redirect:/journal/" + id + "#info";
    }
}