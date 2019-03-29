package com.hellokoding.springboot.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AttackController {
    @GetMapping({"/", "/csrf"})
    public String csrf(Model model, @RequestParam(value="name", required=false, defaultValue="Bob") String name) {
        model.addAttribute("name", name);
        return "csrf";
    }

    @GetMapping({"/hello"})
    public String hello(Model model, Integer userId) {
        String username = PersistenceService.getUserNameFromId(userId);
        model.addAttribute("username", username);
        return "hello";
    }
}

class PersistenceService {
    static public String getUserNameFromId(Integer userId) {
        return "<script>alert(1)</script>";
    }
}
