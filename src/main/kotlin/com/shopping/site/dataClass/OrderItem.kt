package com.shopping.site.dataClass

import java.math.BigDecimal
import jakarta.persistence.*

@Entity
@Table(name = "ORDER_ITEMS")
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null, // 자동 생성되는 ID

    @Column(name = "ORDER_ID", nullable = false)
    val orderId: Long, // ORDERS 테이블의 ID를 참조

    @Column(name = "PRODUCT_ID", nullable = false)
    val productId: Long, // PRODUCT 테이블의 ID를 참조

    @Column(name = "QUANTITY", nullable = false)
    val quantity: Int, // 주문한 제품의 수량

    @Column(name = "PRICE", nullable = false, precision = 10, scale = 2)
    val price: BigDecimal // 제품의 개별 가격
) {
    // JPA를 위한 기본 생성자 (빈 생성자)
    protected constructor() : this(
        id = null,
        orderId = 0L,
        productId = 0L,
        quantity = 0,
        price = BigDecimal.ZERO
    )
}