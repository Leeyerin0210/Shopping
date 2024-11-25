package com.shopping.site

import jakarta.persistence.*

@Entity
@Table(name = "reviewdata")
data class ReviewData(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, columnDefinition = "TEXT")
    val review: String = "",

    @Column(name = "user_name", length = 50)
    val userName: String? = null,

    @Column
    val rating: Int = 0,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product? = null
) {
    // 기본 생성자
    constructor() : this(
        id = 0,
        review = "",
        userName = null,
        rating = 0,
        product = null
    )
}
