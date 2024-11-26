package com.shopping.site.dataClass

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity

data class Orders(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "email", nullable = false)
    val userId: Long = 0,

    @Column(name = "total_price", nullable = false)
    val totalPrice: BigDecimal = BigDecimal.ZERO,

    @Column(name = "order_date", nullable = false)
    val orderDate: LocalDateTime = LocalDateTime.now()
) {
    constructor() : this(
        id = 0,
        userId = 0,
        totalPrice = BigDecimal.ZERO,
        orderDate = LocalDateTime.now()
    )
}
