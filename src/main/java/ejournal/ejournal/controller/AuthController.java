package ejournal.ejournal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/")
    public String index() { return "login"; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/head/home")
    public String headHome() { return "head-home"; }

    @GetMapping("/teacher/home")
    public String teacherHome() { return "teacher-home"; }
}
