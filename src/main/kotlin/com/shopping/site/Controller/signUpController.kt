package com.shopping.site.Controller
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.validation.BindingResult
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class SignupController {

    // 회원가입 페이지 렌더링
    @GetMapping("/signup")
    fun showSignupForm(model: Model): String {
        model.addAttribute("title", "회원가입")
        return "signup" // Mustache 템플릿 이름 (src/main/resources/templates/signup.mustache)
    }

    // 회원가입 데이터 처리
    @PostMapping("/signup")
    fun handleSignup(
        @RequestParam("name") name: String,
        @RequestParam("nickname") nickname: String,
        @RequestParam("email") email: String,
        @RequestParam("address") address: String,
        @RequestParam("address2", required = false) address2: String?,
        @RequestParam("root", required = false) root: String?,
        @RequestParam("code", required = false) code: String?,
        @RequestParam("aggrement", required = false) agreement: Boolean?,
        redirectAttributes: RedirectAttributes
    ): String {
        // 개인정보 수집 및 이용에 동의하지 않으면 다시 폼으로 리다이렉트
        if (agreement != true) {
            redirectAttributes.addFlashAttribute("error", "개인정보 수집 및 이용에 동의해야 합니다.")
            return "redirect:/signup"
        }

        // 데이터 저장 (여기서는 간단히 출력으로 대체)
        println("회원가입 정보:")
        println("이름: $name, 별명: $nickname, 이메일: $email, 주소: $address $address2, 가입경로: $root, 추천인 코드: $code")

        // 가입 완료 후 리다이렉트
        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다!")
        return "redirect:/signup-success"
    }

    // 가입 성공 페이지
    @GetMapping("/signup-success")
    fun showSuccessPage(model: Model): String {
        model.addAttribute("title", "가입 완료")
        return "signup-success" // Mustache 템플릿 이름 (src/main/resources/templates/signup-success.mustache)
    }
}
