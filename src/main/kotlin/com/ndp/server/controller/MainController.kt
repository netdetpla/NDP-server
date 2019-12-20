package com.ndp.server.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping


@Controller
class StaticController {
    @get:GetMapping("/")
    val taskPage: String
        get() = "/pages/image.html"
}
