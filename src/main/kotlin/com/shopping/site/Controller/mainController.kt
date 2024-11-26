package com.shopping.site.Controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MainController {

    @GetMapping("/main")
    fun index(request: HttpServletRequest, model: Model): String {
        val session = request.session
        val userEmail = session.getAttribute("userEmail") as? String

        if (userEmail != null) {
            model.addAttribute("isLoggedIn", true)
            model.addAttribute("userEmail", userEmail)
        } else {
            model.addAttribute("isLoggedIn", false)
        }

        return "index"
    }
}
