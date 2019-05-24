package com.vargadan.attacker;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AttackViewController {

    @GetMapping("/xxe")
    public String xxe() {
        return "xxe";
    }

    @GetMapping("/csrf")
    public String csrf() {
        return "csrf";
    }

    @GetMapping("/keylogin")
    public String keylogin() {
        return "keylogin";
    }
}