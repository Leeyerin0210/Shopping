package com.shopping.site.Service

import com.shopping.site.dataClass.Order
import com.shopping.site.dataClass.OrderItem
import com.shopping.site.dataClass.User
import com.shopping.site.repository.CartRepository
import com.shopping.site.repository.OrderItemsRepository
import com.shopping.site.repository.OrdersRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
@Service
class OrderService(
    private val orderRepository: OrdersRepository,
    private val orderItemRepository: OrderItemsRepository,
    private val cartRepository: CartRepository,
    private val productService: ProductService // ProductService 주입
) {

    @Transactional
    fun createOrder(userEmail: String): Order {
        val cartItems = cartRepository.findAllByUser_Email(userEmail)
        if (cartItems.isEmpty()) throw IllegalArgumentException("장바구니가 비어 있습니다.")

        val totalPrice = cartItems.sumOf { it.product.price * it.quantity.toBigDecimal() }

        val order = Order(
            user = User(email = userEmail),
            totalPrice = totalPrice,
            status = "Pending"
        )
        orderRepository.save(order)

        cartItems.forEach { cartItem ->
            val orderItem = OrderItem(
                order = order,
                product = cartItem.product,
                quantity = cartItem.quantity,
                price = cartItem.product.price
            )
            orderItemRepository.save(orderItem)
        }

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

    fun getOrderDetails(userEmail: String, orderId: Long): Map<String, Any> {
        val order = orderRepository.findByOrderIdAndUser_Email(orderId, userEmail)
            ?: throw IllegalArgumentException("해당 주문을 찾을 수 없습니다.")

        val orderItems = orderItemRepository.findAllByOrder_OrderId(orderId)

        return mapOf(
            "order" to order,
            "items" to orderItems
        )
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
    fun deleteOrder(orderId: Long) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order not found") }

        if (order.status != "Pending") {
            throw IllegalStateException("Only pending orders can be canceled.")
        }

        orderRepository.delete(order)
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

            val updatedOrder = order.copy(status = "결제취소") // 상태를 "결제취소"로 변경
            orderRepository.save(updatedOrder) // 상태 저장
        } else {
            throw IllegalStateException("Only completed payments can be canceled.")
        }
    }

    // 모든 주문 가져오기
    fun getAllOrders(): List<Order> {
        return orderRepository.findAll()
    }

    @Transactional
    fun approveRefund(orderId: Long) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order not found") }

        if (order.status != "환불신청") {
            throw IllegalStateException("Only orders with a refund request can be approved.")
        }

        val orderItems = orderItemRepository.findByOrder(order)

        // 제품 재고 복구
        orderItems.forEach { item ->
            productService.increaseStock(item.product.id, item.quantity)
        }

        val updatedOrder = order.copy(status = "반품승인")
        orderRepository.save(updatedOrder)
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
}
