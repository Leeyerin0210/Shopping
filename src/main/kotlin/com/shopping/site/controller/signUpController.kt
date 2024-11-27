package com.shopping.site.controller

import com.shopping.site.repository.UserRepository
import com.shopping.site.dataClass.User
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class SignupController @Autowired constructor(
    private val userRepository: UserRepository
)  {
    // 회원가입 화면 - GET 요청 매핑 추가
    @GetMapping("/signup")
    fun showSignupForm(model: Model): String {
        // 오류 메시지 초기화
        model.addAttribute("error", null)
        return "signup" // signup.mustache 템플릿 렌더링
    }
    @PostMapping("/signup")
    fun handleSignup(
        @RequestParam("name") name: String,
        @RequestParam("nickname") nickname: String,
        @RequestParam("email") email: String,
        @RequestParam("password") password: String,
        @RequestParam("address") address: String,
        @RequestParam("agreement", required = false) agreement: Boolean?,
        redirectAttributes: RedirectAttributes
    ): String {
        if (agreement != true) {
            redirectAttributes.addFlashAttribute("error", "개인정보 수집 및 이용에 동의해야 합니다.")
            return "redirect:/signup"
        }

        if (password.length < 8) {
            redirectAttributes.addFlashAttribute("error", "비밀번호는 최소 8자 이상이어야 합니다.")
            return "redirect:/signup"
        }

        if (userRepository.findByEmail(email) != null) {
            redirectAttributes.addFlashAttribute("error", "이미 사용 중인 이메일입니다.")
            return "redirect:/signup"
        }

        val user = User(
            username = name,
            nickname = nickname,
            email = email,
            password = password,
            address = address
        )
        userRepository.save(user)

        // 회원가입 성공 메시지 설정
        redirectAttributes.addFlashAttribute("success", "회원가입이 완료되었습니다!")
        return "redirect:/login"
    }
    // 가입 성공 페이지 (옵션)
    @GetMapping("/signup-success")
    fun showSuccessPage(model: Model): String {
        model.addAttribute("title", "가입 완료")
        return "signup-success" // Mustache 템플릿 이름
    }
}
