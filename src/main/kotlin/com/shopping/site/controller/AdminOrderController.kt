package com.shopping.site.controller

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
@RequestMapping("/admin/orders")
class AdminOrderController(
    private val orderService: OrderService
) {

    // AdminOrderController 수정
    @GetMapping
    fun getAllOrders(request: HttpServletRequest, model: Model): String {
        val adminEmail = request.session.getAttribute("adminEmail") as? String
            ?: return "redirect:/admin/login"
        // 주문 목록을 가져오면서 상태별 버튼 표시 여부 추가
        val orders = orderService.getAllOrders().map { order ->
            mapOf(
                "orderId" to order.orderId,
                "user" to order.user.email,
                "status" to order.status,
                "totalPrice" to order.totalPrice,
                "canMarkAsShipped" to (order.status == "결제완료"),
                "canApproveRefund" to (order.status == "환불신청")
            )
        }

        model.addAttribute("orders", orders)
        return "admin-orders"
    }


    // 주문 취소
    @PostMapping("/{orderId}/cancel")
    fun cancelOrder(@PathVariable orderId: Long): String {
        orderService.deleteOrder(orderId)
        return "redirect:/admin/orders"
    }

    // 결제 취소
    @PostMapping("/{orderId}/cancel-payment")
    fun cancelPayment(@PathVariable orderId: Long): String {
        orderService.cancelPayment(orderId)
        return "redirect:/admin/orders"
    }

    @PostMapping("/{orderId}/approve-refund")
    @ResponseBody
    fun approveRefund(@PathVariable orderId: Long): Map<String, Any> {
        return try {
            orderService.approveRefund(orderId)
            mapOf("success" to true, "message" to "Refund approved successfully.")
        } catch (e: Exception) {
            mapOf("success" to false, "message" to (e.message ?: "An error occurred"))
        }
    }


    @PostMapping("/{orderId}/mark-shipped")
    @ResponseBody
    fun markAsShipped(@PathVariable orderId: Long): Map<String, Any> {
        return try {
            orderService.markAsShipped(orderId)
            mapOf("success" to true, "message" to "Order marked as shipped.")
        } catch (e: Exception) {
            mapOf("success" to false, "message" to (e.message ?: "An error occurred"))
        }
    }

}
