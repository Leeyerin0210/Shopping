package com.shopping.site.repository

import com.shopping.site.dataClass.Order
import com.shopping.site.dataClass.OrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemsRepository : JpaRepository<OrderItem, Long> {
    fun findAllByOrder_OrderId(orderId: Long): List<OrderItem>
    fun findByOrder(order: Order): List<OrderItem>
}