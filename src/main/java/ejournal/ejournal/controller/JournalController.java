package ejournal.ejournal.controller;

import ejournal.ejournal.repo.StudentGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/journal") // Всі запити до /journal будуть оброблятися тут
@RequiredArgsConstructor
public class JournalController {

    private final StudentGroupRepository studentGroupRepository;

    /**
     * Обробляє запити GET /journal/{id}
     * (наприклад, /journal/1, /journal/2 тощо)
     *
     * @param id    ID журналу (StudentGroup) з URL
     * @param model Модель Spring для передачі даних у HTML
     * @return назва HTML-шаблону ("journal")
     */
    @GetMapping("/{id}")
    public String getJournalPage(@PathVariable Long id, Model model) {

        // 1. Знаходимо журнал (StudentGroup) в базі за ID
        var journal = studentGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Журнал не знайдено: " + id));

        // 2. Кладемо знайдений журнал у модель
        // (Тепер HTML-шаблон зможе отримати до нього доступ через `${journal}`)
        model.addAttribute("journal", journal);

        // 3. Повертаємо назву нашого HTML-файлу (journal.html)
        return "journal";
    }
}
