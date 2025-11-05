package ejournal.ejournal.controller;

import ejournal.ejournal.repo.LessonPlanRepository;
import ejournal.ejournal.repo.StudentGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional; // ✅ ІМПОРТ

@Controller
@RequestMapping("/journal")
@RequiredArgsConstructor
public class JournalController {

    private final StudentGroupRepository studentGroupRepository;
    private final LessonPlanRepository lessonPlanRepository;

    /**
     * Обробляє запити GET /journal/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("@journalSecurityService.canViewJournal(authentication, #id) or hasRole('ADMIN') or hasRole('HEAD')")
    @Transactional(readOnly = true) // ✅ ВИПРАВЛЕННЯ
    public String getJournalPage(@PathVariable Long id, Model model) {

        // 1. Знаходимо журнал (StudentGroup) в базі за ID
        var journal = studentGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Журнал не знайдено: " + id));

        // 2. Завантажуємо КТП для цього журналу
        var lessonPlans = lessonPlanRepository.findAllByStudentGroupIdOrderByLessonNumberAsc(id);

        // 3. Кладемо дані у модель
        model.addAttribute("journal", journal);
        model.addAttribute("lessonPlans", lessonPlans);

        // 4. Повертаємо назву нашого HTML-файлу
        return "journal";
    }
}