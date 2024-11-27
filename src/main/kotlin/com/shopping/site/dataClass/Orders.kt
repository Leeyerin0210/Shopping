package com.shopping.site.dataClass

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val orderId: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    val user: User,

    val orderDate: LocalDateTime = LocalDateTime.now(),
    val totalPrice: BigDecimal,
    val status: String = "Pending"
)
{
    constructor() : this(
        orderId = 0,
        user = User(),
        totalPrice = BigDecimal.ZERO,
        orderDate = LocalDateTime.now(),
        status = "Pending"
    )
}
