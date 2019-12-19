package com.netdetpla.ndp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticController {
    @GetMapping("/")
    public String getTaskPage() {
        return "/pages/image.html";
    }
}
