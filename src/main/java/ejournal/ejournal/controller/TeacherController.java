package ejournal.ejournal.controller;

import ejournal.ejournal.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')") // Лише для викладачів
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping("/home")
    public String getTeacherHomePage(Authentication authentication, Model model) {
        // Отримуємо email поточного залогіненого викладача
        String userEmail = authentication.getName();

        // Знаходимо всі журнали, прив'язані до цього викладача
        var journals = teacherService.getJournalsForTeacher(userEmail);

        model.addAttribute("journals", journals);
        model.addAttribute("teacherName", userEmail); // Можемо додати ПІБ, якщо треба

        // Повертаємо нову HTML сторінку
        return "teacher-dashboard";
    }
}

