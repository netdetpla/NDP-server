package com.ndp.server.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping


@Controller
class StaticController {
    @GetMapping("/")
    fun getDefaultPage(): String {
        return "/pages/image.html"
    }
}
