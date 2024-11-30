package com.shopping.site.dataClass

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "Coupons")
data class Coupon(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String = "",

    @Column(nullable = false)
    val discount: Double = 0.0,

    @Column(name = "expiry_date", nullable = false)
    val expiryDate: LocalDate = LocalDate.now(),

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    val user: User? = null
) {
    constructor() : this(
        id = 0,
        name = "",
        discount = 0.0,
        expiryDate = LocalDate.now(),
        user = null
    )

}
