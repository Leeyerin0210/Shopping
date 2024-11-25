package com.shopping.site

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class WishlistCartController(
    private val wishlistService: WishlistService,
    private val cartService: CartService
) {

    @PostMapping("/wishlist/add")
    fun addToWishlist(
        @RequestBody requestBody: Map<String, Long>,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val productId = requestBody["productId"] ?: return ResponseEntity.badRequest().body("제품 ID가 누락되었습니다.")
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).body("로그인이 필요합니다.")

        wishlistService.addToWishlist(userEmail, productId)
        return ResponseEntity.ok("위시리스트에 추가되었습니다.")
    }

    @PostMapping("/cart/add")
    fun addToCart(
        @RequestBody requestBody: Map<String, Long>,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val productId = requestBody["productId"] ?: return ResponseEntity.badRequest().body("제품 ID가 누락되었습니다.")
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).body("로그인이 필요합니다.")

        cartService.addToCart(userEmail, productId, quantity = 1)
        return ResponseEntity.ok("장바구니에 추가되었습니다.")
    }
}
