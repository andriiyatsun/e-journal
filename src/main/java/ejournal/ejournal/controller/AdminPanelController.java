package ejournal.ejournal.controller;

import ejournal.ejournal.repo.AcademicYearRepository;
import ejournal.ejournal.repo.DepartmentRepository;
import ejournal.ejournal.repo.HolidayRepository;
import ejournal.ejournal.repo.StudentGroupRepository;
import ejournal.ejournal.repo.UserRepository;
import ejournal.ejournal.repo.VacationPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// ✅ ВАЖЛИВО: Переконайся, що імпорт саме цей:
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPanelController {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentGroupRepository studentGroupRepository;

    // Репозиторії для вкладки "Календар"
    private final AcademicYearRepository academicYearRepository;
    private final VacationPeriodRepository vacationPeriodRepository;
    private final HolidayRepository holidayRepository;

    /** Рендер твоєї головної admin-dashboard.html */

    @Transactional(readOnly = true) // ✅ Ця анотація триматиме сесію БД відкритою
    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "tab", required = false) String tab,
                            Model model) {
        // === Вкладки Користувачів ===
        model.addAttribute("teachers", userRepository.findAllByRole_Name("ROLE_TEACHER"));
        model.addAttribute("heads",    userRepository.findAllByRole_Name("ROLE_HEAD"));
        model.addAttribute("admins",   userRepository.findAllByRole_Name("ROLE_ADMIN"));

        // === Вкладка Журналів ===
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("journals", studentGroupRepository.findAll());

        // === Вкладка Календаря ===
        model.addAttribute("academicYears", academicYearRepository.findAll());
        model.addAttribute("vacations", vacationPeriodRepository.findAll());
        model.addAttribute("holidays", holidayRepository.findAll());

        // Активна вкладка
        model.addAttribute("activeTab", tab != null ? tab : "teachers");
        return "admin-dashboard";
    }
}