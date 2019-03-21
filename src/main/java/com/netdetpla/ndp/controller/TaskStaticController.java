package com.netdetpla.ndp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TaskStaticController {

    @GetMapping("/task")
    public String getTaskPage() {
        return "/task.html";
    }
}
