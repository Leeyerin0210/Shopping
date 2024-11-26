package com.shopping.site.dataClass


import jakarta.persistence.*

@Entity
data class Wishlist(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    val user: User,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product
) {
    constructor() : this(
        id = null,
        user = User(),
        product = Product()
    )
}
