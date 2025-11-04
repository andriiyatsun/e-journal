package ejournal.ejournal.controller;

import ejournal.ejournal.model.UserEntity;
import ejournal.ejournal.repo.UserRepository;
import ejournal.ejournal.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/admins")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminsController {

    private final AdminUserService adminUserService;

    @PostMapping("/add")
    public String add(@RequestParam String name,
                      @RequestParam String surname,
                      @RequestParam String email,
                      @RequestParam String password) {
        adminUserService.createAdmin(name, surname, email, password);
        return "redirect:/admin/dashboard#admins";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @RequestParam String name,
                       @RequestParam String surname,
                       @RequestParam String email,
                       @RequestParam(required = false) String password) {
        adminUserService.updateAdmin(id, name, surname, email, password);
        return "redirect:/admin/dashboard#admins";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return "redirect:/admin/dashboard#admins";
    }
}
