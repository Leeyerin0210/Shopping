package com.shopping.site.Controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserStatusController {

    @GetMapping("/api/check-login-status")
    fun checkLoginStatus(request: HttpServletRequest): Map<String, Boolean> {
        val session = request.session
        val userEmail = session.getAttribute("userEmail") as? String
        return mapOf("loggedIn" to (userEmail != null))
    }
}
