package com.shopping.site

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class LoginController(
    private val userRepository: UserRepository,
    private val userService: UserService
) {

    @GetMapping("/login")
    fun showLoginPage(
        @RequestParam(required = false) error: Boolean?,
        model: Model
    ): String {
        if (error == true) {
            model.addAttribute("errorMessage", "Invalid email or password.")
        }
        model.addAttribute("title", "Login or Sign Up")
        return "login"
    }

    @PostMapping("/signup")
    @ResponseBody
    fun registerUser(
        @RequestParam username: String,
        @RequestParam email: String,
        @RequestParam password: String
    ): ResponseEntity<String> {
        println("Received signup data - Username: $username, Email: $email, Password: $password")
        return try {
            userService.register(username, email, password)
            ResponseEntity.ok("User registered successfully!")
        } catch (e: IllegalArgumentException) {
            println("Error during registration: ${e.message}")
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping("/login")
    @ResponseBody // JSON 응답을 반환
    fun loginUser(
        @RequestParam email: String,
        @RequestParam password: String,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val user = userRepository.findByEmail(email)
            ?: return ResponseEntity.badRequest().body("User not found")

        return if (password==user.password) { // 비밀번호 검증
            val session: HttpSession = request.session
            session.setAttribute("userEmail", email) // 세션에 사용자 이메일 저장
            ResponseEntity.ok("Login successful")
        } else {
            ResponseEntity.badRequest().body("Invalid password")
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
