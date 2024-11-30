package com.shopping.site.controller

import com.shopping.site.Service.CartService
import com.shopping.site.repository.CouponRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDate

@Controller
@RequestMapping("/cart")
class CartController(
    private val cartService: CartService,
    private val couponRepository: CouponRepository,
) {

    @GetMapping
    fun getCart(request: HttpServletRequest, model: Model): String {
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: run {
                // 세션에 현재 URL 저장
                request.session.setAttribute("redirectAfterLogin", "/cart")
                return "redirect:/login"
            }
        val cartItems = cartService.getCartItems(userEmail)
        val subtotal = cartService.calculateSubtotal(cartItems)
        val shipping = BigDecimal("4000")
        val total = subtotal + shipping
        val coupons = couponRepository.findByUserEmail(userEmail)
        model.addAttribute("cartItems", cartItems)
        model.addAttribute("subtotal", subtotal)
        model.addAttribute("shipping", shipping)
        model.addAttribute("total", total)
        model.addAttribute("coupons",coupons)

        return "cart"
    }
    @PostMapping("/apply-discounts")
    fun applyDiscounts(
        @RequestBody body: Map<String, Any>,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).body(mapOf("message" to "로그인이 필요합니다."))

        val points = body["points"]?.toString()?.toIntOrNull() ?: 0
        val couponId = body["couponId"]?.toString()?.toLongOrNull()

        try {
            // 할인된 총액 계산
            val discountedTotal = cartService.calculateDiscountedTotal(userEmail, points, couponId)

            // 쿠폰 할인 계산
            val couponDiscount = if (couponId != null) {
                val coupon = couponRepository.findById(couponId)
                    .orElseThrow { IllegalArgumentException("Invalid coupon ID") }
                if (coupon.user?.email != userEmail) throw IllegalArgumentException("Coupon does not belong to the user")
                if (coupon.expiryDate.isBefore(LocalDate.now())) throw IllegalArgumentException("Coupon has expired")

                val couponDiscount = coupon.discount
            } else {
                BigDecimal.ZERO
            }

            return ResponseEntity.ok(
                mapOf(
                    "message" to "Discounts applied successfully!",
                    "usedPoints" to points,
                    "couponDiscount" to couponDiscount,
                    "discountedTotal" to discountedTotal
                )
            )
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(mapOf("message" to e.message)) as ResponseEntity<Map<String, Any>>
        }
    }


    @PostMapping("/remove")
    fun removeItem(
        @RequestBody body: Map<String, Any>,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        val productId = body["productId"]?.toString()?.toLongOrNull()
            ?: throw IllegalArgumentException("Missing or invalid productId")
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: return ResponseEntity.badRequest().body(mapOf("message" to "User not logged in"))

        cartService.removeFromCart(userEmail, productId)
        return ResponseEntity.ok(mapOf("message" to "Item removed successfully"))
    }
    @PostMapping("/update")
    fun updateItemQuantity(
        @RequestBody body: Map<String, Any>,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        val productId = body["productId"]?.toString()?.toLongOrNull()
            ?: throw IllegalArgumentException("Missing or invalid productId")
        val quantity = body["quantity"]?.toString()?.toIntOrNull()
            ?: throw IllegalArgumentException("Missing or invalid quantity")
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: return ResponseEntity.badRequest().body(mapOf("message" to "User not logged in"))

        // 수량 업데이트
        cartService.updateCartItemQuantity(userEmail, productId, quantity)

        // 변경된 가격 정보 가져오기
        val updatedCartItems = cartService.getCartItems(userEmail)
        val updatedSubtotal = cartService.calculateSubtotal(updatedCartItems)
        val shipping = BigDecimal("4000")
        val updatedTotal = updatedSubtotal + shipping

        // 업데이트된 상품의 총 가격 계산
        val updatedProductPrice = updatedCartItems.find { it.product.id == productId }?.let {
            it.product.price * BigDecimal(it.quantity)
        } ?: BigDecimal.ZERO

        // BigDecimal을 문자열로 변환하여 JSON 호환성 유지
        return ResponseEntity.ok(
            mapOf(
                "message" to "Quantity updated successfully",
                "subtotal" to updatedSubtotal.toPlainString(),
                "total" to updatedTotal.toPlainString(),
                "productPrice" to updatedProductPrice.toPlainString()
            )
        )
    }


}
