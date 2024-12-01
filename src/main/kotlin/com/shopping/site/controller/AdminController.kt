package com.shopping.site.controller


import com.shopping.site.Service.CouponService
import com.shopping.site.Service.SupportTicketService
import com.shopping.site.Service.AdminService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin")
class AdminController(
    private val adminService: AdminService,
    private val couponService: CouponService,
    private val supportTicketService: SupportTicketService
) {
    // 어드민 로그인 페이지
    @GetMapping("/login")
    fun showAdminLoginPage(): String {
        return "adminlogin" // 어드민 로그인 템플릿
    }

    // 어드민 로그인 처리
    @PostMapping("/login")
    fun handleAdminLogin(
        @RequestParam email: String,
        @RequestParam password: String,
        request: HttpServletRequest
    ): String {
        val admin = adminService.login(email, password)
        return if (admin != null) {
            val session: HttpSession = request.session
            session.setAttribute("adminEmail", admin.email)
            "redirect:/admin" // 로그인 성공 시 어드민 대시보드로 리디렉션
        } else {
            "redirect:/admin/login?error=invalid" // 로그인 실패 시 에러 메시지 표시
        }
    }

    // 어드민 대시보드 접근
    @GetMapping
    fun goAdmin(request: HttpServletRequest, model: Model): String {
        val adminEmail = request.session.getAttribute("adminEmail") as? String
            ?: run {
                request.session.setAttribute("redirectAfterLogin", "/admin")
                return "redirect:/admin/login"
            }

        return "admin"
    }

    // 쿠폰 관리 페이지
    @GetMapping("/coupons")
    fun showCouponManagementPage(request: HttpServletRequest, model: Model): String {
        val adminEmail = request.session.getAttribute("adminEmail") as? String
            ?: return "redirect:/admin/login"

        val allCoupons = couponService.getAllCoupons()
        model.addAttribute("coupons", allCoupons)
        return "admin-coupons"
    }

    // 문의사항 관리 페이지
    @GetMapping("/support")
    fun getAllTickets(request: HttpServletRequest, model: Model): String {
        val adminEmail = request.session.getAttribute("adminEmail") as? String
            ?: return "redirect:/admin/login"

        val tickets = supportTicketService.getAllTickets()
        model.addAttribute("tickets", tickets)
        return "admin-support"
    }

    // 어드민 로그아웃
    @GetMapping("/logout")
    fun logoutAdmin(request: HttpServletRequest): String {
        request.session.invalidate() // 세션 초기화
        return "redirect:/admin/login"
    }

    @PostMapping("/support/reply/{id}")
    @ResponseBody
    fun addReply(
        @PathVariable id: Long,
        @RequestBody replyContent: Map<String, String>
    ): ResponseEntity<String> {
        val content = replyContent["content"] ?: return ResponseEntity.badRequest().body("답변 내용이 필요합니다.")
        supportTicketService.addReply(id, content)
        return ResponseEntity.ok("답변이 저장되었습니다.")
    }

}
