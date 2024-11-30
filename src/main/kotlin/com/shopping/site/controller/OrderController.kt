package com.shopping.site.controller

import com.shopping.site.Service.OrderService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping("/checkout")
    fun createOrder(
        @RequestBody body: Map<String, Any>,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, String>> {
        // 사용자 이메일 확인
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).body(mapOf("message" to "로그인이 필요합니다."))

        // 요청 본문에서 적립금 및 쿠폰 ID 확인
        val usedPoints = body["points"]?.toString()?.toIntOrNull() ?: 0
        val couponId = body["couponId"]?.toString()?.toLongOrNull()
        println("usedPoints : $usedPoints")
        println("couponID : $couponId")
        try {
            // 주문 생성
            orderService.createOrder(userEmail, usedPoints, couponId)
            return ResponseEntity.ok(mapOf("message" to "주문이 완료되었습니다.", "redirectUrl" to "/orders"))
        } catch (e: Exception) {
            return ResponseEntity.status(400).body(mapOf("message" to "주문에 실패했습니다."))
        }
    }

    @PostMapping("/{orderId}/pay")
    @ResponseBody
    fun makePayment(@PathVariable orderId: Long): Map<String, Any> {
        return try {
            orderService.updateOrderStatusToPaid(orderId)

            mapOf("success" to true, "message" to "Payment successful!")
        } catch (e: Exception) {
            mapOf("success" to false, "message" to (e.message ?: "An error occurred"))
        }
    }

    @PostMapping("/{orderId}/delete")
    @ResponseBody
    fun deleteOrder(@PathVariable orderId: Long): Map<String, Any> {
        return try {
            orderService.deleteOrder(orderId)
            mapOf("success" to true, "message" to "Order canceled and removed successfully.")
        } catch (e: Exception) {
            mapOf("success" to false, "message" to (e.message ?: "An error occurred"))
        }
    }

    @PostMapping("/{orderId}/cancel-payment")
    @ResponseBody
    fun cancelPayment(@PathVariable orderId: Long): Map<String, Any> {
        return try {
            orderService.cancelPayment(orderId)
            mapOf("success" to true, "message" to "Payment has been canceled.")
        } catch (e: Exception) {
            mapOf("success" to false, "message" to (e.message ?: "An error occurred"))
        }
    }
    @PostMapping("/{orderId}/cancel")
    @ResponseBody
    fun cancelOrder(@PathVariable orderId: Long): Map<String, Any> {
        return try {
            orderService.deleteOrder(orderId) // 서비스 호출
            mapOf("success" to true, "message" to "Order canceled successfully.")
        } catch (e: IllegalStateException) {
            mapOf("success" to false, "message" to "Cannot cancel the order.")
        } catch (e: Exception) {
            mapOf("success" to false, "message" to "An error occurred while canceling the order.")
        }
    }



    @GetMapping("/{orderId}")
    fun getOrderDetails(
        @PathVariable orderId: Long,
        request: HttpServletRequest,
        model: Model
    ): String {
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: return "redirect:/login"

        val (order, items) = orderService.getOrderDetailsWithItems(orderId)

        // 주문 상태에 따른 버튼 설정
        val paymentUrl = if (order.status == "Pending") "/orders/$orderId/pay" else null
        val additionalButtonLabel = when (order.status) {
            "Pending" -> "Cancel Order"
            "결제완료" -> "Cancel Payment"
            "배송완료" -> "Request Refund"
            else -> null
        }
        val additionalButtonUrl = when (order.status) {
            "Pending" -> "/orders/$orderId/cancel"
            "결제완료" -> "/orders/$orderId/cancel-payment"
            "배송완료" -> "/orders/$orderId/request-refund"
            else -> null
        }

        // Model에 데이터 추가
        model.addAttribute("order", order)
        model.addAttribute("items", items)
        model.addAttribute("paymentUrl", paymentUrl)
        model.addAttribute("additionalButtonLabel", additionalButtonLabel)
        model.addAttribute("additionalButtonUrl", additionalButtonUrl)

        return "order-details"
    }




    @GetMapping
    fun getOrderList(request: HttpServletRequest, model: Model): String {
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?:  run {
                // 세션에 현재 URL 저장
                request.session.setAttribute("redirectAfterLogin", "/admin/orders")
                return "redirect:/login"
            }
        val orders = orderService.getOrderList(userEmail)
        model.addAttribute("orders", orders)
        return "orders"
    }


    @PostMapping("/{orderId}/request-refund")
    @ResponseBody
    fun requestRefund(@PathVariable orderId: Long): Map<String, Any> {
        return try {
            orderService.requestRefund(orderId)
            mapOf("success" to true, "message" to "Refund request submitted successfully.")
        } catch (e: Exception) {
            mapOf("success" to false, "message" to (e.message ?: "An error occurred"))
        }
    }

}
