package com.shopping.site.controller

import com.shopping.site.Service.CouponService
import com.shopping.site.Service.OrderService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/admin")
class AdminController(private val couponService: CouponService){

    // AdminOrderController 수정
    @GetMapping
    fun goAdmin(request: HttpServletRequest, model: Model): String {
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: run {
                // 세션에 현재 URL 저장
                request.session.setAttribute("redirectAfterLogin", "/admin")
                return "redirect:/login"
            }

        return "admin"
    }

    @GetMapping("/coupons")
    fun showCouponManagementPage(model: Model): String {
        val allCoupons = couponService.getAllCoupons()
        model.addAttribute("coupons", allCoupons) // coupons 변수로 데이터를 템플릿에 전달
        return "admin-coupons"
    }


}
