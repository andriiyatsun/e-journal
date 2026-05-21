package ejournal.ejournal.controller;

import ejournal.ejournal.model.UserEntity;
import ejournal.ejournal.repo.StudentGroupRepository;
import ejournal.ejournal.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HeadController {

    private final UserRepository userRepository;
    private final StudentGroupRepository studentGroupRepository;

    @GetMapping("/head/home")
    @PreAuthorize("hasRole('HEAD')")
    public String getHeadDashboard(Model model, Authentication auth) {
        // Отримуємо поточного голову за email (username)
        UserEntity headUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено"));

        var department = headUser.getDepartment();

        if (department != null) {
            model.addAttribute("departmentName", department.getName());
            model.addAttribute("journals", studentGroupRepository.findAllBySubjectDepartmentId(department.getId()));
        } else {
            model.addAttribute("departmentName", "Не призначено");
            model.addAttribute("journals", java.util.List.of());
        }

        return "head-home";
    }
}
