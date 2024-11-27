package com.shopping.site.controller

import com.shopping.site.Service.OrderService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/order")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping("/create")
    fun createOrder(
        @RequestBody requestBody: Map<String, Long>,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).body("로그인이 필요합니다.")

        val shippingAddressId = requestBody["shippingAddressId"]
            ?: return ResponseEntity.badRequest().body("배송 주소 ID가 누락되었습니다.")

        orderService.createOrder(userEmail, shippingAddressId)
        return ResponseEntity.ok("주문이 완료되었습니다.")
    }
}
