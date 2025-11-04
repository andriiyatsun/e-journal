package ejournal.ejournal.controller;

import ejournal.ejournal.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/teachers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TeacherAdminController {

    private final AdminUserService adminUserService;

    @PostMapping("/add")
    public String add(@RequestParam String name,
                      @RequestParam String surname,
                      @RequestParam String email,
                      @RequestParam String password,
                      // ✅ ЗМІНА: Поля стали необов'язковими
                      @RequestParam(required = false) Long departmentId,
                      @RequestParam(required = false) Long subjectId) {
        adminUserService.createTeacher(name, surname, email, password, departmentId, subjectId);
        return "redirect:/admin/dashboard#teachers";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @RequestParam String name,
                       @RequestParam String surname,
                       @RequestParam String email,
                       @RequestParam(required = false) String password, // може бути порожній
                       // ✅ ЗМІНА: Поля стали необов'язковими
                       @RequestParam(required = false) Long departmentId,
                       @RequestParam(required = false) Long subjectId) {
        adminUserService.updateTeacher(id, name, surname, email, password, departmentId, subjectId);
        return "redirect:/admin/dashboard#teachers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return "redirect:/admin/dashboard#teachers";
    }
}
