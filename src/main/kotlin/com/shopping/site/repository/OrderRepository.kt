package com.shopping.site.repository

import com.shopping.site.dataClass.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long> {
    // 특정 사용자 이메일로 주문 목록을 조회
    fun findAllByUser_Email(userEmail: String): List<Order>
}
