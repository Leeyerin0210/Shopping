package com.shopping.site.controller

import com.shopping.site.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class LoginController(
    private val userRepository: UserRepository,
) {

    @GetMapping("/login")
    fun showLoginPage(
        @RequestParam(required = false) error: Boolean? = false,
        request: HttpServletRequest,
        model: Model
    ): String {
        if (error == true) {
            model.addAttribute("errorMessage", "Invalid email or password.")
        }
        model.addAttribute("title", "Login or Sign Up")


        return "login" // 로그인 페이지 템플릿
    }

    @PostMapping("/login")
    fun loginUser(
        @RequestParam email: String,
        @RequestParam password: String,
        request: HttpServletRequest,
        model: Model
    ): String {
        val user = userRepository.findByEmail(email)
            ?: run {
                model.addAttribute("errorMessage", "User not found.")
                return "login"
            }

        return if (password == user.password) { // 비밀번호 검증
            val session: HttpSession = request.session
            session.setAttribute("userEmail", email) // 세션에 사용자 이메일 저장

            // 세션에 저장된 이전 URL로 리다이렉트
            val redirectUrl = session.getAttribute("redirectAfterLogin") as? String ?: "/main"
            session.removeAttribute("redirectAfterLogin") // 세션에서 제거
            "redirect:$redirectUrl"
        } else {
            model.addAttribute("errorMessage", "Invalid password.")
            "login"
        }
    }

    @GetMapping("/logout")
    fun logoutUser(request: HttpServletRequest): String {
        request.session.invalidate() // 세션 무효화
        return "redirect:/main"
    }

    @GetMapping("/mypage")
    fun showMyPage(request: HttpServletRequest, model: Model): String {
        val session = request.session
        val userEmail = session.getAttribute("userEmail") as? String
            ?: return "redirect:/login" // 로그인되지 않은 경우 로그인 페이지로 리다이렉트

        model.addAttribute("userEmail", userEmail)
        return "mypage" // 마이페이지 템플릿
    }
}

