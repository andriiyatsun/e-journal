package ejournal.ejournal.controller;

import ejournal.ejournal.model.LessonPlanEntity; // ✅ Імпортуємо LessonPlanEntity
import ejournal.ejournal.repo.LessonPlanRepository;
import ejournal.ejournal.repo.StudentGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Month; // ✅ Імпортуємо Month
import java.util.List;
import java.util.stream.Collectors; // ✅ Імпортуємо Collectors

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
    @Transactional(readOnly = true)
    public String getJournalPage(@PathVariable Long id, Model model) {

        // 1. Знаходимо журнал (StudentGroup) в базі за ID
        var journal = studentGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Журнал не знайдено: " + id));

        // 2. Завантажуємо ВСЕ КТП
        List<LessonPlanEntity> allLessonPlans = lessonPlanRepository.findAllByStudentGroupIdOrderByLessonNumberAsc(id);

        // 3. ✅ Фільтруємо на КП-1 (Вересень - Грудень) та КП-2 (Січень - Травень/Червень)
        List<LessonPlanEntity> kp1Plans = allLessonPlans.stream()
                .filter(lp -> {
                    Month actualMonth = lp.getActualDate() != null ? lp.getActualDate().getMonth() : null;
                    return actualMonth != null && (actualMonth.getValue() >= 9 || actualMonth.getValue() <= 12);
                })
                .collect(Collectors.toList());

        List<LessonPlanEntity> kp2Plans = allLessonPlans.stream()
                .filter(lp -> {
                    Month actualMonth = lp.getActualDate() != null ? lp.getActualDate().getMonth() : null;
                    return actualMonth != null && (actualMonth.getValue() >= 1 && actualMonth.getValue() <= 8); // Січень-Серпень
                })
                .collect(Collectors.toList());

        // 4. Кладемо дані у модель
        model.addAttribute("journal", journal);
        model.addAttribute("kp1Plans", kp1Plans); // ✅ КП-1
        model.addAttribute("kp2Plans", kp2Plans); // ✅ КП-2

        // 5. Повертаємо назву нашого HTML-файлу
        return "journal";
    }
}