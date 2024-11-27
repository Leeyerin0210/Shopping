package com.shopping.site.Service

import com.shopping.site.dataClass.Order
import com.shopping.site.dataClass.OrderItem
import com.shopping.site.dataClass.User
import com.shopping.site.repository.CartRepository
import com.shopping.site.repository.OrderItemRepository
import com.shopping.site.repository.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val cartRepository: CartRepository
) {

    @Transactional
    fun createOrder(userEmail: String, shippingAddressId: Long): Order {
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
}
