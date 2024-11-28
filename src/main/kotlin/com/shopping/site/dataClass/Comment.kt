package com.shopping.site.dataClass

import jakarta.persistence.*

@Entity
@Table(name = "comments")
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val content: String = "",

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    val review: ReviewData? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "email", nullable = false)
    val user: User? = null
)
