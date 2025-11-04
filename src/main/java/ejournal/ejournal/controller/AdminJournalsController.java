package ejournal.ejournal.controller;

import ejournal.ejournal.service.StudentGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Цей НОВИЙ контролер обробляє дії,
 * пов'язані з "Журналами" (StudentGroup)
 */
@Controller
@RequestMapping("/admin/journals") // ✅ Батьківський шлях
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminJournalsController {

    private final StudentGroupService studentGroupService;

    /**
     * Обробляє запит з адмін-панелі на створення нового Предмету (і Журналу).
     * Повний шлях: POST /admin/journals/subjects/add
     */
    @PostMapping("/subjects/add") // ✅ Шлях методу
    public String addSubjectAndJournal(@RequestParam String subjectName,
                                       @RequestParam Long departmentId) {

        // Викликаємо наш "тригер"
        studentGroupService.createSubjectAndJournal(subjectName, departmentId);

        // Повертаємо адміна на вкладку "Журнали"
        return "redirect:/admin/dashboard#journals";
    }

    // TODO: В майбутньому тут буде логіка для
    // призначення викладачів до журналу
}

