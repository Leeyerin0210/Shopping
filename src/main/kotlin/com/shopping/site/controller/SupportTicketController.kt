package com.shopping.site.controller

import com.shopping.site.Service.SupportTicketService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Controller
@RequestMapping("/support")
class SupportTicketController(
    private val ticketService: SupportTicketService
) {
    // 모든 티켓 조회
    @GetMapping("/all")
    fun getAllTickets(model: Model): String {
        val tickets = ticketService.getAllTickets().map { ticket ->
            val now = LocalDateTime.now()
            val isNew = ChronoUnit.DAYS.between(ticket.createdAt, now) < 1 // 1일 이내면 NEW 표시
            mapOf(
                "id" to ticket.id,
                "title" to ticket.title,
                "isSecret" to ticket.isSecret,
                "restrictedUrl" to "/support/${ticket.id}",
                "url" to "/support/${ticket.id}",
                "category" to ticket.category,
                "maskedUsername" to maskUsername(ticket.user.email),
                "createdAt" to ticket.createdAt.toString(),
                "isNew" to isNew,
                "hasReply" to ticket.hasReply,
                "reply" to ticket.reply
            )
        }
        model.addAttribute("tickets", tickets)
        return "SupportAll" // 모든 티켓을 보여줄 템플릿
    }

    // 작성 페이지 표시
    @GetMapping("/write")
    fun showWritePage(): String {
        return "write" // 작성 페이지 템플릿 이름
    }

    // 작성 처리
    @PostMapping("/create")
    fun createTicket(
        request: HttpServletRequest,
        @RequestParam title: String,
        @RequestParam message: String,
        @RequestParam category: String,
        @RequestParam(required = false, defaultValue = "false") isSecret: Boolean
    ): String {
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: run {
                request.session.setAttribute("redirectAfterLogin", "/support/write")
                return "redirect:/login"
            }

        ticketService.createTicket(userEmail, title, message, isSecret, category)
        return "redirect:/support/all" // 작성 후 전체 페이지로 리디렉션
    }

    // 사용자 이메일 마스킹
    private fun maskUsername(email: String): String {
        val atIndex = email.indexOf('@')
        return if (atIndex > 1) email.replaceRange(1, atIndex, "***") else email
    }
    // 상세 페이지
    @GetMapping("/{id}")
    fun getTicketDetails(@PathVariable id: Long, model: Model, request: HttpServletRequest): String {
        val ticket = ticketService.getTicketById(id)
        val userEmail = request.session.getAttribute("userEmail") as? String

        if (ticket.isSecret && ticket.user.email != userEmail) {
            return "redirect:/support/all?error=unauthorized"
        }

        model.addAttribute("id", ticket.id)
        model.addAttribute("title", ticket.title)
        model.addAttribute("maskedUsername", maskUsername(ticket.user.email))
        model.addAttribute("createdAt", ticket.createdAt)
        model.addAttribute("message", ticket.message)
        model.addAttribute("hasReply",ticket.hasReply)
        model.addAttribute("reply",ticket.reply)

        return "SupportDetails" // 상세 페이지 템플릿 이름
    }
    // 수정 페이지 표시
    @GetMapping("/edit/{id}")
    fun showEditPage(@PathVariable id: Long, model: Model, request: HttpServletRequest): String {
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: run {
                request.session.setAttribute("redirectAfterLogin", "/support/edit/$id")
                return "redirect:/login"
            }

        val ticket = ticketService.getTicketById(id)

        // 비밀글 여부와 작성자 확인
        if (ticket.isSecret && ticket.user.email != userEmail) {
            return "redirect:/support/all?error=unauthorized"
        }

        model.addAttribute("id", ticket.id)
        model.addAttribute("title", ticket.title)
        model.addAttribute("category", ticket.category)
        model.addAttribute("isDelivery", ticket.category == "배송문의")
        model.addAttribute("isExchange", ticket.category == "교환문의")
        model.addAttribute("isProduct", ticket.category == "상품문의")
        model.addAttribute("message", ticket.message)
        model.addAttribute("isSecret", ticket.isSecret)

        return "support-edit" // 수정 페이지 템플릿
    }

    // 수정 처리
    @PostMapping("/update/{id}")
    fun updateTicket(
        @PathVariable id: Long,
        @RequestParam title: String,
        @RequestParam category: String,
        @RequestParam message: String,
        @RequestParam(required = false, defaultValue = "false") isSecret: Boolean,
        request: HttpServletRequest
    ): String {
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: run {
                request.session.setAttribute("redirectAfterLogin", "/support/edit/$id")
                return "redirect:/login"
            }

        ticketService.updateTicket(id, title, category, message, isSecret, userEmail)
        return "redirect:/support/all" // 수정 후 목록 페이지로 리디렉션
    }
}
