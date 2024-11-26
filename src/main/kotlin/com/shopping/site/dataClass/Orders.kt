package com.shopping.site

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity

data class Orders(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long = 0,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product? = null,

    @Column(nullable = false)
    val quantity: Int = 0,

    // 타입을 BigDecimal로 변경
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    val totalPrice: BigDecimal = BigDecimal.ZERO,

    @Column(name = "order_date", nullable = false)
    val orderDate: LocalDateTime = LocalDateTime.now()
) {
    constructor() : this(
        id = 0,
        userId = 0,
        product = null,
        quantity = 0,
        totalPrice = BigDecimal.ZERO,
        orderDate = LocalDateTime.now()
    )
}
