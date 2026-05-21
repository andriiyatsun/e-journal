package ejournal.ejournal.controller;

import ejournal.ejournal.model.UserEntity;
import ejournal.ejournal.repo.DepartmentRepository;
import ejournal.ejournal.repo.UserRepository;
import ejournal.ejournal.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/heads")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class HeadAdminController {

    private final AdminUserService adminUserService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    // --- НОВИЙ МЕТОД: Показує сторінку (GET запит) ---
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        UserEntity head = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Голову відділу не знайдено: " + id));

        model.addAttribute("user", head);
        model.addAttribute("role", "ROLE_HEAD"); // Кажемо шаблону, що це Голова
        model.addAttribute("departments", departmentRepository.findAll()); // Передаємо відділи
        return "admin-user-edit";
    }

    @PostMapping("/add")
    public String add(@RequestParam String name,
                      @RequestParam String surname,
                      @RequestParam String email,
                      @RequestParam String password,
                      @RequestParam(required = false) Long departmentId) {
        adminUserService.createHead(name, surname, email, password, departmentId);
        return "redirect:/admin/dashboard#heads";
    }

    // --- ЗБЕРЕЖЕННЯ ДАНИХ (POST запит) ---
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @RequestParam String name,
                       @RequestParam String surname,
                       @RequestParam String email,
                       @RequestParam(required = false) String password,
                       @RequestParam(required = false) Long departmentId) {
        adminUserService.updateHead(id, name, surname, email, password, departmentId);
        return "redirect:/admin/dashboard#heads";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return "redirect:/admin/dashboard#heads";
    }
}