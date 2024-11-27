package com.shopping.site.dataClass

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "cart")
data class Cart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val cartId: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    val user: User,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    var quantity: Int = 1,

    val addedAt: LocalDateTime = LocalDateTime.now()
)
 {
    constructor() : this(
        cartId = 0,
        user = User(),
        product = Product(),
        quantity = 0,
        addedAt = LocalDateTime.now()
    )
}
