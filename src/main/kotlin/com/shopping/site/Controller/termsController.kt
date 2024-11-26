package com.shopping.site.Controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class TermsController {

    // 약관 페이지 렌더링
    @GetMapping("/terms")
    fun showTermsPage(model: Model): String {
        model.addAttribute("title", "약관 동의")
        return "terms" // Mustache 템플릿 이름 (src/main/resources/templates/terms.mustache)
    }

    // 약관 동의 처리
    @PostMapping("/terms")
    fun handleTermsAgreement(
        @RequestParam("agree", required = false) agree: Boolean
    ): String {
        return if (agree) {
            "redirect:/signup" // 약관 동의 후 다음 페이지로 리다이렉트
        } else {
            "redirect:/terms?error=1" // 동의하지 않으면 다시 약관 페이지로 리다이렉트
        }
    }
}
