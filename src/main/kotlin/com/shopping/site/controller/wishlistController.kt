package com.shopping.site.controller

import com.shopping.site.Service.WishlistService
import com.shopping.site.repository.WishlistRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*


    @Controller
    @RequestMapping("/wishlist")
    class WishlistController(
        private val wishlistService: WishlistService
    ) {

        // 위시리스트 페이지 렌더링
        @GetMapping
        fun showWishlist(request: HttpServletRequest, model: Model): String {
            val userEmail = request.session.getAttribute("userEmail") as? String
                ?: run {
                    // 세션에 현재 URL 저장
                    request.session.setAttribute("redirectAfterLogin", "/wishlist")
                    return "redirect:/login"
                }
            val wishlist = wishlistService.getWishlist(userEmail)
            model.addAttribute("wishlist", wishlist)
            return "wishlist"
        }

        // 위시리스트에서 제품 삭제
        @PostMapping("/delete")
        @ResponseBody
        fun removeFromWishlist(
            @RequestBody requestBody: Map<String, Long>,
            request: HttpServletRequest
        ): ResponseEntity<String> {
            val userEmail = request.session.getAttribute("userEmail") as? String
                ?: return ResponseEntity.status(401).body("로그인이 필요합니다.")

            val productId = requestBody["productId"] ?: return ResponseEntity.badRequest().body("제품 ID가 누락되었습니다.")

            return if (wishlistService.removeFromWishlist(userEmail, productId)) {
                ResponseEntity.ok("위시리스트에서 삭제되었습니다.")
            } else {
                ResponseEntity.badRequest().body("삭제할 항목을 찾을 수 없습니다.")
            }
        }
    }


