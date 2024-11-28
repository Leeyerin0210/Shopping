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
            ?: run {
                // 세션에 현재 URL 저장
                request.session.setAttribute("redirectAfterLogin", "/admin/orders")
                return "redirect:/login"
            }
        val cartItems = cartService.getCartItems(userEmail)
        val subtotal = cartService.calculateSubtotal(cartItems)
        val shipping = BigDecimal("4000")
        val total = subtotal + shipping

        model.addAttribute("cartItems", cartItems)
        model.addAttribute("subtotal", subtotal)
        model.addAttribute("shipping", shipping)
        model.addAttribute("total", total)

        return "cart"
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

        cartService.updateCartItemQuantity(userEmail, productId, quantity)
        return ResponseEntity.ok(mapOf("message" to "Quantity updated successfully"))
    }
}
