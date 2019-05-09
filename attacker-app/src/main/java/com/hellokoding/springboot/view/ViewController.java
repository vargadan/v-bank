package com.hellokoding.springboot.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/csrf")
    public String csrf() {
        return "csrf";
    }

    @GetMapping("/keylogin")
    public String keylogin() {
        return "keylogin";
    }
}