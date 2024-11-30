package com.shopping.site.Service

import com.shopping.site.dataClass.Order
import com.shopping.site.dataClass.OrderItem
import com.shopping.site.dataClass.User
import com.shopping.site.repository.CartRepository
import com.shopping.site.repository.CouponRepository
import com.shopping.site.repository.OrderItemsRepository
import com.shopping.site.repository.OrdersRepository
import com.shopping.site.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
@Service
class OrderService(
    private val orderRepository: OrdersRepository,
    private val orderItemRepository: OrderItemsRepository,
    private val cartRepository: CartRepository,
    private val productService: ProductService, // ProductService 주입
    private val userRepository: UserRepository,
    private val couponRepository: CouponRepository// UserRepository 주입
) {
    // 모든 주문 가져오기
    fun getAllOrders(): List<Order> {
        return orderRepository.findAll()
    }
    @Transactional
    fun deleteOrder(orderId: Long) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order not found") }

        if (order.status != "Pending") {
            throw IllegalStateException("Only pending orders can be canceled.")
        }

        orderRepository.delete(order)
    }


    @Transactional
    fun createOrder(userEmail: String, usedPoints: Int, couponId: Long?): Order {
        val user = userRepository.findByEmail(userEmail)
            ?: throw IllegalArgumentException("User not found")

        val cartItems = cartRepository.findAllByUser_Email(userEmail)
        if (cartItems.isEmpty()) throw IllegalArgumentException("Cart is empty")

        // 총 가격 계산
        val subtotal = cartItems.sumOf { it.product.price * it.quantity.toBigDecimal() }
        val shipping = BigDecimal("4000")
        var totalPrice = subtotal + shipping

        // 적립금 차감
        if (usedPoints > 0) {
            if (usedPoints > user.balance) throw IllegalArgumentException("Insufficient points")
            totalPrice -= usedPoints.toBigDecimal()
        }

        // 쿠폰 할인 적용
        if (couponId != null) {
            val coupon = couponRepository.findById(couponId)
                .orElseThrow { IllegalArgumentException("Invalid coupon") }
            if (coupon.user?.email != userEmail) throw IllegalArgumentException("Coupon does not belong to the user")
            totalPrice -= coupon.discount.toBigDecimal()
        }

        if (totalPrice < BigDecimal.ZERO) totalPrice = BigDecimal.ZERO

        // 주문 생성
        val order = Order(
            user = user,
            totalPrice = totalPrice,
            status = "Pending"
        )
        orderRepository.save(order)

        // 주문 항목 저장
        cartItems.forEach { cartItem ->
            val orderItem = OrderItem(
                order = order,
                product = cartItem.product,
                quantity = cartItem.quantity,
                price = cartItem.product.price
            )
            orderItemRepository.save(orderItem)

            // 재고 감소
            productService.reduceStock(cartItem.product.id, cartItem.quantity)
        }

        // 적립금 차감 및 적립
        user.balance -= usedPoints
        user.balance += (totalPrice * BigDecimal("0.1")).toInt() // 총 금액의 10% 적립
        userRepository.save(user)

        // 장바구니 비우기
        cartRepository.deleteAllByUser_Email(userEmail)

        return order
    }

    fun getOrderList(userEmail: String): List<Order> {
        return orderRepository.findAllByUser_Email(userEmail)
    }

    fun getOrderDetailsWithItems(orderId: Long): Pair<Order, List<OrderItem>> {
        val order = orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order not found") }
        val items = orderItemRepository.findByOrder(order)
        return Pair(order, items)
    }
    @Transactional
    fun markAsShipped(orderId: Long) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order not found") }

        if (order.status == "결제완료") {
            val updatedOrder = order.copy(status = "배송완료")
            orderRepository.save(updatedOrder)
        } else {
            throw IllegalStateException("Only paid orders can be marked as shipped.")
        }
    }
    @Transactional
    fun requestRefund(orderId: Long) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order not found") }

        if (order.status == "배송완료") { // 환불 신청은 배송 완료 상태에서만 가능
            val updatedOrder = order.copy(status = "환불신청")
            orderRepository.save(updatedOrder)
        } else {
            throw IllegalStateException("Refund request can only be made for completed deliveries.")
        }
    }
    fun getOrderDetails(userEmail: String, orderId: Long): Map<String, Any> {
        val order = orderRepository.findByOrderIdAndUser_Email(orderId, userEmail)
            ?: throw IllegalArgumentException("Order not found")
        val orderItems = orderItemRepository.findAllByOrder_OrderId(orderId)
        return mapOf("order" to order, "items" to orderItems)
    }

    @Transactional
    fun updateOrderStatusToPaid(orderId: Long) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order not found") }

        if (order.status == "Pending") {
            val orderItems = orderItemRepository.findByOrder(order)

            // 제품 재고 감소
            orderItems.forEach { item ->
                productService.reduceStock(item.product.id, item.quantity)
            }

            val updatedOrder = order.copy(status = "결제완료")
            orderRepository.save(updatedOrder)
        } else {
            throw IllegalStateException("Order is already paid or canceled.")
        }
    }

    @Transactional
    fun cancelPayment(orderId: Long) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order not found") }

        if (order.status == "결제완료") {
            val orderItems = orderItemRepository.findByOrder(order)

            // 제품 재고 복구
            orderItems.forEach { item ->
                productService.increaseStock(item.product.id, item.quantity)
            }

            val updatedOrder = order.copy(status = "결제취소")
            orderRepository.save(updatedOrder)
        } else {
            throw IllegalStateException("Only completed payments can be canceled.")
        }
    }

    // 환불 승인
    @Transactional
    fun approveRefund(orderId: Long) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order not found") }

        if (order.status != "환불신청") throw IllegalStateException("Order not eligible for refund approval")

        val orderItems = orderItemRepository.findByOrder(order)

        // 제품 재고 복구
        orderItems.forEach { item ->
            productService.increaseStock(item.product.id, item.quantity)
        }

        val updatedOrder = order.copy(status = "반품승인")
        orderRepository.save(updatedOrder)
    }
}
