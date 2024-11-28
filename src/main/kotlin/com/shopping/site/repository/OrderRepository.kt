package com.shopping.site.repository

import com.shopping.site.dataClass.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrdersRepository : JpaRepository<Order, Long> {
    fun findAllByUser_Email(userEmail: String): List<Order>
    fun findByOrderIdAndUser_Email(orderId: Long, userEmail: String): Order?
}
