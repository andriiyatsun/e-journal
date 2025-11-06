package ejournal.ejournal.controller;

import ejournal.ejournal.model.LessonPlanEntity;
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

import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

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

        // 3. ✅ ВИПРАВЛЕНА ЛОГІКА: Фільтруємо на КП-1 (Вересень - Грудень) та КП-2 (Січень - Червень)
        // Запускаємо фільтрацію тільки якщо є дати, щоб уникнути NullPointerException

        List<LessonPlanEntity> kp1Plans = allLessonPlans.stream()
                .filter(lp -> lp.getActualDate() != null)
                .filter(lp -> {
                    int monthValue = lp.getActualDate().getMonthValue();
                    // КП-1: Вересень (9), Жовтень (10), Листопад (11), Грудень (12)
                    return monthValue >= 9 && monthValue <= 12;
                })
                .collect(Collectors.toList());

        List<LessonPlanEntity> kp2Plans = allLessonPlans.stream()
                .filter(lp -> lp.getActualDate() != null)
                .filter(lp -> {
                    int monthValue = lp.getActualDate().getMonthValue();
                    // КП-2: Січень (1), Лютий (2), Березень (3), Квітень (4), Травень (5), Червень (6), Липень (7), Серпень (8)
                    return monthValue >= 1 && monthValue <= 8;
                })
                .collect(Collectors.toList());

        // 4. Кладемо дані у модель
        model.addAttribute("journal", journal);
        model.addAttribute("kp1Plans", kp1Plans);
        model.addAttribute("kp2Plans", kp2Plans);

        // 5. Повертаємо назву нашого HTML-файлу
        return "journal";
    }
}