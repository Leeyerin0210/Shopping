package com.shopping.site.controller

import com.shopping.site.Service.CartService
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

@Controller
@RequestMapping("/cart")
class CartController(
    private val cartService: CartService
) {

    @GetMapping
    fun getCart(request: HttpServletRequest, model: Model): String {
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: return "redirect:/login"

        val cartItems = cartService.getCartItems(userEmail)
        val shipping = BigDecimal("20.00") // shipping을 BigDecimal로 선언
        val subtotal = cartItems.sumOf { it.product.price * it.quantity.toBigDecimal() }
        val total = subtotal + shipping


        model.addAttribute("cartItems", cartItems)
        model.addAttribute("subtotal", subtotal)
        model.addAttribute("shipping", shipping)
        model.addAttribute("total", total)

        return "cart" // Mustache 템플릿 이름
    }

    @PostMapping("/remove")
    fun removeItem(
        @RequestBody body: Map<String, Any>, // JSON 데이터를 매핑
        request: HttpServletRequest
    ): String {
        val productId = body["productId"]?.toString()?.toLongOrNull() // 문자열로 변환 후 Long 변환 시도
            ?: throw IllegalArgumentException("Missing or invalid productId") // 유효하지 않으면 예외 발생
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: return "redirect:/login"

        println("Removing item for userEmail=$userEmail, productId=$productId")
        cartService.removeFromCart(userEmail, productId)
        return "redirect:/cart"
    }

}
