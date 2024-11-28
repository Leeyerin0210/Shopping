package com.shopping.site.dataClass

import jakarta.persistence.*

@Entity
@Table(name = "reviewdata")
data class ReviewData(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, columnDefinition = "TEXT")
    val review: String = "",

    @Column
    val rating: Int = 0,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "email", nullable = false)
    val user: User? = null
)
{
    // 기본 생성자
    constructor() : this(
        id = 0,
        review = "",
        rating = 0,
        product = null
    )
}
