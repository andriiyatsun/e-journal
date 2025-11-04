package ejournal.ejournal.controller;

import ejournal.ejournal.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/heads")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class HeadAdminController {

    private final AdminUserService adminUserService;

    @PostMapping("/add")
    public String add(@RequestParam String name,
                      @RequestParam String surname,
                      @RequestParam String email,
                      @RequestParam String password,
                      // ✅ ЗМІНА: Поле стало необов'язковим
                      @RequestParam(required = false) Long departmentId) {
        adminUserService.createHead(name, surname, email, password, departmentId);
        return "redirect:/admin/dashboard#heads";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @RequestParam String name,
                       @RequestParam String surname,
                       @RequestParam String email,
                       @RequestParam(required = false) String password,
                       // ✅ ЗМІНА: Поле стало необов'язковим
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
