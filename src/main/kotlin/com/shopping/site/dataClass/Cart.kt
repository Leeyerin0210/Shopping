package com.shopping.site.dataClass

import jakarta.persistence.*

@Entity
@Table(name = "CART")
data class Cart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @Column(nullable = false)
    var quantity: Int = 0
) {
    constructor() : this(
        id = 0,
        user = User(),
        product = Product(),
        quantity = 0
    )
}
