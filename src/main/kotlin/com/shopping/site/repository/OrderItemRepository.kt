package com.shopping.site.repository

import com.shopping.site.dataClass.OrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository : JpaRepository<OrderItem, Long> {
    // 특정 주문 ID로 주문 항목 조회
    fun findAllByOrder_OrderId(orderId: Long): List<OrderItem>
}
