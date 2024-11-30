package com.shopping.site.controller

import com.shopping.site.Service.CouponService
import com.shopping.site.dataClass.Coupon
import com.shopping.site.dto.CouponRequest
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Controller
@RequestMapping("/coupons")
class CouponController(
    private val couponService: CouponService
) {

    // 사용자 전용 쿠폰 페이지
    @GetMapping
    fun getUserCoupons(request: HttpServletRequest, model: Model): String {
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: run {
                request.session.setAttribute("redirectAfterLogin", "/coupons")
                return "redirect:/login"
            }

        val coupons = couponService.getCouponsByUser(userEmail)
        model.addAttribute("coupons", coupons)
        return "user-coupons"
    }

    // 관리자용 쿠폰 목록 조회
    @GetMapping("/all")
    fun getAllCoupons(): ResponseEntity<List<Coupon>> {
        val coupons = couponService.getAllCoupons()
        return ResponseEntity.ok(coupons)
    }

    // 쿠폰 발급
    @PostMapping("/add")
    fun addCoupon(@RequestBody request: CouponRequest): ResponseEntity<Any> {
        return try {
            val coupon = couponService.createCoupon(request)
            ResponseEntity.ok(coupon)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to e.message))
        }
    }

    // 쿠폰 삭제
    @DeleteMapping("/delete/{id}")
    fun deleteCoupon(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            couponService.deleteCoupon(id)
            ResponseEntity.ok(mapOf("message" to "Coupon deleted successfully"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to e.message))
        }
    }
}
