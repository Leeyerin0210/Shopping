package com.shopping.site
import jakarta.persistence.*

import jakarta.persistence.*

@Entity
@Table(name = "Users")
data class User(
    @Id
    @Column(name = "email", nullable = false, unique = true)
    val email: String = "",

    val username: String = "",
    val password: String = "",

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val wishlist: List<Wishlist> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val cart: List<Cart> = mutableListOf()
) {
    constructor() : this(
        email = "",
        username = "",
        password = "",
        wishlist = mutableListOf(),
        cart = mutableListOf()
    )
}
