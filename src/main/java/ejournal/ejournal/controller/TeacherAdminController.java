package ejournal.ejournal.controller;

import ejournal.ejournal.model.StudentGroupEntity;
import ejournal.ejournal.model.UserEntity;
import ejournal.ejournal.repo.StudentGroupRepository;
import ejournal.ejournal.repo.UserRepository;
import ejournal.ejournal.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ✅ Імпортуємо Model
import org.springframework.web.bind.annotation.*;

import java.util.List; // ✅ Імпортуємо List
import java.util.Set;   // ✅ Імпортуємо Set

@Controller
@RequestMapping("/admin/teachers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TeacherAdminController {

    private final AdminUserService adminUserService;
    // ✅ Додаємо репозиторії для завантаження даних
    private final UserRepository userRepository;
    private final StudentGroupRepository studentGroupRepository;

    /**
     * ✅ НОВИЙ МЕТОД: Показує сторінку редагування для конкретного викладача.
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        // 1. Знаходимо викладача
        UserEntity teacher = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Викладача не знайдено: " + id));

        // 2. Знаходимо ВСІ журнали, щоб показати у списку
        List<StudentGroupEntity> allJournals = studentGroupRepository.findAll();

        // 3. Передаємо дані у HTML
        model.addAttribute("teacher", teacher);
        model.addAttribute("allJournals", allJournals);

        // 4. Повертаємо назву шаблону
        return "admin-edit-teacher";
    }

    @PostMapping("/add")
    public String add(@RequestParam String name,
                      @RequestParam String surname,
                      @RequestParam String email,
                      @RequestParam String password,
                      @RequestParam(required = false) Long departmentId, // Це поле застаріле
                      @RequestParam(required = false) Long subjectId) {  // Це поле застаріле
        // Примітка: метод createTeacher все ще використовує застарілі departmentId/subjectId
        // Його теж варто оновити, щоб він приймав 'journalIds'
        adminUserService.createTeacher(name, surname, email, password, departmentId, subjectId);
        return "redirect:/admin/dashboard#teachers";
    }

    /**
     * ✅ ОНОВЛЕНИЙ МЕТОД: Зберігає зміни для викладача.
     */
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @RequestParam String name,
                       @RequestParam String surname,
                       @RequestParam String email,
                       @RequestParam(required = false) String password, // може бути порожній
                       // ✅ ЗМІНА: Видаляємо застарілі 'departmentId' та 'subjectId'
                       // і додаємо 'journalIds' зі сторінки admin-edit-teacher.html
                       @RequestParam(required = false) Set<Long> journalIds) {

        // ✅ ЗМІНА: Викликаємо оновлений метод сервісу
        adminUserService.updateTeacher(id, name, surname, email, password, journalIds);
        return "redirect:/admin/dashboard#teachers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return "redirect:/admin/dashboard#teachers";
    }
}