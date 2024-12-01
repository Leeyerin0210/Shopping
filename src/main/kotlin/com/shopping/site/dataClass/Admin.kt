package com.shopping.site.dataClass

import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
@Table(name = "ADMINS")
data class Admin(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val name: String,

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
