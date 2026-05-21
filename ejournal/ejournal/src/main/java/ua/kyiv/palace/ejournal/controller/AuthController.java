package ua.kyiv.palace.ejournal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "Невірне ім'я користувача або пароль!");
        }

        if (logout != null) {
            model.addAttribute("message", "Ви успішно вийшли з системи!");
        }

        return "loginPage"; // назва HTML файлу без розширення
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // створіть цю сторінку пізніше
    }
}